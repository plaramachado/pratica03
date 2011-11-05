package client.view;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.UnknownHostException;

import javax.swing.JFrame;

import client.Client;


public class ClientFrame extends BaseClientFrame{
	
	private Client client;

	/**
	 * Initializes interface and set event listeners
	 * */
	public ClientFrame(){
		super("iChat");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.getRegisterButton().addActionListener(new RegisterButtonListener(this));
		
		try {
			this.client = new Client();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Client getClient(){
		return this.client;
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
		LoginDialog l = new LoginDialog();
		l.setLocationRelativeTo(this.getFrame());
		l.pack();
		System.out.println("Event fired");
		
	}
	
}

class RegisterListener extends BaseListener{

	/**
	 * Fired when the user clicks in the 'OK' button 
	 * in the login frame.  
	 * */	
	public void actionPerformed(ActionEvent e) {
		
		Client c = this.getFrame().getClient();
		
		// Fasten seat belt. Doing risky things here.
		Component tf = (Component)(e.getSource());
		LoginDialog l = (LoginDialog)tf.getParent();
		
		c.setUserName(l.getLoginField().getText());
		c.setPassword(l.getPwdField().getText());
		
		c.register();
		
		
	}
	
}

class CallButtonListener extends BaseListener{

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
}