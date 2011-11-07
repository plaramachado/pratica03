package client.model;

import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import client.Client.ClientListener;
import client.view.*;

public class DefaultClientListenerImpl implements ClientListener {
	
	private ClientFrame frame;
	
	public DefaultClientListenerImpl(ClientFrame frame){
		this.frame = frame;
		//System.out.println("Set frame on DefClientListener");
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
		//JOptionPane.showMessageDialog(this.frame, "Accept call from " + caller + "?");
		int answer = JOptionPane.showConfirmDialog(this.frame, "Accept call from " + caller + "?", "Incoming call", JOptionPane.YES_NO_OPTION);
		
		if(answer == JOptionPane.YES_OPTION){
			this.frame.getClient().acceptCall();
			this.frame.createChatFrame(caller);
		}else{
			this.frame.getClient().refuseCall();
		}
	}

	@Override
	public void updateCallStatus(String status) {
		System.out.println("Status:" + status);
		//if( this.frame == null) System.out.println("Frame is null");
		//if( this.frame.getCallDialog() == null) System.out.println("CallDialog is null");
		//if( this.frame.getCallDialog().getLabel() == null) System.out.println("Label is null");
		JTextArea l = this.frame.getCallDialog().getLabel();
		
		l.setText( l.getText() + "\n" + status + "...");

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
	public void callCompleted(String caller) {
		// TODO Quer dize que é aqui que vai a mágica, seu Silvio?
		// Adicionar a abertura dos sockets e tal
		this.frame.createChatFrame(caller);
		this.frame.getCallDialog().dispose();

	}

}
