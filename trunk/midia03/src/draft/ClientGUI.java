package draft;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;


public class ClientGUI extends JFrame{
	
	// Buttons
	private JButton registerButton;
	private JButton playVideoButton;
	private JButton pauseVideoButton;
	private JButton quitButton;
	private JButton callButton;
	private JButton endCallButton;
	
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
		
		//Tenta alterar o look and feel
		try {
		    UIManager.setLookAndFeel("com.seaglasslookandfeel.SeaGlassLookAndFeel");
		    
		} catch (Exception e) {
			System.out.println("Coudnt change look and fell");
			
		    //e.printStackTrace();
		}
		
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
	public JButton getCallButton() {
		if(callButton != null) return callButton;
		
		callButton = new JButton();
		callButton.setIcon(createImageIcon("resources/icons/call-start.png"));
		callButton.setToolTipText("Call selected");
		callButton.addActionListener(new CallButtonListener(this));
		return callButton;
	}

	public JButton getEndCallButton() {
		if(endCallButton != null) return endCallButton;
		
		endCallButton = new JButton();
		endCallButton.setIcon(createImageIcon("resources/icons/call-stop.png"));
		endCallButton.setToolTipText("End call");
		return endCallButton;
	}

	public JPanel getContactsPanel() {
		if(contactsPanel != null) return contactsPanel;
		
		JScrollPane scroller = new JScrollPane(this.getContactsList());
		scroller.setMinimumSize(new Dimension(130, 300));
		
		contactsPanel = new JPanel();
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridx = 0;
		c.gridy = 0;
		contactsPanel.setLayout(new GridBagLayout());
		contactsPanel.add(scroller, c);
		
		c.gridx = 0;
		c.gridy = 1;
		c.insets = new Insets(10, 0, 0, 0);
		contactsPanel.add(this.getCallButton(), c);
		
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
			private DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();
			@Override
			public Component getListCellRendererComponent(JList list, Object value,
					int index, boolean isSelected, boolean cellHasFocus) {
				// TODO Auto-generated method stub
				JLabel renderer = (JLabel) defaultRenderer.getListCellRendererComponent(list, value, index,
				        isSelected, cellHasFocus);
				renderer.setIcon(createImageIcon("resources/icons/online2.jpg"));
				//renderer.setVerticalTextPosition(JLabel.RIGHT);
				return renderer;
				//return new JLabel( value.toString() , , JLabel.LEFT);
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



// A classe que implementa o frame para as conversas
class ChatFrame extends JInternalFrame{
	private JPanel mainPanel;
	
	public ChatFrame(String title){
		super(title);
	}
}



// Exemplos de listeners
class CallButtonListener implements ActionListener{
	private ClientGUI c;
	public CallButtonListener(ClientGUI c){
		this.c = c;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		JList l = c.getContactsList();
		int[] i = l.getSelectedIndices();
		Object[] v = l.getSelectedValues();
		String s = "";
		
		for(int k=0; k<v.length; k++){
			s +=  v[k].toString() + ", ";
		}
		
		JOptionPane.showConfirmDialog(null, "Calling " + s );
		
		
	}
	
}