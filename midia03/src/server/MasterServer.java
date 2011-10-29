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
		public void getClient(RegisteredClient c) {
			
		}
	};
	
	
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
						server.setListener(new ClientListener() {
							
							@Override
							public void getClient(RegisteredClient c) {
								// TODO Auto-generated method stub
								
							}
						});
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
