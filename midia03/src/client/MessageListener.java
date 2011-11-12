package client;

/**
 * Interface que representa objetos interessados
 * em eventos ligdos à recepção e envio de
 * mensagens.
 * */

public interface MessageListener {
	
	public void onMessageReceived(String message);
	
	

}
