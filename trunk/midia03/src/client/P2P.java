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

public class P2P extends Thread{
	Socket tcpConnection;
	BufferedWriter bufferedWriter;
	BufferedReader bufferedReader;
	private String ip;
	
	private int receiveRTSPPort;
	private int sendRTSPPort;
	
	private int RTPPort;
	
	private int receiveMessagePort;
	private int sendMessagePort;


	private int port; 
	
	boolean isConnected = false;
	List<Message> msgBuffer = new ObservableArrayList<Message>();
	
	public P2P(){
		
	}
	public P2P(String ip, int port){
		this.ip = ip;
		this.port = port;
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
	public void requestP2P(){
		if (!isConnected){
			String wRequest = "SETUP\r\n";
			wRequest += "portext: " + String.valueOf(receiveMessagePort) + "\r\n";
			wRequest += "porRTSP: " + String.valueOf(receiveRTSPPort) + "\r\n";
			try {
				
				tcpConnection= new Socket(ip, port);
				bufferedWriter = new BufferedWriter(new OutputStreamWriter(tcpConnection.getOutputStream()) );
				sendMessage(wRequest);
				//TODO SETAR ISCONNECTED PRA TRUE CASO OK
				startTextAndVideoServers(); //dei certo, vou iniciar
				
			} catch (Exception e){
				e.printStackTrace();
				endConnection();
			}
		}
	}

	public void receiveP2P(){
		try{
			System.out.println("receive1");
			tcpConnection= new ServerSocket(port).accept();
			System.out.println("receive2");
			bufferedReader = new BufferedReader(new InputStreamReader(tcpConnection.getInputStream()));
			String request;
			while((request = bufferedReader.readLine().trim()) != null){
				if (request.contains("portext")){
					sendMessagePort =  Integer.parseInt(request.substring(request.indexOf(": ")));
				} else if (request.contains("porRTSP")) {
					sendRTSPPort =  Integer.parseInt(request.substring(request.indexOf(": ")));
				}
			}
			responseP2P();
			startTextAndVideoServers(); //dei certo, vou iniciar
		} catch(Exception e){
			
		}
	}
	public void responseP2P(){
		String wResponse = "SETUPOK \r\n";
		wResponse += "porRTSP: " + String.valueOf(receiveRTSPPort) + "\r\n";
	}
	
	public void startTextAndVideoServers(){
		try {
			sendVideo();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		receiveMessage();
	}
	
	
	public void requestVideo(){ //request TO SEND a video

	}
	public void acceptVideo(){ //accept TO RECEIVE a video

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
			// TODO Auto-generated catch block
			//			tcpConnexion.
			e.printStackTrace();
			endConnection();
		}
	}
}
