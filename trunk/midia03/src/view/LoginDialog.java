package view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class LoginDialog extends JDialog{
	private JPanel content;
	private JTextField loginField;
	private JTextField pwdField;
	private JButton okButton;
	private JButton cancelButton;
	
	public LoginDialog(){
		this.setContentPane(this.getContent());
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.setVisible(true);
		this.setTitle("Enter your credentials");
		
		//this.setModal(true);
		
	}

	public JPanel getContent() {
		if(content != null) return content;
		
		content = new JPanel();
		content.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(10, 10, 10, 10);
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.NONE;
		content.add(new JLabel("Login "), c);
		
		c.gridx = 1;
		c.gridy = 0;
		content.add(this.getLoginField(), c);
		
		c.gridx = 0;
		c.gridy = 1;
		content.add(new JLabel("Password "), c);
		
		c.gridx = 1;
		c.gridy = 1;
		content.add(this.getPwdField(), c);
		

		JPanel p = new JPanel();
		p.setLayout(new GridLayout(1,2));
		p.add(this.getOkButton());
		p.add(this.getCancelButton());
		
		c.gridx = 1;
		c.gridy = 2;
		content.add(p, c);
		
//		c.gridx = 2;
//		c.gridy = 2;
//		content.add(this.getCancelButton(), c);
		
//		content.setMinimumSize(new Dimension(400,400));
		
		return content;
	}

	public JTextField getLoginField() {
		if(loginField != null) return loginField;
		
		loginField = new JTextField();
		loginField.setColumns(20);
		return loginField;
	}

	public JTextField getPwdField() {
		if(pwdField != null) return pwdField;
		
		pwdField = new JTextField();
		pwdField.setColumns(20);
		
		
		return pwdField;
	}

	public JButton getOkButton() {
		if(okButton !=null ) return okButton;
		
		okButton = new JButton("OK");
		okButton.addActionListener( new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
				
			}
		});
		return okButton;
	}

	public JButton getCancelButton() {
		if(cancelButton != null) return cancelButton;
		
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				dispose();
			}
		});
		return cancelButton;
	}
	
}
