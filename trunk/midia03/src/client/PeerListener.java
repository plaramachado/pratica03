package client;

public interface PeerListener{
	public void gotP2P(String ip, int port);
}