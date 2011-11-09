package client;

public class Message {

	private String content; //conteudo da mensagem
	
	public Message(String content){
		this.content = content;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String toString(){
		return content;
		
	}
}
