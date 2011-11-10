package client.model;

import java.util.List;

import javax.swing.JInternalFrame;

import client.Message;
import client.P2P;

public class ClientInfo {
	private JInternalFrame chatFrame;
	private List<Message> messageBuffer;
	private P2P p2p;
	
	public void setMessageBuffer(List<Message> messageBuffer) {
		this.messageBuffer = messageBuffer;
	}
	public List<Message> getMessageBuffer() {
		return messageBuffer;
	}
	public void setChatFrame(JInternalFrame chatFrame) {
		this.chatFrame = chatFrame;
	}
	public JInternalFrame getChatFrame() {
		return chatFrame;
	}
	public void setP2p(P2P p2p) {
		this.p2p = p2p;
	}
	public P2P getP2p() {
		return p2p;
	}
	

}
