package server;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.StringTokenizer;


public class Server {
	
	private BufferedReader bufferedReader;
	private BufferedWriter bufferedWriter;
	private Socket tcpConnexion;
	private RegisteredClient client; //data for the client of this connection
	ClientListener listener;
	
	public void setListener(ClientListener listener) {
		this.listener = listener;
	}
	
	/**
	 * Called when a client registers, in a instance of server.
	 * Used by the MasterServer to keep track of things and register the client, also check
	 * If the password is correct 
	 * @author Pedro
	 *
	 */
	public static interface ClientListener{
		public boolean getClient(RegisteredClient c);
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
			String newLine = "";
			try {
				newLine = bufferedReader.readLine();
				System.out.println(newLine);
				StringTokenizer tokens = new StringTokenizer(newLine);
				String request = tokens.nextToken();
				if(request.equals("REGISTER")){
					String name = tokens.nextToken();
					newLine = bufferedReader.readLine(); //password = *****
					System.out.println("Password line  = "+newLine);
					tokens = new StringTokenizer(newLine);
					//skips tokens
					tokens.nextToken(); //password
					tokens.nextToken(); //=
					String password = tokens.nextToken();
					newLine = bufferedReader.readLine(); //port = 1234
					tokens = new StringTokenizer(newLine);
					//skips tokens
					tokens.nextToken(); //port
					tokens.nextToken(); //=
					String portForClient = tokens.nextToken();
					String compAdress = tcpConnexion.getRemoteSocketAddress().toString();
					String substring = compAdress.substring(1);
					String[] split = substring.split(":");
					String ip = split[0];
					String portForServer = split[1];
					client = new RegisteredClient();
					client.setIp(ip);
					client.setPassword(password);
					client.setPortForClient(Integer.parseInt(portForClient));
					client.setPortForServer(Integer.parseInt(portForServer));
					client.setUserName(name);
					boolean registered = listener.getClient(client);
					if(registered) sendMessage("200 OK \r\n\r\n");
					if(!registered) sendMessage("401 \r\n");
					
//					System.out.printf(remoteSocketAddress.toString());
					
					
					
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
				break;
			}
			
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
		}
	}
}
