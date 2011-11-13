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

	//private InetAddress clientIPAddress; //Client IP address
	//private int RTPDestinationPort = 0; //destination port for RTP packets  (given by the RTSP Client)
	//private Socket client;
	private int localRTSPport;
	
	
	
	
	private String localIP;
	
	private ServerSocket listenSocket = null;

	// Mapeamento de threads para numeros de linha da tabela
	
	
	private int localRTSPPort;


	
	

	/**
	 * Inicia o socket de escuta das requisições RSTP.
	 * */
	public P2PVideoServer(){
		this.localRTSPport = localRTSPPort;
		try {
			listenSocket = new ServerSocket(0);
		} catch (IOException e) {
			System.out.println("Error: could not start listening on port " + localRTSPport + ": " + e.getMessage());			
		}

		try {
			this.localIP = InetAddress.getLocalHost().getHostAddress();			
			setLocalRTSPPort(listenSocket.getLocalPort());
			
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
			
		}
		
	}

	
	// FIM MOD 3.01


	//------------------------------------
	//main
	//------------------------------------
	public static void main(String argv[]) throws Exception{

		// MOD 3.02
		// Pede a porta caso o usuario nao tenha digitado na linha de comando
		int port = 0;
		if(argv.length == 0){
			String cmdPort = 
				JOptionPane.showInputDialog(null, null, "Enter listening port for the server", JOptionPane.QUESTION_MESSAGE);

			if(cmdPort == null || cmdPort.equals(""))
				cmdPort = "12345";
			port = Integer.parseInt(cmdPort);
		}else{
			port = Integer.parseInt(argv[0]);
		}
		// FIM MOD 3.02


		//create a Server object
		P2PVideoServer theServer = new P2PVideoServer();
		System.out.println("LISTEN PORT: " + theServer.getLocalRTSPPort());
		theServer.setPort(port);

		
		theServer.waitForRTSPClient();

	}

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
				t.addObserver(this);
				new Thread( t ).start();
				
			} catch (IOException e) {				
				e.printStackTrace();
			}			
			done = true; // Espera apenas uma conexao. Sim, é assim que fazemos. =P
		}
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
