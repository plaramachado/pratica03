package client.model;

import client.Client;
import client.P2P;
import client.PeerListener;

public class DefaultPeerListenerImpl implements PeerListener{

	@Override
	public void gotP2P(Client host, String ip, int port) {
		// TODO Auto-generated method stub
		System.out.println("last client: " + host.getLastClient());
		host.getConnectionsP2P().put(host.getLastClient(), new P2P(ip,port));
	}

}
