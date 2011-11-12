package client;

import java.io.IOException;

import server.MasterServer;

public class Main {
	/**
	 * This main method will not be called regularly, there will be another class that starts up the GUI
	 * and does something similar to what this method does, filling password, username, calling register
	 * and other stuff.
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws Exception{
//video
		//	pedro(); //executa a main com esse cara
	//	tiago(); //depois só com esse
//		Thread.sleep(1000);
//		tiago.call(pedro.getUserName());
		
		
		//texto
		//texto();
		
		//testando call p2p
//		Client c1 = createClient("c1","123");
//		P2P p1 = new P2P("localhost",MasterServer.serverPort);
//		//c1.setP2plistener(p1);
//		Client c2 = createClient("c2","123");
//		P2P p2 = new P2P("localhost",MasterServer.serverPort);
//		//c2.setP2plistener(p2);
//		
//		c1.call("c2");
//		//System.out.println("ok?");
//		Thread.sleep(1000);
//		p2.requestP2P();
//	}
//	public void texto() throws Exception{
//		P2P bainca = bainca();
//		P2P felipe = felipe();
//		bainca.receiveMessageLoop();
//		felipe.receiveMessageLoop();
//		Thread.sleep(1000);
//		bainca.sendMessage(new Message("ola felipe, eu sou o bainca!"));
//		bainca.sendMessage(new Message("alo?! tem alguem ai?"));
//		bainca.sendMessage(new Message("felipee!"));
//		felipe.sendMessage(new Message("ola, bainca, tava ocupado bla bla bla."));
//		
//		Message msg;
//		while ((msg = felipe.getMessage()) != null){
//			System.out.println(msg);
//		}
//		while ((msg = bainca.getMessage()) != null){
//			System.out.println(msg);
//		}
//		Thread.sleep(10000);
//	}
//	public static P2P bainca() throws Exception{
//		Client c = createClient("bainca","123");
//		P2P p2p = new P2P();
//		p2p.start();
//		//p2p.gotP2P("localhost", 5052,5054,5056, 6000,6001);
//		return p2p;
//	}
//	public static P2P felipe() throws Exception{
//		Client c = createClient("felipe","123");
//		P2P p2p = new P2P();
//		p2p.start();
//		//p2p.gotP2P("localhost", 5054,5052,5058,6001,6000);
//		return p2p;
//	}
//	public static void tiago() throws Exception{
//		Client tiago = createClient("tiago", "123");
//		P2P tiagop2p = new P2P();
//		tiagop2p.start();
//		//tiagop2p.gotP2P("localhost", 5052,5054,5056, 6000,6001); //ip do outro cliente
//		System.out.println("fui!");
//		tiagop2p.receiveVideo();
//		System.out.println("receiving");
//		tiagop2p.sendVideo();
		
	}
	public static void pedro() throws Exception{
//		Client pedro = createClient("pedro", "123");
//		P2P pedrop2p = new P2P();
//		pedrop2p.start();
//		//pedrop2p.gotP2P("localhost", 5054,5052,5058,7000,7001); //ip do outro cliente
//		pedrop2p.sendVideo();
//		Thread.sleep(20000);
//		pedrop2p.receiveVideo();
		
	}
	public static Client createClient(String name, String password) throws IOException{
		Client client = new Client();
		client.setPassword(password);
		client.setUserName(name);
		client.register();
		return client;
	}
}
