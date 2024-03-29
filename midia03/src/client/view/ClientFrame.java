package client.view;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListModel;

import org.omg.CORBA.PRIVATE_MEMBER;

import client.Client;
import client.Client.ClientListener;
import client.GroupClient;
import client.P2PServer;
import client.PeerListener;
import client.model.DefaultClientListenerImpl;
import client.model.DefaultGroupClientListenerImpl;
import client.model.DefaultPeerListenerImpl;




public class ClientFrame extends BaseClientFrame{
	
	// Estas classes necessitam de apenas uma inst�ncia por aplica��o
	private Client client;
	private LoginDialog loginDialog;
	private CallDialog callDialog;
	private ClientListener clientListener;
	private PeerListener peerListener;
	private GroupClient groupClient;
	

	private P2PServer p2pServer;
	

	/**
	 * Initializes interface and set event listeners
	 * */
	public ClientFrame(){
		super("iChat");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(700, 500);
		this.setMinimumSize(new Dimension(700, 500));
		
		// Escuta P2P
		this.peerListener = new DefaultPeerListenerImpl();
		peerListener.setFrame(this);
		this.p2pServer = new P2PServer(peerListener);
		new Thread(this.p2pServer).start();
		
		this.getRegisterButton().addActionListener(new RegisterButtonListener(this));
		this.getCallButton().addActionListener(new CallButtonListener(this));
		
		this.getQuitButton().addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				getClient().unregister();
				System.exit(0);
				
			}
		});
		
		// Not so cool, but...
		this.getCreateGroupButton().addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String groupName = JOptionPane.showInputDialog("Enter group name:");
				if(groupName != null)
					groupClient.createGroup(groupName);
				
			}
		});
		
		this.getCallGroupButton().addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String groupName = getGroupsList().getSelectedValue().toString();
				groupClient.requestJoin(groupName);
				
				
				
				CallDialog l = new CallDialog(ClientFrame.this);
				setCallDialog(l);
				l.setLocationRelativeTo(ClientFrame.this);
				l.getLabel().setText("Calling " + groupName);
				l.setTitle("Call to " + groupName);
				l.setVisible(true);
				l.pack();
				
				
			}
		});
		
		this.getRefreshGroupsButton().addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				groupClient.requestGroupList();
				
			}
		});
		
		try {
			this.client = new Client();
			this.client.setPort(p2pServer.getLocalPort());
			this.clientListener = new DefaultClientListenerImpl(this);
			this.client.setListener(clientListener);
			
			this.groupClient = new GroupClient(client);
			this.groupClient.setListener(new DefaultGroupClientListenerImpl(this));
			
		} catch (UnknownHostException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
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
	}

	public CallDialog getCallDialog() {
		return callDialog;
	}

	public Client getClient(){
		return this.client;
	}
	
	public GroupClient getGroupClient(){
		return this.groupClient;
	}
		
	public PeerListener getPeerListener() {
		return peerListener;
	}

	public void setPeerListener(PeerListener peerListener) {
		this.peerListener = peerListener;
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
		l.setVisible(true);
		l.setModal(true);
		l.pack();
		this.getFrame().getCallButton().setEnabled(true);
		this.getFrame().getQuitButton().setEnabled(true);
		this.getFrame().getCreateGroupButton().setEnabled(true);
		this.getFrame().getCallGroupButton().setEnabled(true);
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
		c.call(s);
		
	}
	
}


