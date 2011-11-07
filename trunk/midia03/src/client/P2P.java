package client;
import java.net.ServerSocket;

import video.Client;
import video.Server;

public class P2P extends Thread implements PeerListener{
	private String ip;
	private int receiveVideoPort;
	private int sendVideoPort;
	public void receiveText(String pText){
		
	}
	public void sendText(String pText){
		
	}
	public void gotP2P(String ip, int receiveVideoPort, int sendVideoPort) {

		this.ip = ip;
		this.receiveVideoPort = receiveVideoPort;
		this.sendVideoPort = sendVideoPort;
	}
	public void receiveVideo(){
		try {
			video.Client client = new video.Client();
			client.setIp(ip);
			client.setPort(sendVideoPort);
			client.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public void sendVideo() throws Exception{
		int sessionID = 123456;
		ServerSocket generalSock = new ServerSocket(receiveVideoPort);
        System.out.println("Esperando por cliente...");
        Server server = new Server();
        server.RTSP_ID = sessionID++;
        
       // server.serverSocket = generalSock;
        server.RTSPsocket = generalSock.accept();
        
        System.out.println("Usuário conectado ao socket");
        Thread t = new Thread (server);
        t.start();

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
