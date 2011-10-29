import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import server.MasterServer;


public class Client {
	private static final int serverPort = MasterServer.serverPort;
	private static final String serverIP = "localhost";
	private BufferedReader bufferedReader;
	private BufferedWriter bufferedWriter;
	
	private String userName = "";
	private String password = "";
	private int port;
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public Client() throws UnknownHostException, IOException{
		Socket serverSocket = new Socket(serverIP, serverPort);
		bufferedReader = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
		bufferedWriter = new BufferedWriter(new OutputStreamWriter(serverSocket.getOutputStream()));
		waitForServer();
	}

	private void waitForServer() throws IOException {
		boolean done = false;
		while(!done){
			String readLine = bufferedReader.readLine();
			System.out.printf(readLine);
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
		client.setUserName("Shremps");
		client.register();
		
		
	}

	public void register() throws IOException {
		bufferedWriter.append("REGISTER " + userName + "\n");
		bufferedWriter.append("password " + password + "\n");
		bufferedWriter.append("port " + port + "\n");
		bufferedWriter.flush();		
	}
}
