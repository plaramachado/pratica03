package client.model;

import java.util.ArrayList;

import javax.swing.JOptionPane;

import client.Client.ClientListener;
import client.view.ClientFrame;

public class DefaultClientListenerImpl implements ClientListener {
	
	private ClientFrame frame;
	
	public DefaultClientListenerImpl(ClientFrame frame){
		this.frame = frame;
	}
	
	@Override
	public void changeStateOnline(boolean state) {
		// TODO Auto-generated method stub

	}

	@Override
	public void registerError() {
		// TODO Auto-generated method stub
		JOptionPane.showMessageDialog(null, "Erro registering user");

	}

	@Override
	public void newClientList(ArrayList<String> clients) {
	}

	@Override
	public void incomingCall(String caller) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateCallStatus(String status) {
		// TODO Auto-generated method stub

	}

	@Override
	public void callFailedNotFound() {
		// TODO Auto-generated method stub

	}

	@Override
	public void callFailedDecline() {
		// TODO Auto-generated method stub

	}

	@Override
	public void callCompleted() {
		// TODO Auto-generated method stub

	}

}
