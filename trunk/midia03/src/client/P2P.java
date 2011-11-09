package client;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import video.Client;
import video.Server;

public class P2P extends Thread implements PeerListener{
	private String ip;
	
	private int receiveRTSPPort;
	private int sendRTSPPort;
	
	private int RTPPort;
	
	private int receiveMessagePort;
	private int sendMessagePort;

	ArrayList<Message> msgBuffer = new ArrayList<Message>();
	public void receiveText(String pText){
		
	}
	public void sendText(String pText){
		
	}
	public void gotP2P(String ip, int receiveRTSPPort, int sendRTSPPort, int RTPPort, int receiveMessagePort, int sendMessagePort) {

		this.ip = ip;
		this.receiveRTSPPort = receiveRTSPPort;
		this.sendRTSPPort = sendRTSPPort;
		
		this.RTPPort = RTPPort;
		
		this.receiveMessagePort = receiveMessagePort;
		this.sendMessagePort = sendMessagePort;

	}
	public void requestVideo(){ //request TO SEND a video
		//if ok, call sendVideo()
	}
	public void acceptVideo(){ //accept TO RECEIVE a video
		//if ok, call receiveVideo().. que nao tem a porra da chamada do start, tem que modificar video.cliente
	}
	public void receiveVideo(){
		new Thread ("Receive video thread") {
			public void run() {
				video.Client client = new video.Client();
				client.setIp(ip);
				client.setRTSPPort(sendRTSPPort);
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
					generalSock = new ServerSocket(receiveRTSPPort);
		        System.out.println("Esperando por cliente...");
		        Server server = new Server();
		        server.RTSP_ID = sessionID++;
		        
		       // server.serverSocket = generalSock;
				server.RTSPsocket = generalSock.accept();

		        System.out.println("Usuário conectado ao socket");
		        Thread t = new Thread (server);
		        t.start();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        
			}
		}.start();
	}
	
	public void sendMessage(Message msg){
		try {
			Socket socket = new Socket(ip,sendMessagePort);
			PrintStream ps = new PrintStream(socket.getOutputStream()); 
			ps.println(msg.getContent());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	public void receiveMessage(){
		new Thread("Receive message thread"){
			public void run(){
				try{
					ServerSocket server = new ServerSocket(receiveMessagePort);
					while(true){ //botar uma condicao decente aqui, tipo isNotDie()
						Socket socket= server.accept();
						BufferedReader buff = new BufferedReader(new InputStreamReader(socket.getInputStream()));
						Message msg = new Message(buff.readLine());
						msgBuffer.add(msg);
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
	public void gotP2P(String ip, int port) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
}
