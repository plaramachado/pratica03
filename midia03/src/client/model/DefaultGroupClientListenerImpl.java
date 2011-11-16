package client.model;

import java.util.ArrayList;

import javax.swing.JOptionPane;

import client.GroupClientListener;
import client.view.ClientFrame;
import client.view.MutableList;

public class DefaultGroupClientListenerImpl implements GroupClientListener {
	
	private ClientFrame clientFrame;
	
	public DefaultGroupClientListenerImpl(ClientFrame f) {
		this.clientFrame = f;
	}

	@Override
	public void groupRefused(String groupName) {
		JOptionPane.showMessageDialog(clientFrame, "Your call to the group" + groupName + "was refused");

	}

	@Override
	public void groupAccepted(String groupName) {
		// TODO Auto-generated method stub
		// FIXME criar janela de conversa em grupo

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
		JOptionPane.showMessageDialog(clientFrame, "Sucessfully created the group" + groupName);

	}

	@Override
	public void clientWantsToJoin(String groupName, String clientName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateClientsInGroup(ArrayList<String> participants) {
		
		System.out.println("UPDATING GROUP LIST");
		MutableList l = this.clientFrame.getGroupsList();
		
		l.getContents().removeAllElements();
		for(int i = 0; i<participants.size(); i++){
			l.getContents().addElement(participants.get(i));
		}
		this.clientFrame.pack();
		this.clientFrame.repaint();

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

}
