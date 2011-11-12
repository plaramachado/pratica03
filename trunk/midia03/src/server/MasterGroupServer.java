package server;

import java.io.IOException;
import java.util.ArrayList;

import server.view.ServerFrame;

/**
 * @author Pedro
 * Call the main method here
 * or Instantiate masterServer and then just instantiate this guy
 */
public class MasterGroupServer {
	
	ArrayList<GroupServer> groupServers = new ArrayList<GroupServer>();
	ArrayList<Group> groups = new ArrayList<Group>();
	MasterServer masterServer;
	
	public MasterGroupServer(MasterServer masterServer) {
		super();
		this.masterServer = masterServer;
	}

	public static class Group{
		String name;
		String owner;
		ArrayList<String> clientNames;
		
		public void addClient(String client){
			clientNames.add(client);
		}
		
		public void removeClient(String client){
			clientNames.remove(client);
		}
		
		public String getParticipantsMessage(){
			String msg = "PARTICIPANTS \r\n";
			for (int i = 0; i < clientNames.size(); i++) {
				msg += clientNames.get(i) + " \r\n";
			}
			msg += "\r\n";
			return msg;
		}
		
		public ArrayList<String> getClientNames() {
			return clientNames;
		}
		
		public String getName() {
			return name;
		}
		public String getOwner() {
			return owner;
		}
		public void setName(String name) {
			this.name = name;
		}
		public void setOwner(String owner) {
			this.owner = owner;
		}
	}

	public void newServer(Server server) {
		GroupServer groupServer = new GroupServer(server);
		groupServer.setMaster(this);
		groupServers.add(groupServer);
	}

	public boolean addNewGroup(String groupName, String owner) {
		boolean exists = groupExist(groupName);
		if(exists) return false;
		Group group = new Group();
		group.setName(groupName);
		group.setOwner(owner);
		groups.add(group);
		return true;
	}

	public boolean groupExist(String groupName) {
		Group group = getGroup(groupName);
		return (group != null);
	}

	private Group getGroup(String groupName) {
		for (int i = 0; i < groups.size(); i++) {
			if(groups.get(i).getName().equals(groupName) ) return groups.get(i);
		}
		return null;
	}

	public void askEnterGroup(String groupName, String clientName) {
		Group group = getGroup(groupName);
		String owner = group.getOwner();
		masterServer.sendMessage(owner, Messages.wantJoin(groupName, clientName));
	}

	public void refusedClient(String clientName, String groupName) {
		masterServer.sendMessage(clientName, Messages.refused(groupName));
		
	}

	public void acceptClient(String clientName, String groupName) {
		getGroup(groupName).addClient(clientName);
		updateGroupParticipants(groupName);
		masterServer.sendMessage(clientName, Messages.accepted(groupName));
	}

	private void updateGroupParticipants(String groupName) {
		Group group = getGroup(groupName);
		String participantsMessage = group.getParticipantsMessage();
		String owner = group.getOwner();
		masterServer.sendMessage(owner, participantsMessage);
	}

	public String getGroupList() {
		String message = "GROUPS";
		for (int i = 0; i < groups.size(); i++) {
			message += groups.get(i)+" \r\n";
		}
		message += "\r\n";
		return message;
	}

	public void closeGroup(String groupName) {
		Group group = getGroup(groupName);
		String name = group.getName();
		ArrayList<String> clientNames = group.getClientNames();
		groups.remove(group);
		String msg = Messages.close(name);
		for (int i = 0; i < clientNames.size(); i++) {
			masterServer.sendMessage(clientNames.get(i), msg);
		}
	}

	public void clientLeave(String groupName, String userName) {
		Group group = getGroup(groupName);
		group.removeClient(userName);
		updateGroupParticipants(groupName);
	}
	
	public static void main(String[] args) throws IOException{
		MasterServer m = new MasterServer();
		ServerFrame s = new ServerFrame();
		m.setListener(s.getListener());
		m.listen();
		
		MasterGroupServer mg = new MasterGroupServer(m);
		
	}
}