package client;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import util.ObservableArrayList;
import video.Client;
import video.Server;
/**
 * Classe que representa a conexão com outro cliente, independente
 * de qual das partes tenha iniciado a chamada. 
 * */
public class P2P extends Thread{
	
	private String remoteIP;
	private String remotePeerName;
	Socket tcpConnection; // TCP socket used for control
	
	
	Socket textSocket; // TCP socket to send and receive text messages
	BufferedWriter bufferedWriter; // Para envio de mensagem
	BufferedReader bufferedReader; // Paraa recepção de mensagem
	
	
	private int localRTSPPort;
	private int remoteRTSPPort;
	private int RTPPort;
	
	private int localMessagePort;
	private int remoteMessagePort;

	private int remotePort; 
	private int localPort;
	
	
	
	
	// Listeners de eventos
	private MessageListener messageListener;
	private PeerListener peerListener;
	
	boolean isConnected = false;
	List<Message> msgBuffer = new ObservableArrayList<Message>();
	
	/**
	 * Construtor usado quando se deseja um objeto para 
	 * RECEBER a conexão. Deve ser seguido por uma chamada
	 * a startBuffers(socket).
	 * */
	public P2P(PeerListener listener){
		this.peerListener = listener;
	}
	/**
	 * Called when the client want to contact the other
	 * */
	
	
	
	
	/**
	 * Construtor usado quando se deseja que o peer 
	 * inicie a comunicação com o outro lado.
	 * startBuffers() deve ser chamado sem parametros.
	 * */
	public P2P(String remoteIP, int remotePort, PeerListener listener){
		this.remoteIP = remoteIP;
		this.remotePort = remotePort;
		this.peerListener = listener;
	}
	
