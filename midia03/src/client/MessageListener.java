package client;

/**
 * Interface que representa objetos interessados
 * em eventos ligdos � recep��o e envio de
 * mensagens.
 * */

public interface MessageListener {
	
	public void onMessageReceived(String message);
	
	

}
