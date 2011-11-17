package client;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import util.ObservableArrayList;
import video.unicast.P2PVideoClient;
import video.unicast.P2PVideoServer;
/**
 * Classe que representa a conexão com outro cliente, independente
 * de qual das partes tenha iniciado a chamada. 
 * */
public class P2P extends Thread{
	
	private String remoteIP;
	private String remotePeerName;
	
	Socket textSocket; // TCP socket to send and receive text messages
	BufferedWriter textWriter; // Para envio de mensagem
	BufferedReader textReader; // Paraa recepção de mensagem
	
	private Socket controlConnection; // TCP socket used for control
	private BufferedWriter controlWriter;
	private BufferedReader controlReader;
	
	private P2PVideoServer localVideoServer;
	private P2PVideoClient localVideoClient;
	
	private int localRTSPPort;
	private int remoteRTSPPort;
	private int localRTPPort;
	private int remoteRTPPort;
	
	private int localMessagePort;
	private int remoteMessagePort;
	
	// Used for control
	private int remotePort; 
	private int localPort;
	
	// Listeners de eventos
	private MessageListener messageListener;
	private PeerListener peerListener;
	
	boolean isConnected = false;
	
	/**
	 * Construtor usado quando se deseja um objeto para 
	 * RECEBER a conexão. Deve ser seguido por uma chamada
	 * a startBuffers(socket).
	 * */
	public P2P(PeerListener listener){
		this();
		this.peerListener = listener;
	}
	
	/**
	 * Construtor usado quando se deseja que o peer 
	 * inicie a comunicação com o outro lado.
	 * startBuffers() deve ser chamado sem parametros.
	 * */
	public P2P(String remoteIP, int remotePort, PeerListener listener){
		this();
		this.remoteIP = remoteIP;
		this.remotePort = remotePort;
		this.peerListener = listener;
	}
	
	// inicia o servidor de vídeo
	private P2P(){
		this.localVideoServer = new P2PVideoServer();
		this.setLocalRTSPPort(this.localVideoServer.getLocalRTSPPort());
		System.out.println("P2P: Criado o servidor de video local, escutando na porta " + this.localVideoServer.getLocalRTSPPort());
	}
	
