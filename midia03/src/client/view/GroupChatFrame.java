package client.view;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

public class GroupChatFrame extends ChatFrame {
	
	private boolean isOwner;

	public GroupChatFrame(BaseClientFrame cf) {
		super(cf, false);
		this.setEventHandlers();
	}
	
	public void setEventHandlers(){
		
		this.getMessageTextArea().addKeyListener( new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				
			}
			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER){

					String msg = getMessageTextArea().getText().trim().trim();
					if(msg.isEmpty()) return;
					getFrame().getGroupClient().groupText(msg);
					System.out.println("ENVIANDO PRO SERVIDOR" + msg);
					getMessageTextArea().setText("");
					//getChatTextArea().append("\nMe: " + msg);
					
				}
			}
			public void keyPressed(KeyEvent e) {}
		});
		
		this.addInternalFrameListener(new InternalFrameListener() {
			
			public void internalFrameOpened(InternalFrameEvent arg0) {}
			
			public void internalFrameIconified(InternalFrameEvent arg0) {}
			
			public void internalFrameDeiconified(InternalFrameEvent arg0) {}
			
			public void internalFrameDeactivated(InternalFrameEvent arg0) {}
			
			public void internalFrameClosing(InternalFrameEvent arg0) {}
			
			@Override
			public void internalFrameClosed(InternalFrameEvent arg0) {
				if(isOwner){
					getFrame().getGroupClient().groupClose();
				}else{
					getFrame().getGroupClient().groupLeave();
				}
					//JOptionPane.showConfirmDialog(null, "terminar isso");
					
				
			}
			
			public void internalFrameActivated(InternalFrameEvent arg0) {}
		});
		
		
		this.getPlayVideoButton().addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// FIXME NAO FUNCIONA :d
				getFrame().getGroupClient().sendVideo();
				
			}
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

	public void setOwner(boolean isOwner) {
		this.isOwner = isOwner;
	}

	public boolean isOwner() {
		return isOwner;
	}

}
