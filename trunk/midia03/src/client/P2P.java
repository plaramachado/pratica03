package client;
import java.io.IOException;
import java.net.ServerSocket;

import video.Client;
import video.Server;

public class P2P extends Thread implements PeerListener{
	private String ip;
	
	private int receiveRTSPPort;
	private int sendRTSPPort;
	
	private int RTPPort;

	public void receiveText(String pText){
		
	}
	public void sendText(String pText){
		
	}
	public void gotP2P(String ip, int receiveRTSPPort, int sendRTSPPort, int RTPPort) {

		this.ip = ip;
		this.receiveRTSPPort = receiveRTSPPort;
		this.sendRTSPPort = sendRTSPPort;
		
		this.RTPPort = RTPPort;

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
	@Override
	public void gotP2P(String ip, int port) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
}
