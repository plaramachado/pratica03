package server;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import server.Server.ServerListener;
import server.view.ServerFrame;


public class MasterServer {

	public static final int serverPort = 2000;
	
	MasterGroupServer groupMaster;

	ArrayList<Server> servers = new ArrayList<Server>();
	ArrayList<RegisteredClient> clients = new ArrayList<RegisteredClient>();
	MasterListener listener;
	ServerListener lis = new ServerListener() {

		@Override
		public boolean getClient(RegisteredClient c) {
			for (int i = 0; i < clients.size(); i++) {
				RegisteredClient registeredClient = clients.get(i);
				if(registeredClient.sameName(c)){
					if(registeredClient.samePass(c)){
						if(registeredClient.isOnline()){
							System.out.println("Already Online");
							return false; //he's already online

						}
						c.setOnline(true);
						clients.set(i, c);

						updateClients();
						return true; //correct password
					} else{
						System.out.println("Wrong Password");
						return false; //wrong password
					}
				}
			}
			clients.add(c); //registers client, wasn't before
			c.setOnline(true);
			updateClients();
			return true; //registered
		}

		@Override
		public void offClient(RegisteredClient c, Server s) {
			if(c != null) c.setOnline(false);
			servers.remove(s);
			updateClients();
		}

	};
	
	public void setListener(MasterListener listener) {
		this.listener = listener;
	}

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
		if(listener != null) listener.changeClients(clients);
	}

	public static void main(String[] args) throws IOException{
		MasterServer m = new MasterServer();
		ServerFrame s = new ServerFrame();
		m.setListener(s.getListener());
		m.listen();
		
		
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
						server.setMaster(MasterServer.this);
						servers.add(server);
						server.setListener(lis);
						if(groupMaster != null) groupMaster.newServer(server);
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

	/**
	 * 
	 * @param client
	 * @param message
	 * @return returns false if the client isn't registered or is offline
	 */
	public boolean sendInvite(String client, String caller) {
		boolean clientFound =  false;
		for (int i = 0; i < servers.size(); i++) {
			RegisteredClient client2 = servers.get(i).getClient();
			if(client2 != null) clientFound = client2.getUserName().equals(client);
			if(clientFound){ 
				servers.get(i).sendInvite(caller);
				return true;
			}
			//			servers.get(i).getClient()
		}
		return false;
	}

	public boolean clientOnline(String client) {
		for (int i = 0; i < clients.size(); i++) {
			if( clients.get(i).getUserName().equals(client)) return clients.get(i).isOnline();
		}
		return false;
	}

	public void confirmInvite(String lastCaller, String ip, int portForClient) {
		Server s = findServer(lastCaller);
		if(s != null) s.confirmInvite(ip, portForClient);
	}

	private Server findServer(String lastCaller) {
		boolean clientFound = false;
		for (int i = 0; i < servers.size(); i++) {
			RegisteredClient client2 = servers.get(i).getClient();
			
			if(client2 != null) clientFound = client2.getUserName().equals(lastCaller);
			if(clientFound) return servers.get(i);
		}
		return null;
	}

	public void declineInvite(String lastCaller) {
		Server findServer = findServer(lastCaller);
		if(findServer != null) findServer.declineInvite();
	}

	public void removeClient(String name) {
		Server findServer = findServer(name);
		if(findServer != null){
			servers.remove(findServer);
			RegisteredClient client = findServer.getClient();
			clients.remove(client);
			updateClients();
		}
	}
}
