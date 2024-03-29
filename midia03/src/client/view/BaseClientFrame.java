package client.view;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Scrollbar;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
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

import client.Client;
import client.GroupClient;

/**
 * Client main window.
 * See action listeners for details
 * 
 * */
public class BaseClientFrame extends JFrame{
	
	// Buttons
	private JButton registerButton;
	private JButton quitButton;
	private JButton callButton;
	private JButton endCallButton;
	private JButton callGroupButton;
	private JButton createGroupButton;
	private JButton refreshGroupsButton;
	
	// Panels
	private JPanel contactsPanel;
	private JPanel menuPanel;
	private JPanel mainPanel;
	
	// Desktop panes
	private JDesktopPane chatPanel;
	
	// Textboxes
	private JTextArea messageTextArea;
	private JTextArea chatTextArea;
	
	// Lists
	private MutableList contactsList;
	private MutableList groupsList;
	
	// Internal frames
	private Map<String, JInternalFrame> chatWindows; // All active chat windows.
	
	
	public static void main(String[] args) {
		BaseClientFrame c = new BaseClientFrame("iChat");
		c.setSize(new Dimension(500, 500));
		c.setMinimumSize(new Dimension(500, 500));
		c.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		c.setVisible(true);
	}
	
	public BaseClientFrame(String title){
		super(title);
		//this.setLayout(new GridBagLayout());
		//GridBagConstraints c = new GridBagConstraints();
		//c.gridx = 0;
		//c.gridy = 0;
		this.getMainPanel().setMinimumSize(new Dimension(400,400));
		this.getContentPane().add(this.getMainPanel(), BorderLayout.CENTER);
		this.getCallButton().setEnabled(false);
		this.getCallGroupButton().setEnabled(false);
		this.getQuitButton().setEnabled(false);
		this.getCreateGroupButton().setEnabled(false);
		
		//this.getChatWindows();
		
		//Tenta alterar o look and feel
//		try {
//		    UIManager.setLookAndFeel("com.seaglasslookandfeel.SeaGlassLookAndFeel");
//		    
//		} catch (Exception e) {
//			System.out.println("Coudnt change look and feel");			
//		}
		
		
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
		return callButton;
	}

	public JButton getEndCallButton() {
		if(endCallButton != null) return endCallButton;
		
		endCallButton = new JButton();
		endCallButton.setIcon(createImageIcon("resources/icons/call-stop.png"));
		endCallButton.setToolTipText("End call");
		return endCallButton;
	}

	public JButton getCreateGroupButton(){
		if(this.createGroupButton != null) return createGroupButton;
		
		createGroupButton = new JButton();
		createGroupButton.setIcon(createImageIcon("resources/icons/config-users.png"));
		createGroupButton.setToolTipText("Create group");
		return createGroupButton;
	}
	
	public JButton getRefreshGroupsButton(){
		if(this.refreshGroupsButton != null) return refreshGroupsButton;
		
		refreshGroupsButton = new JButton("R");
		return refreshGroupsButton;
	}
	
	public JPanel getContactsPanel() {
		if(contactsPanel != null) return contactsPanel;
		
		JScrollPane scroller = new JScrollPane(this.getContactsList());
		scroller.setMinimumSize(new Dimension(130, 400));
		scroller.setSize(new Dimension(100, 400));
		
		contactsPanel = new JPanel();
		contactsPanel.setMinimumSize(new Dimension(100,100));
		contactsPanel.setSize(new Dimension(100,100));
		contactsPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		c.fill = c.VERTICAL;
		c.gridx = 0;
		c.gridy = 0;
		scroller.setBorder(BorderFactory.createTitledBorder("Contacts"));
		contactsPanel.add(scroller, c);
		
		c.gridx = 0;
		c.gridy = 1;
		c.insets = new Insets(10, 0, 0, 0);
		contactsPanel.add(this.getCallButton(), c);
		
		scroller = new JScrollPane(this.getGroupsList());
		scroller.setMinimumSize(new Dimension(130, 400));
		scroller.setSize(new Dimension(100, 400));
		
		c.fill = c.VERTICAL;
		c.gridx = 0;
		c.gridy = 2;
		scroller.setBorder(BorderFactory.createTitledBorder("Groups"));
		contactsPanel.add(scroller, c);
		
		
		c.gridx = 0;
		c.gridy = 3;
		c.insets = new Insets(10, 0, 0, 0);
		contactsPanel.add(this.getCallGroupButton(), c);
		
		c.gridx = 1;
		c.gridy = 3;
		c.insets = new Insets(10, 0, 0, 0);
		contactsPanel.add(this.getCreateGroupButton(), c);
		
		c.gridx = 0;
		c.gridy = 4;
		c.insets = new Insets(10, 0, 0, 0);
		contactsPanel.add(this.getRefreshGroupsButton(), c);
		
		return contactsPanel;
	}
	
	
	
	
	public JButton getCallGroupButton() {
		if(this.callGroupButton != null) return callGroupButton;
		callGroupButton = new JButton();
		callGroupButton.setIcon(createImageIcon("resources/icons/call-start.png"));
		callGroupButton.setToolTipText("Call group");
		return callGroupButton;
	}

