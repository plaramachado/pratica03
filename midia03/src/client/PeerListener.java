package client;

import client.view.ClientFrame;

public interface PeerListener{
	
	/**
	 * Called when the program sets up a connection with the peer
	 * 
	 * */
	public void gotP2P(P2P peer);

	public void setFrame(ClientFrame clientFrame);
}