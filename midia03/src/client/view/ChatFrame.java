package client.view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.Observable;
import java.util.Observer;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import util.ObservableArrayList;
import client.Message;
import client.P2P;

public class ChatFrame extends JInternalFrame implements Observer{
	private JPanel content;
	private JTextArea chatTextArea;
	private JTextArea messageTextArea;
	private JPanel chatPanel;
	private String caller;
	private JButton sendButton;
	private JButton playVideoButton;
	private JButton pauseVideoButton;
	
	
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
		this.getPlayVideoButton().addActionListener( new PlayVideoButtonListener(this));
		this.addInternalFrameListener(new CloseHandler(this));
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
		c.weightx = 2;
		c.gridwidth = 4;
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(0, 0, 10, 10);
		chatPanel.add(pane, c);
		
		pane = new JScrollPane(this.getMessageTextArea());
		pane.setMinimumSize(new Dimension(400, 70));
		
		c.weighty = 0.1;
		c.weightx = 1;
		c.gridwidth = 3;
		c.gridx = 0;
		c.gridy = 10;
		c.fill = GridBagConstraints.BOTH;
		chatPanel.add(pane, c);
		
		// Adiciona o botão de vídeo
		c.gridx = 3;
		c.gridy = 10;
		c.gridwidth = 1;
		chatPanel.add(this.getPlayVideoButton(), c);
		
		
		chatPanel.setBorder(BorderFactory.createEtchedBorder());
		
		return chatPanel;
	}
	
	public JButton getPlayVideoButton() {
		if(playVideoButton == null){
			playVideoButton = new JButton();
			playVideoButton.setIcon(createImageIcon("resources/icons/camera-web.png"));
			playVideoButton.setToolTipText("Start video stream");
		}
		return playVideoButton;
	}
	
	public BaseClientFrame getFrame() {
		return this.frame;
		
	}
	
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

	public void setP2P(P2P connection) {
		// TODO Auto-generated method stub
		this.p2pconnect = connection;
	}
	
}




// Event handlers


class CloseHandler implements InternalFrameListener{

	private ChatFrame chatFrame;
	
	public CloseHandler(ChatFrame frame){
		this.chatFrame = frame;
	}
	
	@Override
	public void internalFrameClosed(InternalFrameEvent e) {
		// TODO - logica para encerrar a conexao
		P2P p = this.chatFrame.p2pconnect;
		p.sendBye();
		
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

class PlayVideoButtonListener implements ActionListener{
	private ChatFrame chatFrame;
	public PlayVideoButtonListener(ChatFrame c){
		this.chatFrame = c;
	}
    public void actionPerformed(ActionEvent e){
    	P2P p2p = this.chatFrame.p2pconnect;
    	p2p.receiveVideo();
    	this.chatFrame.getPlayVideoButton().setEnabled(false); // HAHAHA só pode usar 1 vez 
    }
    
}