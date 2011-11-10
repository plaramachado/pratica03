package server;

import java.util.ArrayList;

public class MasterGroupServer {
	
	ArrayList<GroupServer> groupServers = new ArrayList<GroupServer>();
	ArrayList<String> groupNames = new ArrayList<String>();

	public void newServer(Server server) {
		GroupServer groupServer = new GroupServer(server);
		groupServer.setMaster(this);
		groupServers.add(groupServer);
	}

	public boolean addNewGroup(String groupName) {
		boolean exists = groupExist(groupName);
		if(exists) return false;
		groupNames.add(groupName);
		return true;
	}

	private boolean groupExist(String groupName) {
		for (int i = 0; i < groupNames.size(); i++) {
			if(groupNames.get(i).equals(groupName) ) return true;
		}
		return false;
	}

}
