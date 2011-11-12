package client.model;

import client.MessageListener;
import client.view.ChatFrame;

public class DefaultMessageListenerImpl implements MessageListener {
	private ChatFrame chatFrame;
	
	DefaultMessageListenerImpl(ChatFrame chatFrame){
		this.chatFrame = chatFrame;
	}
	@Override
	public void onMessageReceived(String message) {
		chatFrame.getChatTextArea().append("\nHe/She/It: " + message);

	}

}