	/**
	 * Inicia os sockets para a comunicação texto.
	 * 
	 * */
	public void requestP2P(){
		ServerSocket server = null;
		try {
			setControlConnection(new Socket(remoteIP, remotePort));
			startControlBuffers();
		} catch (UnknownHostException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		if (!isConnected){
			try {
				server = new ServerSocket(0); // inicia socket para texto
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			localMessagePort = server.getLocalPort();
			localRTSPPort = this.localVideoServer.getLocalRTSPPort();
			
			String wRequest = "SETUP\r\n";
			wRequest += "portext: " + String.valueOf(localMessagePort) + "\r\n";
			wRequest += "porRTSP: " + String.valueOf(getLocalRTSPPort()) + "\r\n";
			try {
				
				
				getControlWriter().append(wRequest);
				getControlWriter().flush();
				
				// Obtem a porta para RSTP
				//BufferedReader reader = new BufferedReader(new InputStreamReader(tcpConnection.getInputStream()));
				String line = getControlReader().readLine();
				line = getControlReader().readLine();
				int remotePort = Integer.parseInt(line.substring( line.indexOf(":")+ 1).trim());
				this.setRemoteRTSPPort(remotePort);
				System.out.println("HANDSHAKE COMPLETED, REMOTE RTSP PORT = " + remotePort);
				isConnected = true;				
			} catch (Exception e){
				e.printStackTrace();
				//endConnection();
			}
			
		}
		
		// Espera a conexão do cliente
		try {
			textSocket = server.accept();
			//textSocket = server.accept();
			startBuffers(textSocket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Aguarda pelo bye em uma thread separada
		Thread t = new Thread(){
			public void run() {
				// O que fazer quando receber o BYE?]
				// Fechar a janela de chat
				// Encerrar Sevidor e Clientes de video
				// Fechar sockets
				String line = "";
				while(true){
					try {
						line = getControlReader().readLine();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if( line!=null && line.contains("BYE"))
						endConnection();
				}
				
//				try{
//					endConnection();
//				}catch(Exception e){
//					e.printStackTrace();
//				}
				
			}
		};
		t.start();
		
	}

	public void startControlBuffers() throws IOException {
		setControlWriter(new BufferedWriter( new OutputStreamWriter(getControlConnection().getOutputStream())));
		setControlReader(new BufferedReader(new InputStreamReader(getControlConnection().getInputStream())));
	}
	
	public void sendBye(){
		String request = "BYE \r\n";
		try {
			getControlWriter().append(request);
			getControlWriter().flush();
			this.endConnection();
		} catch (IOException e) {
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
		textReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		textWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()) );
		System.out.println("startBuffers() on client that ACCEPTED the call");
		peerListener.gotP2P(this);
		receiveMessageLoop();
	}
	
	
	/**
	 * Call this when you want to open the channel with the remote host.
	 * */
	public void startBuffers() throws IOException {
		
		textSocket = new Socket(this.remoteIP, this.remoteMessagePort);
		textReader = new BufferedReader(new InputStreamReader(textSocket.getInputStream()));
		textWriter = new BufferedWriter(new OutputStreamWriter(textSocket.getOutputStream()) );
		peerListener.gotP2P(this);
		System.out.println("startBuffers() on client that INITIATED the call");
		receiveMessageLoop();
	}
	
//	public void acceptVideo(){ //accept TO RECEIVE a video
//		//this.localVideoClient = new P2PVideoClient(getRemoteIP(), getRemoteRTSPPort());
//
//	}
	public void receiveVideo(){
		new Thread ("Receive video thread") {
			public void run() {
				// Cria a janela. A partir daqui, as interações são disparadas pelos 
				// eventos da interface
				P2PVideoClient client = new P2PVideoClient(remoteIP, remoteRTSPPort);
				
			}
		}.start();

	}
	
	public void receiveMessageLoop(){
		new Thread("Receive message thread"){
			public void run(){
				try{
					
					String line;
					while((line = textReader.readLine()) != null){ //botar uma condicao decente aqui, tipo isNotDie()
						
						System.out.println("RECEIVED " + line);
						messageListener.onMessageReceived(line);
					}
				} catch(Exception e){
					e.printStackTrace();
				}
			}
		}.start();
	}
		
	/**
	 * 
	 * Free all resources allocated to this call. 
	 * */
	private void endConnection() {
		System.out.println("INICIANDO ENCERRAMENTO DA CONEXAO");
		if(getControlConnection() == null) return;
		try {
			getControlWriter().close();
			getControlReader().close();
			getControlConnection().close();
			
			textReader.close();
			textWriter.close();
			textSocket.close();
			
			localVideoClient.endConnection();
			localVideoServer.endConnection();
			
			isConnected = false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("CONEXAO ENCERRADA");
	}



	public void sendMessage(String clientsMessage) {
		try {
			textWriter.append(clientsMessage);
			textWriter.flush();
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

	public void setLocalRTSPPort(int localRTSPPort) {
		this.localRTSPPort = localRTSPPort;
	}

	public int getLocalRTSPPort() {
		return localRTSPPort;
	}

	public void setControlConnection(Socket controlConnection) {
		this.controlConnection = controlConnection;
	}

	public Socket getControlConnection() {
		return controlConnection;
	}

	public void setControlWriter(BufferedWriter controlWriter) {
		this.controlWriter = controlWriter;
	}

	public BufferedWriter getControlWriter() {
		return controlWriter;
	}

	public void setControlReader(BufferedReader controlReader) {
		this.controlReader = controlReader;
	}

	public BufferedReader getControlReader() {
		return controlReader;
	}
}
