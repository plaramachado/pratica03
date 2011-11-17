package client.model;

import java.util.ArrayList;

import javax.swing.JOptionPane;

import client.GroupClientListener;
import client.view.ChatFrame;
import client.view.ClientFrame;
import client.view.GroupChatFrame;
import client.view.MutableList;

public class DefaultGroupClientListenerImpl implements GroupClientListener {
	
	private ClientFrame clientFrame;
	private GroupChatFrame chatFrame;
	
	
	public DefaultGroupClientListenerImpl(ClientFrame f) {
		this.clientFrame = f;
	}

	@Override
	public void groupRefused(String groupName) {
		JOptionPane.showMessageDialog(clientFrame, "Your call to the group " + groupName + " was refused");

	}

	@Override
	public void groupAccepted(String groupName) {
		JOptionPane.showMessageDialog(clientFrame, "You was accepted for the group " + groupName );
		this.chatFrame = this.clientFrame.createGroupChatFrame(groupName);
		this.chatFrame.setOwner(false);
		this.chatFrame.setCaller(groupName);
		

	}

	@Override
	public void createFailed(String groupName) {
		
		System.out.println("CREATE NOK");
		JOptionPane.showMessageDialog(clientFrame, "Could not create group " + groupName);

	}

	@Override
	public void createOk(String groupName) {
		// TODO criar janela de chat do grupo
		// FIXME criar janela de chat do grupo
		System.out.println("CREATE OK");
		JOptionPane.showMessageDialog(clientFrame, "Sucessfully created the group " + groupName);
		this.chatFrame = this.clientFrame.createGroupChatFrame(groupName);
		this.chatFrame.setOwner(false);

	}

	@Override
	public void clientWantsToJoin(String groupName, String clientName) {
		int ok = JOptionPane.showConfirmDialog(clientFrame, "Client " + clientName + " wants to join " + groupName);
		System.out.println("Client " + clientName + " wants to join " + groupName);
		if( ok == JOptionPane.OK_OPTION){
			this.clientFrame.getGroupClient().acceptJoin(clientName);
			System.out.println("JOIN OK");
		}else{
			this.clientFrame.getGroupClient().refuseJoin(clientName);
			System.out.println("JOIN NOK");
		}

	}

	@Override
	public void updateClientsInGroup(ArrayList<String> participants) {
		
		

	}

	@Override
	public void joinGroupStatus(String newLine) {
		

	}

	@Override
	public void updateGroupNames(ArrayList<String> groups) {

		System.out.println("UPDATING GROUP LIST");
		MutableList l = this.clientFrame.getGroupsList();
		
		l.getContents().removeAllElements();
		for(int i = 0; i<groups.size(); i++){
			l.getContents().addElement(groups.get(i));
			System.out.println(groups.get(i));
		}
		this.clientFrame.pack();
		this.clientFrame.repaint();

	}

	@Override
	public void groupEnded(String groupName) {
		JOptionPane.showMessageDialog(clientFrame, "Group " + groupName + " was closed");
		if(this.chatFrame != null) this.chatFrame.dispose();
		this.chatFrame = null;

	}

	@Override
	public void textArrives(String groupName, String clientName, String msg) {
		if(this.chatFrame != null){
			this.chatFrame.getChatTextArea().append(clientName + ": " + msg.trim() + "\n");
		}else{
			System.out.println("CHATFRAME IS NULL");
		}

	}

	@Override
	public void videoPassing(String groupName) {
		// TODO Auto-generated method stub

	}

	public void setChatFrame(GroupChatFrame chatFrame) {
		this.chatFrame = chatFrame;
	}

	public GroupChatFrame getChatFrame() {
		return chatFrame;
	}

}
