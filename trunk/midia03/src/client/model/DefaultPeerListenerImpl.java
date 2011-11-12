package client.model;

import java.util.Map;

import client.Client;
import client.MessageListener;
import client.P2P;
import client.PeerListener;
import client.view.ChatFrame;
import client.view.ClientFrame;

public class DefaultPeerListenerImpl implements PeerListener{
	
	private ClientFrame frame;

	@Override
	public void setFrame(ClientFrame clientFrame) {

		this.frame = clientFrame;
	}

	@Override
	/**
	 * Chamado quando uma nova conexão é estabelecida,
	 * não importa qual das partes iniciou.
	 * É responsável por criar as janelas de chat
	 * */
	public void gotP2P(P2P peer) {
		// OK, temos um nó aqui =P
		ChatFrame c = frame.createChatFrame("unknown");
		MessageListener ml = new DefaultMessageListenerImpl(c);
		peer.setMessageListener(ml);
		c.setP2P(peer);
		
	}		

}
