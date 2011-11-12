package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import server.MasterServer;
import server.Server;


/**
 * @author Pedro
 * How to use this class:
 * First off: you need to create a instance. Once you do, you'll already have a connection with the server
 * but you're not registered.
 * Then you need to set the Username, Password and Port and call Register()
 * Once you do, you'll start receiving client lists when somebody goes online or offline
 * 
 * You can also use a ClientListener to respond to changes in a callback approach
 * Just define a ClientListener and call setListener()
 * 
 * If you go offline (the socket throws an exception) then you should call StartConnection() to register again
 * You may also change Username and Password if you wish.
 * 
 * CALL SETUP CONTINUATION
 * 
 */
public class Client {
	private static final int serverPort = MasterServer.serverPort;
	public static final String serverIP = "localhost";
	private BufferedReader bufferedReader;
	private BufferedWriter bufferedWriter;

	ArrayList<String> clientsOnline = new ArrayList<String>();
	private Map<String,P2P> connectionsP2P = new HashMap<String,P2P>();
	
	private String userName = "";
	private String password = "";
	private int port;
	private Socket serverSocket;
	private ClientListener listener;
	private PeerListener p2plistener;
	
	private boolean waitingForOnlineConfirm;
	private boolean waitingForCall;
	private String lastClient = ""; //the last client called
	private String caller;
	
	ClientForker forker;
	
	public void setForker(ClientForker forker) {
		this.forker = forker;
	}
	
	public static interface ClientForker{
		public boolean fork(String newLine);
		public void connectionDied();
	}
	
	public int getPort() {
		return port;
	}
	
	public static interface ClientListener{
		public void changeStateOnline(boolean state); //the state represents online(true), offline(false)
		public void registerError(); //called when you try to register and an error occurs
		public void newClientList(ArrayList<String> clients); //when the list of clients change
		public void incomingCall(String caller); //an INVITE has been received from the especified caller
			//handle incomingCall and then call either acceptCall() or declineCall()
		public void updateCallStatus(String status); //for monitoring the call status
		public void callFailedNotFound();
		public void callFailedDecline();
		public void callCompleted(String ip, int port, String callerName);
	}
	

	public void setP2plistener(PeerListener p2plistener) {
		this.p2plistener = p2plistener;
	}
	public Map<String,P2P> getConnectionsP2P() {
		return connectionsP2P;
	}
	public void setConnectionsP2P(Map<String,P2P> connectionsP2P) {
		this.connectionsP2P = connectionsP2P;
	}
	
	public String getLastClient() {
		return lastClient;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserName(){ 
		return this.userName;
	}
	public void setListener(ClientListener listener) {
		this.listener = listener;
	}

	public ArrayList<String> getClientsOnline() {
		return clientsOnline;
	}
	
	public void setPort(int port) {
		this.port = port;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Client() throws UnknownHostException, IOException{
		startConnection();
	}


	private void startConnection() throws UnknownHostException, IOException {
		serverSocket = new Socket(serverIP, serverPort);
		bufferedReader = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
		bufferedWriter = new BufferedWriter(new OutputStreamWriter(serverSocket.getOutputStream()));
		new Thread("Client waiting for server thread"){
			public void run() {
				waitForServer();

			}

		}.start();
	}

	private void waitForServer(){
		boolean done = false;
		char[] array = new char[100];
		while(!done){
			String readLine = "";
			try {
				readLine = bufferedReader.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				connectionDies();
				done = true;
				e.printStackTrace();
			}
			if(forker != null){ //allows the processing of server message to be done by another class
				boolean fork = forker.fork(readLine);
				if(fork) continue;
			}
			if(waitingForCall){
				if(readLine.contains("100") || readLine.contains("101") || readLine.contains("180")){
					if(listener != null) listener.updateCallStatus(readLine);
				}
				if(readLine.contains("404")){
					waitingForCall = false;
					if(listener != null) listener.callFailedNotFound();
				}
				if(readLine.contains("603")){
					waitingForCall = false;
					if(listener != null) listener.callFailedDecline();
				}
				if(readLine.contains("200")){ //get call stuff here.
					try {
						waitingForCall = false;
						readLine = bufferedReader.readLine();
						StringTokenizer tokens = new StringTokenizer(readLine);
						tokens.nextToken(); //skips Destination-address:
						String ip = tokens.nextToken(); //gets IP
						readLine = bufferedReader.readLine();
						tokens = new StringTokenizer(readLine);
						tokens.nextToken(); //skips Destination-port:
						String port = tokens.nextToken(); //gets port
						//if(p2plistener != null) p2plistener.gotP2P(this, ip, Integer.parseInt(port));
						if(listener != null) listener.callCompleted(ip, Integer.parseInt(port), lastClient);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}
//			System.out.println("|"+readLine.trim()+"|");
			if(readLine.trim().contains("INVITE")){
				StringTokenizer tokens = new StringTokenizer(readLine);
				tokens.nextToken(); //skips INVITE
				caller = tokens.nextToken();
				if(listener != null) listener.incomingCall(caller);
			}
			if(readLine.trim().equals("401")){ //error
				if(waitingForOnlineConfirm){
					if(listener != null) listener.registerError();
					System.out.println("Register Error!");
					waitingForOnlineConfirm = false;
				}
			}
			if(readLine.trim().equals("200 OK")){
				if(waitingForOnlineConfirm){
					if(listener != null) listener.changeStateOnline(true);
					System.out.println("I'm Online!");
					waitingForOnlineConfirm = false;
				}
			}
			if(readLine.trim().equals("CLIENTS")){
				clientsOnline.clear(); //clears records of clients
				while(true){
					try {
						readLine = bufferedReader.readLine();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						connectionDies();
						done = true;
					}
					if(readLine.trim().isEmpty()) break; //leaves while when it`s blank
					clientsOnline.add(readLine.trim()); //adds online client
				}
				if(listener != null) listener.newClientList(clientsOnline);
				System.out.println("Online Clients now: ");
				for (int i = 0; i < clientsOnline.size(); i++) {
					System.out.println(clientsOnline.get(i));
				}
			}

		}
	}

	public void unregister(){
		StringBuffer s = new StringBuffer("");
		s.append("UNREGISTER " + userName + "\r\n");
		sendMessage(s.toString());
	}

	public void register() {
		StringBuffer s = new StringBuffer("");
		s.append("REGISTER " + userName + "\r\n");
		s.append("password = " + password + "\r\n");
		s.append("port = " + port + "\r\n");
		sendMessage(s.toString());
	}
	
	/**
	 * Method used to call another client. The string client is the name of the client you're calling
	 * @param client
	 */
	public void call(String client) {
		lastClient = client;
		StringBuffer s = new StringBuffer("");
		s.append("CALL " + client + " \r\n");
		waitingForCall = true;
		sendMessage(s.toString());
	}

	public void sendMessage(String message) {
		try {
			waitingForOnlineConfirm = true;
			bufferedWriter.append(message);
			bufferedWriter.flush();
		} catch (IOException e) {
			connectionDies();
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void connectionDies() {
		if(listener != null) listener.changeStateOnline(false);
		if(forker != null) forker.connectionDied();
		if(serverSocket != null && !serverSocket.isClosed())
			try {
				serverSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
	}
	
	/**
	 * Called when you want to accept a call
	 */
	public void acceptCall(){
		//p2plistener.invitedP2P(this, caller);
		sendMessage(Server.SIMPLEOK);
		
		
	}
	
	public void refuseCall(){
		sendMessage(Server.DECLINE);
	}
	public String getNextLine() {
		try {
			return bufferedReader.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
}
