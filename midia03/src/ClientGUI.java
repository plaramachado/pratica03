import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.ScrollPane;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


public class ClientGUI extends JFrame{
	
	// Buttons
	private JButton registerButton;
	private JButton playVideoButton;
	private JButton pauseVideoButton;
	private JButton quitButton;
	
	// Panels
	private JPanel contactsPanel;
	private JPanel chatPanel;
	private JPanel menuPanel;
	private JPanel mainPanel;
	
	// Textboxes
	private JTextArea messageTextArea;
	private JTextArea chatTextArea;
	
	public static void main(String[] args) {
		ClientGUI c = new ClientGUI();
		c.setSize(new Dimension(400, 400));
		c.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		c.setVisible(true);
	}
	
	public ClientGUI(){
		this.getContentPane().setLayout(new GridLayout(1,1));
		
		this.getContentPane().add(this.getMainPanel());
		this.pack();
	}
	
	
	
	// Getters - construct the component if it does not exists
	public JButton getRegisterButton() {

		if(registerButton != null) return registerButton; 
			
		registerButton = new JButton();
		registerButton.setIcon(createImageIcon("resources/icons/nm-device-wired-secure.png"));
		registerButton.setToolTipText("Register");
		
		return registerButton;
	}
	public JButton getPlayVideoButton() {
		if(playVideoButton == null){
			playVideoButton = new JButton();
			playVideoButton.setIcon(createImageIcon("resources/icons/camera-web.png"));
			playVideoButton.setToolTipText("Start video stream");
		}
		return playVideoButton;
	}
	public JButton getPauseVideoButton() {
		if(pauseVideoButton != null) return pauseVideoButton;
		
		pauseVideoButton = new JButton();
		pauseVideoButton.setIcon(createImageIcon("resources/icons/player_pause.png"));
		pauseVideoButton.setToolTipText("Pause video");
		return pauseVideoButton;
	}
	public JButton getQuitButton() {
		if(quitButton != null) return quitButton;
		
		quitButton = new JButton();
		quitButton.setToolTipText("Quit");
		quitButton.setIcon(createImageIcon("resources/icons/system-shutdown-panel-restart.png"));
		return quitButton;
	}
	public JPanel getContactsPanel() {
		if(contactsPanel != null) return contactsPanel;
		
		contactsPanel = new JPanel();
		return contactsPanel;
	}
	
	public JTextArea getMessageTextArea() {
		if(messageTextArea != null) return messageTextArea;
		
		messageTextArea = new JTextArea();
		messageTextArea.setRows(3);
		messageTextArea.setColumns(50);
		messageTextArea.setEnabled(true);
		messageTextArea.setLineWrap(true);
		return messageTextArea;
	}
	
	public JTextArea getChatTextArea() {
		if(chatTextArea != null)return chatTextArea;
		chatTextArea = new JTextArea();
		chatTextArea.setEditable(false);
		chatTextArea.setRows(40);
		chatTextArea.setColumns(50);
		chatTextArea.setPreferredSize(new Dimension(400, 400));
		chatTextArea.setLineWrap(true);
		return chatTextArea;
	}
	
	public JPanel getChatPanel() {
		
		if(chatPanel != null) return chatPanel;
		
		chatPanel = new JPanel();
		chatPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		//chatPanel.setLayout( null);
		
		JScrollPane pane;
		
		pane = new JScrollPane(this.getChatTextArea());
		pane.setMinimumSize(new Dimension(400, 300));
		
		c.weighty = 0.9;
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		chatPanel.add(pane, c);
		
		pane = new JScrollPane(this.getMessageTextArea());
		pane.setMinimumSize(new Dimension(400, 70));
		
		c.weighty = 0.1;
		c.gridx = 0;
		c.gridy = 10;
		c.fill = GridBagConstraints.BOTH;
		chatPanel.add(pane, c);
		
		chatPanel.setBorder(BorderFactory.createEtchedBorder());
		return chatPanel;
	}
	public JPanel getMenuPanel() {
		
		if(menuPanel != null) return menuPanel;
		
		menuPanel = new JPanel();
		GridLayout g = new GridLayout(5, 1);
		g.setVgap(10);
		
		menuPanel.setLayout( g );
		menuPanel.setBorder(BorderFactory.createEtchedBorder());
		
		menuPanel.add(this.getRegisterButton());
		
		menuPanel.add(this.getPlayVideoButton());
		menuPanel.add(this.getPauseVideoButton());
		menuPanel.add(this.getQuitButton());
		
		return menuPanel;
	}
	
	public JPanel getMainPanel() {
		if(mainPanel != null) return mainPanel;
		
		mainPanel = new JPanel();
		GridBagConstraints c = new GridBagConstraints();
		GridBagLayout l = new GridBagLayout();
		mainPanel.setLayout(l);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.1;
		mainPanel.add(this.getMenuPanel(), c);
		
		c.weightx = 0.65;
		//c.fill = GridBagConstraints.HORIZONTAL;
		c.fill = GridBagConstraints.BOTH;
		//c.gridwidth = 5;
		mainPanel.add(this.getChatPanel(), c);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.25;
		mainPanel.add(this.getContactsPanel(), c);
		
		return mainPanel;
		
	}
	
	
	
	
	
	
	
	protected ImageIcon createImageIcon(String path) {
//		java.net.URL imgURL = getClass().getResource(path);
//		if (imgURL != null) {
//		return new ImageIcon(imgURL, description);
//		} else {
//		System.err.println("Couldn't find file: " + path);
//		return null;
//		}
		
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream input = classLoader.getResourceAsStream(path);
		Image logo;
		try {
			logo = ImageIO.read(input);
			return new ImageIcon(logo);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}
	
	

}