	/**
	 * Inicia os sockets para a comunicação texto.
	 * 
	 * */
	public void requestP2P(){
		ServerSocket server = null;
		if (!isConnected){
			try {
				server = new ServerSocket(0); // inicia socket para texto
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			localMessagePort = server.getLocalPort();
			localRTSPPort = localMessagePort + 2; // TODO tirar isso
			
			String wRequest = "SETUP\r\n";
			wRequest += "portext: " + String.valueOf(localMessagePort) + "\r\n";
			wRequest += "porRTSP: " + String.valueOf(localRTSPPort) + "\r\n";
			try {
				
				System.out.println("REMOTE IP:" + remoteIP + ", REMOTE PORT: " + remotePort + " ON REQUESTP2P");
				tcpConnection= new Socket(remoteIP, remotePort);
				BufferedWriter writer = new BufferedWriter( new OutputStreamWriter(tcpConnection.getOutputStream()));
				writer.append(wRequest);
				writer.flush();
				//startBuffers(tcpConnection);
				//sendMessage(wRequest);
				isConnected = true;
				//TODO SETAR ISCONNECTED PRA TRUE CASO OK
				//startTextAndVideoServers(); //dei certo, vou iniciar
				
			} catch (Exception e){
				e.printStackTrace();
				//endConnection();
			}
			
		}
		
		// Espera a conexão do cliente
		//Socket s;
		try {
			textSocket = server.accept();
			//textSocket = server.accept();
			startBuffers(textSocket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	
	public int getRemoteRTSPPort() {
		return remoteRTSPPort;
	}
	public void setRemoteRTSPPort(int remoteRTSPPort) {
		this.remoteRTSPPort = remoteRTSPPort;
	}
	
	private void startBuffers(Socket socket) throws IOException {
		bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()) );
		System.out.println("startBuffers() on client that ACCEPTED the call");
		peerListener.gotP2P(this);
		receiveMessageLoop();
	}
	
	
	/**
	 * Call this when you want to open the channel with the remote host.
	 * */
	public void startBuffers() throws IOException {
		
		textSocket = new Socket(this.remoteIP, this.remoteMessagePort);
		bufferedReader = new BufferedReader(new InputStreamReader(textSocket.getInputStream()));
		bufferedWriter = new BufferedWriter(new OutputStreamWriter(textSocket.getOutputStream()) );
		peerListener.gotP2P(this);
		System.out.println("startBuffers() on client that INITIATED the call");
		receiveMessageLoop();
//		if(bufferedWriter == null){ 
////			throw(new RuntimeException());
//			System.out.println("WRITER NUUULL DAMN IT FUCK BAINCA'S RUNTIME");
//			}
//		else{
//			System.out.println("WRITER NOT NULL DAMN IT");
//		}
//		if(bufferedReader == null){ 
////			throw(new RuntimeException());
//			System.out.println("READER NUUULL DAMN IT FUCK BAINCA'S RUNTIME");
//		}
//		else System.out.println("READER NOT NULL DAMN IT");
	}
	
/*
	public void gotP2P(String ip, int port){
		// TODO Auto-generated method stub
		System.out.println("gotp2p");
		try{
		Thread.sleep(1000); } catch(Exception e) {}
		this.ip = ip;
		this.port = port;
		new Thread("Client waiting for server thread"){
			public void run() {
				receiveP2P();
			}
		}.start();
	}
	
	
	public void gotP2P(String ip, int receiveRTSPPort, int sendRTSPPort, int RTPPort, int receiveMessagePort, int sendMessagePort) {

		
		this.receiveRTSPPort = receiveRTSPPort;
		this.sendRTSPPort = sendRTSPPort;
		
		this.RTPPort = RTPPort;
		
		this.receiveMessagePort = receiveMessagePort;
		this.sendMessagePort = sendMessagePort;

	}*/
	
	

	
	
	
	
	public void startTextAndVideoServers(){
		try {
			// TODO enviar vídeo
			sendVideo();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		receiveMessageLoop();
	}
	
	
	public void requestVideo(){ //request TO SEND a video

	}
	public void acceptVideo(){ //accept TO RECEIVE a video

	}
	public void receiveVideo(){
		new Thread ("Receive video thread") {
			public void run() {
				video.Client client = new video.Client();
				client.setIp(remoteIP);
				client.setRTSPPort(remoteRTSPPort);
				client.setRTPPort(RTPPort);
				try {
					client.start(); //nao pode ser start, tem que ser receive
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();

	}
	public void sendVideo() throws Exception{
		new Thread ("send video thread") {
			public void run() {
				int sessionID = 123456;
				ServerSocket generalSock;
				try {
					generalSock = new ServerSocket(localRTSPPort);
			        System.out.println("Esperando por cliente...");
			        Server server = new Server();
			        server.RTSP_ID = sessionID++;
		        
		       // server.serverSocket = generalSock;
				server.RTSPsocket = generalSock.accept();

		        System.out.println("UsuÃ¡rio conectado ao socket");
		        Thread t = new Thread (server);
		        t.start();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        
			}
		}.start();
	}
	

	public void receiveMessageLoop(){
		new Thread("Receive message thread"){
			public void run(){
				try{
					
					String line;
					while((line = bufferedReader.readLine()) != null){ //botar uma condicao decente aqui, tipo isNotDie()
						
						System.out.println("RECEIVED " + line);
						messageListener.onMessageReceived(line);
					}
				} catch(Exception e){
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	/** Gets the oldest message on buffer. 
	 * 
	 * @return the Message. Null if empty!
	 */
	public Message getMessage(){
		Message wMessage; 
		try{
			wMessage = msgBuffer.remove(0);
		} catch (Exception e){
			wMessage = null;
		}
		return wMessage;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	private void endConnection() {
		if(tcpConnection == null) return;
		try {
			tcpConnection.close();
			isConnected = false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



	public void sendMessage(String clientsMessage) {
		try {
			bufferedWriter.append(clientsMessage);
			bufferedWriter.flush();
		} catch (IOException e) {
		
			e.printStackTrace();
			endConnection();
		}
	}
	public void setSendMessagePort(int sendMessagePort) {
		this.remoteMessagePort = sendMessagePort;
	}
	public int getSendMessagePort() {
		return remoteMessagePort;
	}
	public void setLocalPort(int localPort) {
		this.localPort = localPort;
	}
	public int getLocalPort() {
		return localPort;
	}
	public String getRemoteIP() {
		return remoteIP;
	}
	public void setRemoteIP(String remoteIP) {
		this.remoteIP = remoteIP;
	}
	public void setListener(PeerListener listener) {
		this.peerListener = listener;
	}
	public PeerListener getListener() {
		return peerListener;
	}
	public void setRemotePeerName(String remotePeerName) {
		this.remotePeerName = remotePeerName;
	}
	public String getRemotePeerName() {
		return remotePeerName;
	}
	public void setMessageListener(MessageListener messageListener) {
		this.messageListener = messageListener;
	}
	public MessageListener getMessageListener() {
		return messageListener;
	}
}
