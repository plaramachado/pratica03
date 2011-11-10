package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.StringTokenizer;

import server.Server.ServerForker;

public class GroupServer implements ServerForker {

	private BufferedReader bufferedReader;
	private BufferedWriter bufferedWriter;
	private Server server;
	private MasterGroupServer master;

	public GroupServer(Server server) {
		bufferedReader = server.getBufferedReader();
		bufferedWriter = server.getBufferedWriter();
		server.setForker(this);
		this.server = server;
	}

	@Override
	public boolean fork(String receivedLine) {
		boolean processed = false; //if the fork happens
		StringTokenizer tokens = new StringTokenizer(receivedLine);
		String nextToken = tokens.nextToken();
		if(nextToken.equals("JOIN")){
			processed = true;	
		}
		if(nextToken.equals("CREATE")){
			processed = true;
			String groupName = tokens.nextToken();
			boolean addNewGroup = master.addNewGroup(groupName);
			if(addNewGroup) server.sendMessage("CREATEOK " + groupName + " \r\n");
			if(addNewGroup) server.sendMessage("CREATEERROR " + groupName + " \r\n");
		}
		if(nextToken.equals("GROUPGET")){
			processed = true;
		}
		return processed;
	}

	public void setMaster(MasterGroupServer masterGroupServer) {
		this.master = masterGroupServer;
	}

}
