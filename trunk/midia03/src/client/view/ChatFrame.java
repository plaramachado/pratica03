package client.view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyVetoException;
import java.security.Key;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import util.ObservableArrayList;
import client.Message;
import client.P2P;
import client.model.ClientInfo;

public class ChatFrame extends JInternalFrame implements Observer{
	private JPanel content;
	private JTextArea chatTextArea;
	private JTextArea messageTextArea;
	private JPanel chatPanel;
	private String caller;
	private JButton sendButton;
	private BaseClientFrame frame;
	public P2P p2pconnect;
	
	
	public ChatFrame(BaseClientFrame cf){
		this.frame = cf;
		this.setPreferredSize(new Dimension(400,400));
		this.setResizable(true);
		this.setIconifiable(true);
		this.setMaximizable(true);
		this.setClosable(true);
		this.getContentPane().add(this.getChatPanel());
		this.getMessageTextArea().addKeyListener(new EnterHitHandler(this));
		this.addInternalFrameListener(new CloseHandler());
//		try {
//			this.setMaximum(true);
//		} catch (PropertyVetoException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
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
	
	public JTextArea getMessageTextArea() {
		if(messageTextArea != null) return messageTextArea;
		
		messageTextArea = new JTextArea();
		messageTextArea.setRows(3);
		messageTextArea.setColumns(50);
		messageTextArea.setEnabled(true);
		messageTextArea.setLineWrap(true);
		return messageTextArea;
	}
	
	
	public JPanel getChatPanel(){
		
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
	
	public BaseClientFrame getFrame() {
		return this.frame;
		
	}
	
	public void setCaller(String caller){
		this.caller = caller;
	}
	
	public String getCaller(){
		return this.caller;
	}
	
	
	
	@Override
	/**
	 * Método chamado quando acontece algo de interessante com os 
	 * objetos que as instâncias dessa classe observam.
	*/
	public void update(Observable arg0, Object arg1) {
		if(arg1 instanceof ObservableArrayList){
			ObservableArrayList<Message> l = (ObservableArrayList<Message>) arg1;
			String s = this.getChatTextArea().getText();
			s = s + "\n" + caller + ": " + l.iterator().next();
			this.getChatTextArea().setText(s);
			System.out.println(caller + " says " + l.iterator().next());
		}
		
	}

	@Deprecated
	public void P2PCreate(P2P connection) {
		// TODO Auto-generated method stub
		this.frame.getClient().acceptCall();
		
//		ChatFrame c = this.frame.createChatFrame(caller);
		
//		Map<String, ClientInfo> peers = this.frame.getPeers();
		ClientInfo info = new ClientInfo();
		info.setChatFrame(this);
		setP2P(connection);
		connection.requestP2P();
		System.out.println("CALLED REQUESTP2P FOR SUUUUURE");
//		connection.requestP2P();
//		peers.put(caller, info);
		
		/* to p2p connection */
//		Map<String, P2P> connections = this.frame.getClient().getConnectionsP2P();
//		System.out.println("caller: "+caller);
//		connections.get(caller).requestP2P();
		
//		this.frame.getCallDialog().dispose();
	}

	public void setP2P(P2P connection) {
		// TODO Auto-generated method stub
		this.p2pconnect = connection;
	}
	
}




// Event handlers


class CloseHandler implements InternalFrameListener{

	@Override
	public void internalFrameClosed(InternalFrameEvent e) {
		// TODO - logica para encerrar a conexao
		
	}
	
	public void internalFrameActivated(InternalFrameEvent e) {}
	
	public void internalFrameClosing(InternalFrameEvent e) {}

	public void internalFrameDeactivated(InternalFrameEvent e) {}

	public void internalFrameDeiconified(InternalFrameEvent e) {}

	public void internalFrameIconified(InternalFrameEvent e) {}

	public void internalFrameOpened(InternalFrameEvent e) {}
	
}


class EnterHitHandler implements KeyListener{
	
	private ChatFrame chatFrame;
	
	public EnterHitHandler(ChatFrame f){
		this.chatFrame = f;
	} 
	@Override
	public void keyPressed(KeyEvent e) {
		// TODO enviar a mensagem para o cliente
		if(e.getKeyCode() == KeyEvent.VK_ENTER){

			String msg = chatFrame.getMessageTextArea().getText().trim();
			chatFrame.p2pconnect.sendMessage(msg + "\r\n");
			chatFrame.getMessageTextArea().setText("");
			chatFrame.getChatTextArea().append("\nMe: " + msg);
			
		}
		
	}

	public void keyReleased(KeyEvent e) {}

	public void keyTyped(KeyEvent e) {}
	
}