	public JDesktopPane getChatPanel() {
		
		if(chatPanel != null) return chatPanel;
		
		chatPanel = new JDesktopPane();
		
		return chatPanel;
	}
	public JPanel getMenuPanel() {
		
		if(menuPanel != null) return menuPanel;
		
		menuPanel = new JPanel();
		//GridLayout g = new GridLayout(15, 1);
		//g.setVgap(10);
		
		GridBagConstraints c = new GridBagConstraints();
		menuPanel.setLayout( new GridBagLayout());
		menuPanel.setBorder(BorderFactory.createEtchedBorder());
		c.fill = c.VERTICAL;
		c.insets = new Insets(10, 10, 10, 10);
		c.gridx = 0;
		c.gridy = 0;
		menuPanel.add(this.getRegisterButton(), c);
		
		//c.gridy = 1;
		//menuPanel.add(this.getPlayVideoButton(), c);
		//c.gridy = 2;
		//menuPanel.add(this.getPauseVideoButton(), c);
		
		c.gridy = 1;
		menuPanel.add(this.getQuitButton(), c);
		
		c.gridy = 2;
		c.fill  = c.BOTH;
		c.weighty = 10;
		c.gridheight = 40;
		c.ipady = 300;
		menuPanel.add(new JPanel(), c);
		
		return menuPanel;
	}
	
	public JPanel getMainPanel() {
		if(mainPanel != null) return mainPanel;
		
		mainPanel = new JPanel();
		GridBagConstraints c = new GridBagConstraints();
		GridBagLayout l = new GridBagLayout();
		mainPanel.setLayout(l);
		
		c.fill = GridBagConstraints.BOTH;
		
		c.weightx = 0.1;
		c.gridx = 0;
		c.gridy = 0;
		mainPanel.add(this.getMenuPanel(), c);
		
		c.weightx = 0.65;
		c.gridx = 1;
		c.gridy = 0;
		
		//c.fill = GridBagConstraints.HORIZONTAL;
		//c.fill = GridBagConstraints.BOTH;
		//c.gridwidth = 5;
		mainPanel.add(this.getChatPanel(), c);
		c.ipady = 0;
		
		c.gridx = 2;
		c.gridy = 0;
		//c.fill = GridBagConstraints.BOTH;
		c.weightx = 0.25;
		mainPanel.add(this.getContactsPanel(), c);
		
		return mainPanel;
		
	}
	
	public MutableList getContactsList() {
		if(contactsList != null) return contactsList;
		
		contactsList = new MutableList();
		contactsList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		contactsList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		//contactsList.setVisibleRowCount(-1);
		//contactsList.setSize(new Dimension(100,100));
		
		// Render the list with icon
		contactsList.setCellRenderer(new ListCellRenderer() {
			private DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();
			@Override
			public Component getListCellRendererComponent(JList list, Object value,
					int index, boolean isSelected, boolean cellHasFocus) {
				JLabel renderer = (JLabel) defaultRenderer.getListCellRendererComponent(list, value, index,
				        isSelected, cellHasFocus);
				renderer.setIcon(createImageIcon("resources/icons/online2.jpg"));
				return renderer;
			}
		});
		
		return contactsList;
	}
	
	public MutableList getGroupsList() {
		if(groupsList != null) return groupsList;
		
		groupsList = new MutableList();
		groupsList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		groupsList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		groupsList.setVisibleRowCount(-1);
		
		// Render the list with icon
		contactsList.setCellRenderer(new ListCellRenderer() {
			private DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();
			@Override
			public Component getListCellRendererComponent(JList list, Object value,
					int index, boolean isSelected, boolean cellHasFocus) {
				JLabel renderer = (JLabel) defaultRenderer.getListCellRendererComponent(list, value, index,
				        isSelected, cellHasFocus);
				renderer.setIcon(createImageIcon("resources/icons/online2.jpg"));
				return renderer;
			}
		});
		
		return groupsList;
	}
	
//	@Deprecated
//	public Map<String, JInternalFrame> getChatWindows() {
//		if(chatWindows != null) return chatWindows;
//		
//		chatWindows = new HashMap<String, JInternalFrame>();
//		return chatWindows;
//	}

	/**
	 * Creates a new chat frame. 
	 * Called when one new conversation is initiated.
	 * */
	public ChatFrame createChatFrame(String caller){
		// TO DO Considerar possibilidade de ja existir
		// um frame associado a esta liga��o
		return createChatFrame(caller, true);
	}
	
	public ChatFrame createChatFrame(String caller, boolean setEventHandlers){
		ChatFrame c = new ChatFrame(this);
		c.setTitle("Chat with " + caller);
		c.setVisible(true);
		c.moveToFront();
		c.setSize(400, 400);
		c.setCaller(caller);
		this.getChatPanel().add(c);
		this.repaint();
		return c;
	}
	
	public GroupChatFrame createGroupChatFrame(String group){
		GroupChatFrame c = new GroupChatFrame(this);
		c.setTitle("Group Chat with " + group);
		c.setVisible(true);
		c.moveToFront();
		c.setSize(400, 400);
		c.setCaller(group);
		this.getChatPanel().add(c);
		this.repaint();
		return c;
	}
	
	public Client getClient(){ return null;} // oOps
	public GroupClient getGroupClient(){ return null;} // oOps[2]

	protected ImageIcon createImageIcon(String path) {
		
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

// Exemplos de listeners
class BaseCallButtonListener implements ActionListener{
	private BaseClientFrame c;
	public BaseCallButtonListener(BaseClientFrame c){
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