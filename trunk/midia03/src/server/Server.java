package server;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.StringTokenizer;


public class Server {

	public static final String DECLINE = "603 decline \r\n";
	public static final String SIMPLEOK = "200 OK \r\n\r\n";
	private BufferedReader bufferedReader;
	private BufferedWriter bufferedWriter;
	private Socket tcpConnexion;
	private RegisteredClient client; //data for the client of this connection
	ServerListener listener;
	MasterServer master;
	private boolean waitingForInviteConfirm;
	private String lastCaller = "";

	public RegisteredClient getClient() {
		return client;
	}

	public void setMaster(MasterServer master) {
		this.master = master;
	}

	public void setListener(ServerListener listener) {
		this.listener = listener;
	}

	/**
	 * Called when a client registers, in a instance of server.
	 * Used by the MasterServer to keep track of things and register the client, also check
	 * If the password is correct 
	 * @author Pedro
	 *
	 */
	public static interface ServerListener{
		public boolean getClient(RegisteredClient c); //when a client tries a register
		public void offClient(RegisteredClient c, Server s); //when the connection breaks;
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
				if(waitingForInviteConfirm){
					if(newLine.trim().equals("200 OK")){
						waitingForInviteConfirm = false;
						master.confirmInvite(lastCaller, client.getIp(), client.getPortForClient());
					}
					if(newLine.trim().equals("603 decline")){
						master.declineInvite(lastCaller);
					}
				}
				if(request.equals("CALL")){
					sendMessage("100 trying \r\n");
					String destination = tokens.nextToken();
					boolean online = master.clientOnline(destination.trim());
					if(online){
						sendMessage("101 found \r\n");
						master.sendInvite(destination.trim(), client.getUserName());
						sendMessage("180 ringing \r\n");
					} else{
						sendMessage("404 not found \r\n");
					}
				}
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
					if(registered) sendMessage(SIMPLEOK);
					if(!registered) sendMessage("401 \r\n");

					//					System.out.printf(remoteSocketAddress.toString());



				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				endConnection();
				break;
			}

		}
	}



	private void endConnection() {
		if(tcpConnexion == null) return;
		try {
			tcpConnexion.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		listener.offClient(client, this);
	}



	public void sendMessage(String clientsMessage) {
		try {
			bufferedWriter.append(clientsMessage);
			bufferedWriter.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//			tcpConnexion.
			e.printStackTrace();
			endConnection();
		}
	}

	public void sendInvite(String caller) {
		waitingForInviteConfirm = true;
		lastCaller  = caller; //so you can reply to it later
		String message = "INVITE " + caller+ " \r\n";
		sendMessage(message);
	}

	public void declineInvite() {
		sendMessage(DECLINE);
	}

	public void confirmInvite(String ip, int portForClient) {
		String s = "200 OK \r\n";
		s += "Destination-address: "+ip+"\r\n";
		s += "Destination-port: "+portForClient+"\r\n\r\n";
		sendMessage(s);
	}
}
