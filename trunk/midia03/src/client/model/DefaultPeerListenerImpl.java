package client.model;

import client.Client;
import client.P2P;
import client.PeerListener;

public class DefaultPeerListenerImpl implements PeerListener{

	@Override
	public void gotP2P(Client host, String ip, int port) {
		// TODO Auto-generated method stub
		
		System.out.println("last client: " + host.getLastClient());
		P2P connection = new P2P(ip,port);
		host.getConnectionsP2P().put(host.getLastClient(), connection);
		new Receiver(connection).start();
	}

}
class Receiver extends Thread {
	P2P connection;
	Receiver (P2P connection){
		this.connection = connection;
	}
	public void run(){
		connection.receiveP2P();
	}
}