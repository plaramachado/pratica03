package client.model;

import java.util.ArrayList;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import client.Client.ClientListener;
import client.P2P;
import client.view.*;

public class DefaultClientListenerImpl implements ClientListener {
	
	private ClientFrame frame;
	
	public static String caller = "unknown";
	
	public DefaultClientListenerImpl(ClientFrame frame){
		this.frame = frame;
	}
	
	@Override
	public void changeStateOnline(boolean state) {
		// TODO Auto-generated method stub

	}

	@Override
	public void registerError() {

		JOptionPane.showMessageDialog(frame, "Erro registering user. Please try again.");
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
		
	}

	@Override
	public void incomingCall(String caller) {
		// TODO Tratar a conexão que chegou
		//JOptionPane.showMessageDialog(this.frame, "Accept call from " + caller + "?");
		DefaultClientListenerImpl.caller = caller; 
		int answer = JOptionPane.showConfirmDialog(this.frame, "Accept call from " + caller + "?", "Incoming call", JOptionPane.YES_NO_OPTION);
		
		if(answer == JOptionPane.YES_OPTION){
			this.frame.getClient().acceptCall();
			
		}else{
			this.frame.getClient().refuseCall();
		}
	}

	@Override
	public void updateCallStatus(String status) {
		System.out.println("Status:" + status);
		JTextArea l = this.frame.getCallDialog().getLabel();
		l.setText( l.getText() + "\n" + status + "...");
		System.out.println( l.getText() + "\n" + status + "..." );

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
	public void callCompleted(String ip, int port, String callerName) {
		
		this.frame.getClient().acceptCall();
		P2P p2p = new P2P(ip, port, this.frame.getPeerListener());
		p2p.setRemotePeerName(callerName);
		p2p.requestP2P();
		this.frame.getCallDialog().dispose();

	}

}
