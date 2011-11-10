package client;

public interface PeerListener{
	public void gotP2P(Client host, String ip, int port);
}