package client.view;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JList;
import javax.swing.ListModel;

import util.ObservableArrayList;

import client.Client;
import client.Client.ClientListener;
import client.Message;
import client.P2P;
import client.PeerListener;
import client.model.ClientInfo;
import client.model.DefaultClientListenerImpl;
import client.model.DefaultPeerListenerImpl;




public class ClientFrame extends BaseClientFrame{
	
	// Estas classes necessitam de apenas uma instância por aplicação
	private Client client;
	private LoginDialog loginDialog;
	private CallDialog callDialog;
	private ClientListener clientListener;
	private PeerListener peerListener;
	
	// Estas classes necessitam de uma instância por chamada
	private Map<String, ClientInfo> peers;
	

	/**
	 * Initializes interface and set event listeners
	 * */
	public ClientFrame(){
		super("iChat");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(700, 500);
		this.setMinimumSize(new Dimension(700, 500));
		this.peers = new HashMap<String, ClientInfo>();
		
		this.getRegisterButton().addActionListener(new RegisterButtonListener(this));
		this.getCallButton().addActionListener(new CallButtonListener(this));
		this.getPlayVideoButton().addActionListener(new PlayVideoButtonListener(this));
		
		try {
			this.client = new Client();
			this.client.setPort(5151 + (int)Math.round(10000*Math.random())); //tiago
			this.clientListener = new DefaultClientListenerImpl(this);
			this.client.setListener(clientListener);
			
			this.peerListener = new DefaultPeerListenerImpl();
			this.client.setP2plistener(peerListener);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setLoginDialog(LoginDialog loginDialog) {
		this.loginDialog = loginDialog;
	}

	public LoginDialog getLoginDialog() {
		return loginDialog;
	}
	
	public void setCallDialog(CallDialog callDialog) {
		this.callDialog = callDialog;
		//System.out.println("Setando CallDialog");
	}

	public CallDialog getCallDialog() {
		return callDialog;
	}

	public void updateContactList(ArrayList<String> contactList){
		JList l = this.getContactsList();
		l.removeAll();
		ListModel m = l.getModel();
		
		
		for(int i=0; i<contactList.size(); i++){
			
		}
		//l.repaint();
		this.pack();
	}


	public Client getClient(){
		return this.client;
	}
	
	public void setPeers(Map<String, ClientInfo> peers) {
		this.peers = peers;
	}

	public Map<String, ClientInfo> getPeers() {
		return peers;
	}

	public static void main(String[] args){
		new ClientFrame().setVisible(true);
	}

}






///////////////////////////////////////////
// TODO - Maybe move to separate source files

abstract class BaseListener implements ActionListener{
	private ClientFrame frame;
	public BaseListener(ClientFrame frame){
		this.setFrame(frame);
	}
	public BaseListener(){
		
	}
	public void setFrame(ClientFrame frame) {
		this.frame = frame;
	}
	public ClientFrame getFrame() {
		return frame;
	}
}


class RegisterButtonListener extends BaseListener{

	public RegisterButtonListener(ClientFrame c){
		super(c);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		
		// Prompt user for login and password
		LoginDialog l = new LoginDialog(this.getFrame());
		l.setLocationRelativeTo(this.getFrame());
		l.pack();
		//System.out.println("Event fired");
		
	}
	
}

class RegisterListener extends BaseListener{

	/**
	 * Fired when the user clicks in the 'OK' button 
	 * in the login frame.  
	 * */
	public RegisterListener(ClientFrame c){
		super(c);
	}
	public void actionPerformed(ActionEvent e) {
		
		Client c = this.getFrame().getClient();
		
		// Fasten seat belt. Doing risky things here.
		//Component tf = (Component)(e.getSource());
		
		LoginDialog l = this.getFrame().getLoginDialog();
		
		
		c.setUserName(l.getLoginField().getText());
		c.setPassword(l.getPwdField().getText());
		
		c.register();
		
		this.getFrame().setTitle("iChat - " + l.getLoginField().getText());
		
	}
	
}

class CallButtonListener extends BaseListener{

	public CallButtonListener(ClientFrame c) {
		super(c);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		String s = this.getFrame().getContactsList().getSelectedValue().toString();
		Client c = this.getFrame().getClient();
		
		
		CallDialog l = new CallDialog(this.getFrame());
		this.getFrame().setCallDialog(l);
		l.setLocationRelativeTo(this.getFrame());
		l.getLabel().setText("Calling " + s);
		l.setTitle("Call to " + s);
		l.setVisible(true);
		l.pack();
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		c.call(s);
		
	}
	
}
class PlayVideoButtonListener extends BaseListener{
	public PlayVideoButtonListener(ClientFrame c){
		super(c);
	}
    public void actionPerformed(ActionEvent e){
    	Client c = this.getFrame().getClient();
    	String s = this.getFrame().getContactsList().getSelectedValue().toString();

    	P2P p2p = c.getConnectionsP2P().get(s);
    	p2p.receiveVideo();
    }
    
}
