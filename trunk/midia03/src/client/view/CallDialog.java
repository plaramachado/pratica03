package client.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class CallDialog extends JDialog{
	private JPanel panel;
	private JLabel label;
	private ClientFrame frame;
	private JButton okButton;
	
	public CallDialog(ClientFrame frame) {
		this.frame = frame;
		
		this.setContentPane(this.getPanel());
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.setVisible(true);
		this.setTitle("Calling");
		this.setModal(true);
		frame.setCallDialog(this);
		//this.setFrame(frame);
		//frame.setLoginDialog(this);

	}
	
	public JPanel getPanel() {
		if(this.panel != null) return panel;
		
		panel = new JPanel();
		panel.add(this.getLabel());
		return panel;
	}

	public JLabel getLabel() {
		if(label != null) return label;
		
		label = new JLabel();
		
		return label;
	}

	public ClientFrame getFrame() {
		return frame;		
	}

	public JButton getOkButton() {
		if(okButton != null)return okButton;
		okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
			
		});
		return okButton;
	}
	
	

}
