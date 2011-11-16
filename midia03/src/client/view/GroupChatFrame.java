package client.view;

import java.awt.Dimension;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;

public class GroupChatFrame extends ChatFrame {
	

	public GroupChatFrame(BaseClientFrame cf) {
		super(cf);
		// TODO Auto-generated constructor stub
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
