package client;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import server.Messages;
import video.conference.ClientVideo;
import video.conference.ServerVideo;
import client.Client.ClientForker;

/**
 * @author Pedro
 * First, to use this, you need to have an instance of Client, to be instantiate this guy.
 * So basically you used client the same way as before, and the new parts of the interface that handle groups
 * only need to interact with this guy. Methods that need to be called by the interface:
 * 
 * *** Administrator methods:
 * createGroup
 * refuseJoin
 * acceptJoin
 * *** Client methods:
 * requestJoin
 * requestGroupList
 * groupLeave
 * groupText
 * 
 * You also need to implement GroupClientListener to create the callback methods, and then use setListener
 * 
 * Of course, there is more to come in this very class and in the listener
 */
public class GroupClient implements ClientForker{
	Client client;

	private String myGroupName = "";
	private String groupJoined = "";
	
	public GroupClient(Client client) {
		super();
		this.client = client;
		client.setForker(this);	
	}

	public void setListener(GroupClientListener listener) {
		this.listener = listener;
	}

	@Override
	public boolean fork(String newLine) {
		boolean processed = false;
		StringTokenizer tokens = new StringTokenizer(newLine);
		String nextToken = tokens.nextToken();
		System.out.println("Client try forking " + newLine);
		//		nextToken = nextToken.trim();
		if(nextToken.equals("TEXTGROUP")){
			String groupName = tokens.nextToken();
			String clientName = tokens.nextToken();
			String nextLine = client.getNextLine();
			String msg = "";
			while(!nextLine.trim().isEmpty()){
				msg += nextLine + " \r\n";
				nextLine = client.getNextLine();
			}
			listener.textArrives(groupName, clientName, msg);
		}
		if(nextToken.equals("CLOSED")){
			processed = true;
			String groupName = tokens.nextToken();
			listener.groupEnded(groupName);
		}
		if(nextToken.equals("TEMP")){
			processed = true;
			listener.joinGroupStatus(newLine);
		}
		if(nextToken.equals("REFUSED")){
			processed = true;
			String groupName = tokens.nextToken();
			listener.groupRefused(groupName);
		}
		if(nextToken.equals("ACCEPTGROUP")){
			processed = true;
			String groupName = tokens.nextToken();
			groupJoined  = groupName;
			listener.groupAccepted(groupName);
		}
		if(nextToken.equals("CREATEERROR")){
			processed = true;
			String groupName = tokens.nextToken();
			listener.createFailed(groupName);
		}
		if(nextToken.equals("CREATEOK")){
			processed = true;
			String groupName = tokens.nextToken();
			myGroupName = groupName;
			listener.createOk(groupName);
		}
		if(nextToken.equals("WANTJOIN")){
			processed = true;
			String groupName = tokens.nextToken();
			String clientName = tokens.nextToken();
			listener.clientWantsToJoin(groupName, clientName);
		}
		if(nextToken.equals("GROUPS")){
			System.out.println("GOTGROUPS ");
			processed = true;
			ArrayList<String> groups = new ArrayList<String>();
			while(true){
				String nextLine = client.getNextLine();
				String groupName = nextLine.trim();
				if(groupName.isEmpty()) break;
				groups.add(groupName);
			}
			listener.updateGroupNames(groups);
		}
		if(nextToken.equals("PARTICIPANTS")){
			processed = true;
			ArrayList<String> participants = new ArrayList<String>();
			while(true){
				String nextLine = client.getNextLine();
				String clientName = nextLine.trim();
				if(clientName.isEmpty()) break;
				participants.add(clientName);
			}
			listener.updateClientsInGroup(participants);
		}
		if(nextToken.equals("VIDEOGO")){
			String groupName = tokens.nextToken();
			String connectPort = tokens.nextToken();
			int portNumber = Integer.parseInt(connectPort);
			try {
				ClientVideo.main(Client.serverIP, portNumber);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			listener.videoPassing(groupName);
		}
		return processed;
	}

	@Override
	public void connectionDied() {
		//not necessary, maybe
	}

	public void createGroup(String groupName){ 
		String msg = Messages.create(groupName);
		client.sendMessage(msg);
	}
	
	public void refuseJoin(String clientName){
		String msg = Messages.refuseJoin(myGroupName, clientName);
		client.sendMessage(msg);
	}
	
	public void acceptJoin(String clientName){
		String msg = Messages.acceptJoin(myGroupName, clientName);
		client.sendMessage(msg);
	}
	
	public void requestJoin(String groupName){
		String msg = Messages.requestJoin(groupName);
		client.sendMessage(msg);
	}
	
	public void requestGroupList(){
		String msg = Messages.GROUPGET;
		client.sendMessage(msg);
	}
	
	public void groupLeave(){
		String msg = Messages.leave(groupJoined);
		client.sendMessage(msg);
	}
	
	public void groupText(String message){
		String msg = Messages.groupText(groupJoined, message);
		client.sendMessage(msg);
	}
	
	public void sendVideo(){
		int rtspPortUsed = 0;
		try {
			rtspPortUsed = ServerVideo.mainMethod(0);
		}
		 catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(rtspPortUsed == 0) System.out.println("BAD PORT HERE");
		String sendVideo = Messages.sendVideo(myGroupName, rtspPortUsed);
		client.sendMessage(sendVideo);
	}
	
	GroupClientListener listener = new GroupClientListener() { //EMPTY IMPLEMENTATION, to avoid nullPointer

		@Override
		public void groupRefused(String groupName) {
			// TODO Auto-generated method stub

		}

		@Override
		public void groupAccepted(String groupName) {
			System.out.println("Accepted in " + groupName);
		}

		@Override
		public void createOk(String groupName) {
			System.out.println("Created ok " + groupName);

		}

		@Override
		public void createFailed(String groupName) {
			System.out.println("Created not ok... " + groupName);
		}

		@Override
		public void clientWantsToJoin(String groupName, String clientName) {
			// TODO Auto-generated method stub

		}

		@Override
		public void updateClientsInGroup(ArrayList<String> participants) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void joinGroupStatus(String newLine) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void updateGroupNames(ArrayList<String> groups) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void groupEnded(String groupName) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void textArrives(String groupName, String clientName, String msg) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void videoPassing(String groupName) {
			// TODO Auto-generated method stub
			
		}
	};
	
//	public static void main(String[] args){
//		
//	}

}
