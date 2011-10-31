import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import server.MasterServer;


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
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public ArrayList<String> getClientsOnline() {
		return clientsOnline;
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
				try {
					waitForServer();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
			}
			
		}.start();
	}

	private void waitForServer() throws IOException {
		boolean done = false;
		char[] array = new char[100];
		while(!done){
			String readLine = bufferedReader.readLine();
			if(readLine.trim().equals("CLIENTS")){
				clientsOnline.clear(); //clears records of clients
				while(true){
					readLine = bufferedReader.readLine();
					if(readLine.trim().isEmpty()) break; //leaves while when it`s blank
					clientsOnline.add(readLine.trim()); //adds online client
				}
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

	public void register() throws IOException {
		bufferedWriter.append("REGISTER " + userName + "\r\n");
		bufferedWriter.append("password = " + password + "\r\n");
		bufferedWriter.append("port = " + port + "\r\n");
		bufferedWriter.flush();		
	}
}
	
