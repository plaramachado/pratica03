import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import server.MasterServer;


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
 */
public class Client {
	private static final int serverPort = MasterServer.serverPort;
	private static final String serverIP = "localhost";
	private BufferedReader bufferedReader;
	private BufferedWriter bufferedWriter;

	ArrayList<String> clientsOnline = new ArrayList<String>();

	private String userName = "";
	private String password = "";
	private int port;
	private Socket serverSocket;
	private ClientListener listener;
	
	private boolean waitingForOnlineConfirm;
	
	public static interface ClientListener{
		public void changeStateOnline(boolean state); //the state represents online(true), offline(false)
		public void newClientList(ArrayList<String> clients); //when the list of clients change
	}

	public void setUserName(String userName) {
		this.userName = userName;
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
			System.out.println(readLine.trim());
			if(readLine.trim().equals("200 OK")){
				if(waitingForOnlineConfirm){
					if(listener != null) listener.changeStateOnline(true);
					System.out.println("I'm Online!");
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

	/**
	 * This main method will not be called regularly, there will be another class that starts up the GUI
	 * and does something similar to what this method does, filling password, username, calling register
	 * and other stuff.
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException{
		Client client = new Client();
		client.setPassword("aaa");
		client.setUserName("Shremps2");
		client.register();

	}

	public void register() {
		try {
			waitingForOnlineConfirm = true;
			bufferedWriter.append("REGISTER " + userName + "\r\n");
			bufferedWriter.append("password = " + password + "\r\n");
			bufferedWriter.append("port = " + port + "\r\n");
			bufferedWriter.flush();
		} catch (IOException e) {
			connectionDies();
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void connectionDies() {
		if(listener != null) listener.changeStateOnline(false);
		if(serverSocket != null && !serverSocket.isClosed())
			try {
				serverSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
	}
}

