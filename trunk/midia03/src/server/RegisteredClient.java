package server;

public class RegisteredClient {
	private String userName;
	private String password;
	private int portForClient;
	private int portForServer;
	private String ip;
	private boolean online;
	public boolean isOnline() {
		return online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getPortForClient() {
		return portForClient;
	}
	public void setPortForClient(int portForClient) {
		this.portForClient = portForClient;
	}
	public int getPortForServer() {
		return portForServer;
	}
	public void setPortForServer(int portForServer) {
		this.portForServer = portForServer;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public boolean sameName(RegisteredClient c) {
		return userName.equals(c.getUserName());
	}
	public boolean samePass(RegisteredClient c) {
		return password.equals(c.getPassword());
//		return false;
	}
	
	

}
