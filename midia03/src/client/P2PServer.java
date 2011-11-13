package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This class contains logic to deal with SETUP requests 
 * from other clients, after a call has been established.
 * 
 * It must be instantiated BEFORE the client sends the REGISTER
 * request to the server.
 * */
public class P2PServer implements Runnable{

	private int localPort; // port used by this application to listen to incoming connections
	private ServerSocket acceptSocket;
	private PeerListener listener;
	
	public P2PServer(PeerListener listener){
		this.listener = listener;
		try {
			acceptSocket = new ServerSocket(0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		localPort = acceptSocket.getLocalPort();
	}
	
	
	public int getLocalPort() {
		return localPort;
	}
	
	@Override
	/**
	 * Listen to incoming connections from other peers
	 * After the channels have been establisehd, call 
	 * the gotp2p  method of the PeerListener.   
	 * */
	public void run() {
		
		while(true){
			try {
				Socket peer = acceptSocket.accept();
				System.out.println("P2PServer: Recebi conexao de um cliente");
				new Thread( new P2PServerThread(peer, listener)).start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	
	

}

class P2PServerThread implements Runnable{
	
	Socket peer;
	PeerListener listener;
	private BufferedReader bufferedReader;
	private BufferedWriter bufferedWriter;
	private int sendMessagePort;
	private int sendRTSPPort;
	
	public P2PServerThread(Socket peer, PeerListener listener){
		this.peer = peer;
		this.listener = listener;
		try {
			this.bufferedReader = new BufferedReader(new InputStreamReader(peer.getInputStream()));
			this.bufferedWriter = new BufferedWriter( new OutputStreamWriter(peer.getOutputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	/**
	 * Handles the incoming connection.
	 * */
	public void run() {
		receiveP2P(); 
		
	}
	
	public void receiveP2P(){
		try{
			boolean done = false;
			String request;
			while(!done){ //ora trim... null... ass: Pedro
				request = bufferedReader.readLine().trim();
				System.out.println(request);
				if (request.contains("portext")){
					sendMessagePort =  Integer.parseInt(request.substring(request.indexOf(": ")+2));
				} else if (request.contains("porRTSP")) {
					sendRTSPPort =  Integer.parseInt(request.substring(request.indexOf(": ")+2));
					done = true;
				}
				
			}
			
			P2P p = new P2P(this.listener);
			
			// Send back the RSTP port we are going to listen
			this.bufferedWriter.append(responseP2P(p.getLocalRTSPPort())) ;
			this.bufferedWriter.flush();
			
			
			p.setSendMessagePort(sendMessagePort);
			p.setRemoteRTSPPort(sendRTSPPort);
			p.setRemoteIP(peer.getInetAddress().getHostAddress());
			p.startBuffers();
			
			
			
			System.out.println("P2PServer: iniciado novo PEER");
			
			
			
			//startTextAndVideoServers(); //dei certo, vou iniciar
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public String responseP2P(int receiveRTSPPort){
		String wResponse = "SETUPOK \r\n";
		wResponse += "porRTSP: " + String.valueOf(receiveRTSPPort) + "\r\n";
		return wResponse;
	}
	
}