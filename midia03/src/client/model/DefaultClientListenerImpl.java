package client.model;

import java.util.ArrayList;

import javax.swing.JOptionPane;

import client.Client.ClientListener;
import client.view.ClientFrame;
import client.view.*;

public class DefaultClientListenerImpl implements ClientListener {
	
	private ClientFrame frame;
	
	public DefaultClientListenerImpl(ClientFrame frame){
		this.frame = frame;
		System.out.println("Set frame on DefClientListener");
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
		MutableList l = this.frame.getContactsList();
		
		l.getContents().removeAllElements();
		for(int i = 0; i<clients.size(); i++){
			l.getContents().addElement(clients.get(i));
		}
		this.frame.pack();
		this.frame.repaint();
		// TODO forçar o redesenho da janela, o model atualiza mas a view não
		
	}

	@Override
	public void incomingCall(String caller) {
		// TODO Tratar a conexão que chegou
		JOptionPane.showMessageDialog(this.frame, "Accept call from " + caller + "?");
	}

	@Override
	public void updateCallStatus(String status) {
		System.out.println("Status:" + status);
		if( this.frame == null) System.out.println("Frame is null");
		if( this.frame.getCallDialog() == null) System.out.println("CallDialog is null");
		if( this.frame.getCallDialog().getLabel() == null) System.out.println("Label is null");
		this.frame.getCallDialog().getLabel().setText(status + "...");

	}

	@Override
	public void callFailedNotFound() {
		this.frame.getCallDialog().getLabel().setText("Call failed. User not found");

	}

	@Override
	public void callFailedDecline() {
		this.frame.getCallDialog().getLabel().setText("User rejected call");

	}

	@Override
	public void callCompleted() {
		// TODO Auto-generated method stub

	}

}
