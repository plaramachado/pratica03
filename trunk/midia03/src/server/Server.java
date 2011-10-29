package server;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {
	
	private BufferedReader bufferedReader;
	private BufferedWriter bufferedWriter;
	private Socket tcpConnexion;
	private RegisteredClient client;
	ClientListener listener;
	
	public void setListener(ClientListener listener) {
		this.listener = listener;
	}
	
	public static interface ClientListener{
		public void getClient(RegisteredClient c);
	}

	public Server(Socket accept) throws IOException {
		bufferedReader = new BufferedReader(new InputStreamReader(accept.getInputStream()) );
		bufferedWriter = new BufferedWriter(new OutputStreamWriter(accept.getOutputStream()) );
		tcpConnexion = accept;
	}

	

	public void acceptRequests(){
		boolean done = false;
		System.out.println("Waiting for Requests");
		while(!done){
			String RequestLine = "";
			try {
				RequestLine = bufferedReader.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				break;
			}
			System.out.printf(RequestLine);
		}
	}
}
