package video.unicast;


/* ------------------
   Server
   usage: java Server [RTSP listening port]
   ---------------------- */


import java.io.*;
import java.net.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import java.util.List;

/**
 * Classe principal, que gerencia a interface grï¿½fica e o tratamento inicial das conexï¿½es
 * dos clientes.
 * */

public class P2PVideoServer implements Observer{

	//private Socket client;
	private int localRTSPport;	
	private String localIP;	
	private ServerSocket listenSocket = null;	
	private int localRTSPPort;
	private List<RTSPThread> threads; // Todas as threads iniciadas por este servidor

	/**
	 * Inicia o socket de escuta das requisições RSTP.
	 * */
	public P2PVideoServer(){
		
		threads = new ArrayList<RTSPThread>();
		try {
			listenSocket = new ServerSocket(0);
		} catch (IOException e) {
			System.out.println("P2PVideoServer Error: could not start listening: " + e.getMessage());			
		}
		System.out.println("P2PServerVideo listening on port " + listenSocket.getLocalPort());

		try {
			this.localIP = InetAddress.getLocalHost().getHostAddress();			
			setLocalRTSPPort(listenSocket.getLocalPort());
			
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
			
		}
		
		Thread t = new Thread(){
			public void run() {
				waitForRTSPClient(); // Inicia a repecção em outra thread
			}
		};
		
		t.start();
		
		
	}

	
	// FIM MOD 3.01

	// MOD 2.01
	// Laco para aceitar conexoes dos clientes
	// e delegar o tratamento de cada uma das conexoes 
	// a uma thread especifica.
	public void waitForRTSPClient(){		
		Socket client = null;		
		boolean done = false;
		while(!done){

			try {
				client = listenSocket.accept();
				RTSPThread t = new RTSPThread(client);
				threads.add(t); 
				t.addObserver(this);
				new Thread( t ).start();
				
			} catch (IOException e) {				
				e.printStackTrace();
			}	
			System.out.println("Were done here");
			done = true; // Espera apenas uma conexao. Sim, é assim que fazemos. =P
		}
		
		System.out.println("Thread do servidor de video encerrada");
	}
	// FIM MOD 2.01
	

	// MOD 3.02
	// Classe Server implementa Observer, assim pode receber notificaï¿½ï¿½es de mundanï¿½as de estado de
	// outros clientes
	// TO DO - Pode ser últil para algo?
	public void update(Observable target, Object newStatus) {
		
		// Altera o status da thread na listagem de threads
		
	}
	
	// FIM MOD 3.02
	
	public void endConnection() throws IOException{
		for(RTSPThread t: threads){
			t.endConnection();
		}
		listenSocket.close();
		
	}
 
	

	public void setPort(int port){
		this.localRTSPport = port;
	}


	public void setLocalRTSPPort(int localRTSPPort) {
		this.localRTSPPort = localRTSPPort;
	}


	public int getLocalRTSPPort() {
		return localRTSPPort;
	}


	public String getLocalIP() {
		return localIP;
	}


}
