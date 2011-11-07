package client;

import java.io.IOException;

public class Main {
	/**
	 * This main method will not be called regularly, there will be another class that starts up the GUI
	 * and does something similar to what this method does, filling password, username, calling register
	 * and other stuff.
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws Exception{
		pedro();
	//	tiago();
//		Thread.sleep(1000);
//		tiago.call(pedro.getUserName());
		
	}
	public static void tiago() throws Exception{
		Client tiago = createClient("tiago", "123");
		P2P tiagop2p = new P2P();
		tiagop2p.start();
		tiagop2p.gotP2P("localhost", 5052,5054);
		System.out.println("fui!");
		tiagop2p.receiveVideo();
		System.out.println("receiving");
		tiagop2p.sendVideo();
		
	}
	public static void pedro() throws Exception{
		Client pedro = createClient("pedro", "123");
		P2P pedrop2p = new P2P();
		pedrop2p.start();
		pedrop2p.gotP2P("localhost", 5054,5052);
		pedrop2p.sendVideo();
		//Thread.sleep(20000);
		pedrop2p.receiveVideo();
		
	}
	public static Client createClient(String name, String password) throws IOException{
		Client client = new Client();
		client.setPassword(password);
		client.setUserName(name);
		client.register();
		return client;
	}
}
