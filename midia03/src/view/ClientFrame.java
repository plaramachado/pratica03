package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;


public class ClientFrame extends BaseClientFrame{
	

	/**
	 * Initializes interface and set event listeners
	 * */
	public ClientFrame(){
		super("iChat");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.getRegisterButton().addActionListener(new RegisterButtonListener(this));
	}
	
	public static void main(String[] args){
		new ClientFrame().setVisible(true);
	}
}






///////////////////////////////////////////
// TODO - Maybe move to separate source files

abstract class BaseListener implements ActionListener{
	private BaseClientFrame frame;
	public BaseListener(BaseClientFrame frame){
		this.setFrame(frame);
	}
	public BaseListener(){
		
	}
	public void setFrame(BaseClientFrame frame) {
		this.frame = frame;
	}
	public BaseClientFrame getFrame() {
		return frame;
	}
}


class RegisterButtonListener extends BaseListener{

	public RegisterButtonListener(BaseClientFrame c){
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

	@Override
	public void actionPerformed(ActionEvent e) {
		// Register in the server
		
		
	}
	
}

class CallButtonListener extends BaseListener{

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
}