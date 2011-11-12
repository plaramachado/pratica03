package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
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
		if(nextToken.equals("GROUPTEXT")){
			String message = "";
			processed = true;
			String groupName = tokens.nextToken();
			String newLine = "";
			try {
				newLine = bufferedReader.readLine();
				while(!newLine.trim().isEmpty()){
					message += newLine + "\r\n";
					newLine = bufferedReader.readLine();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			master.groupText(groupName, message, server.getClient().getUserName());
			
		}
		if(nextToken.equals("REFUSE")){
			processed = true;
			String groupName = tokens.nextToken();
			String clientName = tokens.nextToken();
			master.refusedClient(clientName, groupName);
		}
		if(nextToken.equals("ACCEPT")){
			processed = true;
			String groupName = tokens.nextToken();
			String clientName = tokens.nextToken();
			master.acceptClient(clientName, groupName);
		}
		if(nextToken.equals("JOIN")){
			processed = true;	
			String groupName = tokens.nextToken();
			String clientName = tokens.nextToken();
			boolean groupExist = master.groupExist(groupName);
			server.sendMessage(Messages.GROUP_100_TRYING);
			if(groupExist){
				server.sendMessage(Messages.GROUP_101_FOUND);
				master.askEnterGroup(groupName, clientName);
				server.sendMessage(Messages.GROUP_180_RINGING);
			} else{
				server.sendMessage(Messages.GROUP_404_NOT_FOUND);
			}
				
		}
		if(nextToken.equals("CREATE")){
			processed = true;
			String groupName = tokens.nextToken();
			boolean addNewGroup = master.addNewGroup(groupName, server.getClient().getUserName());
			if(addNewGroup) server.sendMessage(Messages.createOk(groupName));
			if(addNewGroup) server.sendMessage(Messages.createError(groupName));
		}
		if(nextToken.equals("CLOSE")){
			processed = true;
			String groupName = tokens.nextToken();
			master.closeGroup("groupName");
			
		}
		if(nextToken.equals("LEAVE")){
			String groupName = tokens.nextToken();
			String userName = server.getClient().getUserName();
			master.clientLeave(groupName, userName);
		}
		if(nextToken.equals("GROUPGET")){
			processed = true;
			String groupList = master.getGroupList();
			server.sendMessage(groupList);
		}
		return processed;
	}



	public void setMaster(MasterGroupServer masterGroupServer) {
		this.master = masterGroupServer;
	}

	@Override
	public void connectionFell() {
		master.clientDies(server.getClient().getUserName());
	}

}
