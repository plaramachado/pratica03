package client.view;

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

public class GroupChatFrame extends ChatFrame {
	

	public GroupChatFrame(BaseClientFrame cf) {
		super(cf, false);
		this.setEventHandlers();
	}
	
	public void setEventHandlers(){
		
		this.getMessageTextArea().addKeyListener( new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent arg0) {}
			public void keyReleased(KeyEvent arg0) {}
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER){

					String msg = getMessageTextArea().getText().trim();
					getFrame().getGroupClient().groupText(msg);
					
					getMessageTextArea().setText("");
					getChatTextArea().append("\nMe: " + msg);
					
				}
			}
		});
		
		this.addInternalFrameListener(new InternalFrameListener() {
			
			public void internalFrameOpened(InternalFrameEvent arg0) {}
			
			public void internalFrameIconified(InternalFrameEvent arg0) {}
			
			public void internalFrameDeiconified(InternalFrameEvent arg0) {}
			
			public void internalFrameDeactivated(InternalFrameEvent arg0) {}
			
			public void internalFrameClosing(InternalFrameEvent arg0) {}
			
			@Override
			public void internalFrameClosed(InternalFrameEvent arg0) {
					JOptionPane.showConfirmDialog(null, "terminar isso");
				
			}
			
			public void internalFrameActivated(InternalFrameEvent arg0) {}
		});
		
		
		
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JFrame f = new JFrame();
		JDesktopPane pane = new JDesktopPane();
		f.setVisible(true);
		GroupChatFrame c = new GroupChatFrame(null);
		//ChatFrame c = new ChatFrame(null);
		c.setMinimumSize(new Dimension(400,400));
		c.setSize(new Dimension(400,400));
		c.setVisible(true);
		c.setMaximizable(true);
		pane.add(c);
		f.getContentPane().add(pane);
		f.pack();
		f.repaint();
		f.setSize(500, 500);
		c.moveToFront();
		f.pack();

	}

}
