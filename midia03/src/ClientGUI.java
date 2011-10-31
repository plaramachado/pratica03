import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.ScrollPane;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;


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
	
	// Lists
	private JList contactsList;
	
	public static void main(String[] args) {
		ClientGUI c = new ClientGUI();
		c.setSize(new Dimension(500, 500));
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
		
		JScrollPane scroller = new JScrollPane(this.getContactsList());
		scroller.setMinimumSize(new Dimension(130, 300));
		
		contactsPanel = new JPanel();
		contactsPanel.setLayout(new GridLayout(1,1));
		contactsPanel.add(scroller);
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
		c.weightx = 1;
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(0, 0, 10, 10);
		chatPanel.add(pane, c);
		
		pane = new JScrollPane(this.getMessageTextArea());
		pane.setMinimumSize(new Dimension(400, 70));
		
		c.weighty = 0.1;
		c.weightx = 1;
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
		c.gridx = 0;
		c.gridy = 0;
		mainPanel.add(this.getMenuPanel(), c);
		
		c.weightx = 0.65;
		c.gridx = 1;
		c.gridy = 0;
		//c.fill = GridBagConstraints.HORIZONTAL;
		c.fill = GridBagConstraints.BOTH;
		//c.gridwidth = 5;
		mainPanel.add(this.getChatPanel(), c);
		
		c.gridx = 2;
		c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.25;
		mainPanel.add(this.getContactsPanel(), c);
		
		return mainPanel;
		
	}
	
	public JList getContactsList() {
		if(contactsList != null) return contactsList;
		Object[] data = new Object[4];
		data[0] = "Felipe";
		data[1] = "Pedro";
		data[2] = "Sainte";
		data[3] = "Vanessa";
		
		
		contactsList = new JList(data);
		contactsList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		contactsList.setLayoutOrientation(JList.VERTICAL_WRAP);
		contactsList.setVisibleRowCount(-1);
		
		// Render the list with icon
		contactsList.setCellRenderer(new ListCellRenderer() {
			
			@Override
			public Component getListCellRendererComponent(JList list, Object value,
					int index, boolean isSelected, boolean cellHasFocus) {
				// TODO Auto-generated method stub
				
				return new JLabel( value.toString() , createImageIcon("resources/icons/online2.jpg"), JLabel.LEFT);
			}
		});
		
		return contactsList;
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
