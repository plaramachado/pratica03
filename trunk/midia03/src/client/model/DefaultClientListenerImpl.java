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
	
	public DefaultClientListenerImpl(ClientFrame frame){
		this.frame = frame;
	}
	
	@Override
	public void changeStateOnline(boolean state) {
		// TODO Auto-generated method stub

	}

	@Override
	public void registerError() {

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
			ChatFrame c = this.frame.createChatFrame(caller);
			
			Map<String, ClientInfo> peers = this.frame.getPeers();
			ClientInfo info = new ClientInfo();
			info.setChatFrame(c);
			peers.put(caller, info);
			
			/* to p2p connection */
			Map<String, P2P> connections = this.frame.getClient().getConnectionsP2P();
			connections.get(caller).responseP2P();
			
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
		//this.frame.createChatFrame(caller);
		
		this.frame.getClient().acceptCall();
		ChatFrame c = this.frame.createChatFrame(caller);
		
		Map<String, ClientInfo> peers = this.frame.getPeers();
		ClientInfo info = new ClientInfo();
		info.setChatFrame(c);
		peers.put(caller, info);
		
		/* to p2p connection */
		Map<String, P2P> connections = this.frame.getClient().getConnectionsP2P();
		System.out.println("caller: "+caller);
		connections.get(caller).requestP2P();
		
		this.frame.getCallDialog().dispose();

	}

}
