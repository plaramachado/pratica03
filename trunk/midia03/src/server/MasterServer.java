package server;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import server.Server.ClientListener;


public class MasterServer {
	
	public static final int serverPort = 2000;
	
	ArrayList<Server> servers = new ArrayList<Server>();
	ArrayList<RegisteredClient> clients = new ArrayList<RegisteredClient>();
	ClientListener lis = new ClientListener() {
		
		@Override
		public boolean getClient(RegisteredClient c) {
			for (int i = 0; i < clients.size(); i++) {
				RegisteredClient registeredClient = clients.get(i);
				if(registeredClient.sameName(c)){
					if(registeredClient.samePass(c)){
						if(registeredClient.isOnline()) return false; //he's already online
						registeredClient.setOnline(true);
						updateClients();
						return true; //correct password
					} else{
						return false; //wrong password
					}
				}
			}
			clients.add(c); //registers client, wasn't before
			c.setOnline(true);
			updateClients();
			return true; //registered
		}

	};
	
	private void updateClients() {
		String clientsMessage = "CLIENTS \r\n";
		for (int i = 0; i < clients.size(); i++) {
			RegisteredClient registeredClient = clients.get(i);
			if(!registeredClient.isOnline()) continue; //if the client isn't online, skip
			clientsMessage += registeredClient.getUserName() + "\r\n";
		}
		clientsMessage += "\r\n";
		for (int i = 0; i < servers.size(); i++) {
			servers.get(i).sendMessage(clientsMessage);
		}
	}
	
	public static void main(String[] args) throws IOException{
		new MasterServer().listen();
	}

	private void listen() throws IOException {
		final ServerSocket listenSocket = new ServerSocket(serverPort);
		while(true){
			System.out.println("Waiting for Connection");
			final Socket accept = listenSocket.accept();
			new Thread("Server - Client thread"){
				public void run() {
					Server server = null;
					try {
						server = new Server(accept);
						servers.add(server);
						server.setListener(lis);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					assert(server != null);
					server.acceptRequests();
				};
			}.start();
			
		}
	}
}
