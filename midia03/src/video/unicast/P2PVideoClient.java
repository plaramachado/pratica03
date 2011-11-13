package video.unicast;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


/* ------------------
   Client
   usage: java Client [Server hostname] [Server RTSP listening port] [Video file requested]
   ---------------------- */

import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;

public class P2PVideoClient{
	
	
	
	//GUI
	//----
	JFrame mainFrame = new JFrame("Client");
	JButton setupButton = new JButton("Setup");
	JButton playButton = new JButton("Play");
	JButton pauseButton = new JButton("Pause");
	JButton tearButton = new JButton("Teardown");
	JPanel mainPanel = new JPanel();
	JPanel buttonPanel = new JPanel();
	JLabel iconLabel = new JLabel();
	ImageIcon icon;

	// MOD 3.04
	// Modificador static de algumas variáveis foi retirado.
	//RTP variables:
	//----------------
	DatagramPacket rcvSocket; //UDP packet received from the server
	DatagramSocket RTPSocket; //socket to be used to send and receive UDP packets
	private int rtpRcvPort; //port where the client will receive the RTP packets

	Timer timer; //timer used to receive data from the UDP socket
	byte[] buf; //buffer used to store data received from the server

	//RTSP variables
	//----------------
	private static RTSPState state;
	Socket RTSPsocket; //socket used to send/receive RTSP messages
	//input and output stream filters
	private BufferedReader RTSPBufferedReader;
	private BufferedWriter RTSPBufferedWriter;
	private String videoFileName = "movie.mjpeg";
	int RTSPSequenceNumber = 0; //Sequence number of RTSP messages within the session
	int RTSPId = 0; //ID of the RTSP session (given by the RTSP Server)
	private int remotePort;
	private String remoteIP; 
	final static String CRLF = "\r\n";
	
	// FIM MOD 3.04

	//Video constants:
	//------------------
	static int MJPEG_TYPE = 26; //RTP payload type for MJPEG video

	//--------------------------
	//Constructor
	//--------------------------
	public P2PVideoClient(String remoteIP, int remotePort) {
		this.setRemotePort(remotePort);
		this.setRemoteIP(remoteIP);
		//this.remoteIP = new InetAddress(remoteIP);
		

		//build GUI
		//--------------------------

		//Frame
		mainFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		//Buttons
		buttonPanel.setLayout(new GridLayout(1,0));
		buttonPanel.add(setupButton);
		buttonPanel.add(playButton);
		buttonPanel.add(pauseButton);
		buttonPanel.add(tearButton);
		setupButton.addActionListener(new SetupButtonListener());
		playButton.addActionListener(new PlayButtonListener());
		pauseButton.addActionListener(new PauseButtonListener());
		tearButton.addActionListener(new TearButtonListener());

		//Image display label
		iconLabel.setIcon(null);

		//frame layout
		mainPanel.setLayout(null);
		mainPanel.add(iconLabel);
		mainPanel.add(buttonPanel);
		iconLabel.setBounds(0, 0, 380, 280);
		buttonPanel.setBounds(0, 280, 380, 50);

		mainFrame.getContentPane().add(mainPanel, BorderLayout.CENTER);
		mainFrame.setSize(new Dimension(390, 370));
		mainFrame.setVisible(true);

		//init timer
		//--------------------------
		timer = new Timer(20, new TimerListener());
		timer.setInitialDelay(0);
		timer.setCoalesce(true);

		//allocate enough memory for the buffer used to receive data from the server
		buf = new byte[15000];
		
		// inicia os buffers
		try {
			RTSPsocket = new Socket(getRemoteIP(), getRemotePort());
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//Set input and output stream filters:
		try {
			RTSPBufferedReader = new BufferedReader(new InputStreamReader(RTSPsocket.getInputStream()) );
			RTSPBufferedWriter = new BufferedWriter(new OutputStreamWriter(RTSPsocket.getOutputStream()) );
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		

		//init RTSP state:
		state = RTSPState.INIT;
	}

	//------------------------------------
	//main
	//------------------------------------
	public static void main(String argv[]) throws Exception {
		new P2PVideoClient("",0).handle(argv); // Não funciona mais
	}
	
	

	/**
	 * 
	 * Main method.
	 * @throws IOException 
	 * */
	public void handle(String [] argv) throws IOException{
		
		// MOD 3.05 
		// Permite a entrada do endereco do servidor, da porta e do nome
		// do recurso a ser carregado caso isto não tenha sido feito
		// via linha de comando.
//		int RTSPServerPort = 0;
//		String serverHost = "";
//		if(argv.length > 1){
//			RTSPServerPort = Integer.parseInt(argv[1]);
//			serverHost = argv[0];
//		}else{
//			serverHost = JOptionPane.showInputDialog(null, null, "Enter server address", JOptionPane.QUESTION_MESSAGE);
//			if (serverHost.equals(""))
//				serverHost = "localhost";
//			String p = JOptionPane.showInputDialog(null, null, "Enter server listening port", JOptionPane.QUESTION_MESSAGE);
//			if( p.equals(""))
//				p = "12345";
//			RTSPServerPort = Integer.parseInt(p);
//		}


		//InetAddress serverIPAddr = InetAddress.getByName(serverHost);

		//if(argv.length > 2 ){
//			videoFileName = argv[2];
//		}else{
//			videoFileName = JOptionPane.showInputDialog(null, null, "Enter file name", JOptionPane.QUESTION_MESSAGE);
//			if(videoFileName.equals(""))
//				videoFileName = "movie.mjpeg";
//		}

		// FIM MOD 3.5


		//Establish a TCP connection with the server to exchange RTSP messages
		//------------------

	}  


	class SetupButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent e){

			System.out.println("Setup Button pressed !");

			if (state == RTSPState.INIT){
				//Init non-blocking RTPsocket that will be used to receive data
				try{
					
					// MOD 2.03
					RTPSocket = new DatagramSocket(0);
					rtpRcvPort = RTPSocket.getLocalPort();
					System.out.println("Starting rvc socket at port " + rtpRcvPort);
					RTPSocket.setSoTimeout(5);
					// FIM MOD 2.03


				}
				catch (SocketException se){
					
					System.out.println("Socket exception: " + se);
					se.printStackTrace();
					//System.exit(0);
				}

				//init RTSP sequence number
				RTSPSequenceNumber = 1;

				//Send SETUP message to the server
				sendRTSPRequest("SETUP");

				//Wait for the response
				if (parseServerResponse() != 200)
					System.out.println("Invalid Server Response");
				else
				{
					state = RTSPState.READY;
					//change RTSP state and print new state
					//state = ....
					//System.out.println("New RTSP state: ....");
				}
			}//else if state != INIT then do nothing
		}
	}

	//Handler for Play button
	//-----------------------
	class PlayButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e){

			//System.out.println("Play Button pressed !");

			if (state == RTSPState.READY){
				//increase RTSP sequence number
				//.....


				//Send PLAY message to the server
				sendRTSPRequest("PLAY");

				//Wait for the response
				if (parseServerResponse() != 200){
					System.out.println("Invalid Server Response");
				}else{
					//change RTSP state and print out new state
					//.....
					// System.out.println("New RTSP state: ...")

					//start the timer
					timer.start();
					state = RTSPState.PLAYING;
				}
			}//else if state != READY then do nothing
		}
	}


	//Handler for Pause button
	//-----------------------
	class PauseButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e){

			//System.out.println("Pause Button pressed !");    
			if (state == RTSPState.PLAYING){
				//increase RTSP sequence number
				//........

				//Send PAUSE message to the server
				sendRTSPRequest("PAUSE");

				//Wait for the response
				if (parseServerResponse() != 200)
					System.out.println("Invalid Server Response");
				else{
					System.out.println("Received 200 response from server");
					//change RTSP state and print out new state
					//........
					state = RTSPState.READY;
					System.out.println("New RTSP state: ..." + state);

					//stop the timer
					timer.stop();
				}
			}
		
		}
	}

	//Handler for Teardown button
	//-----------------------
	class TearButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e){
			
			//Send TEARDOWN message to the server
			sendRTSPRequest("TEARDOWN");

			//Wait for the response
			if (parseServerResponse() != 200){
				System.out.println("Invalid Server Response");
			}else{
				//change RTSP state and print out new state							
				state = RTSPState.DONE;
				//stop the timer
				timer.stop();

				//exit
				System.exit(0);
			}
		}
	}


	//------------------------------------
	//Handler for timer
	//------------------------------------

	class TimerListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			//Construct a DatagramPacket to receive data from the UDP socket
			rcvSocket = new DatagramPacket(buf, buf.length);

			try{
				//receive the DP from the socket:
				RTPSocket.receive(rcvSocket);

				//create an RTPpacket object from the DP
				RTPPacket rtpPacket = new RTPPacket(rcvSocket.getData(), rcvSocket.getLength());

				//print important header fields of the RTP packet received:
				//System.out.println("Got RTP packet with SeqNum # " + rtpPacket.getSequenceNumber() + " TimeStamp " + rtpPacket.getTimestamp()+" ms, of type "+rtpPacket.getPayloadType());

				//print header bitstream:
				//rtpPacket.printHeader();

				//get the payload bitstream from the RTPpacket object
				int payloadLength = rtpPacket.getPayloadLength();				
				byte [] payload = new byte[payloadLength];
				rtpPacket.getPayload(payload);

				//get an Image object from the payload bitstream
				Toolkit toolkit = Toolkit.getDefaultToolkit();
				Image image = toolkit.createImage(payload, 0, payloadLength);

				//display the image as an ImageIcon object
				icon = new ImageIcon(image);
				iconLabel.setIcon(icon);
				iconLabel.getParent().repaint();
			}
			catch (InterruptedIOException iioe){
				//System.out.println("Nothing to read");
				iioe.printStackTrace();
			}
			catch (IOException ioe) {
				System.out.println("Exception caught at Client.actionPerformed: "+ioe);
			}
		}
	}

	//------------------------------------
	//Parse Server Response
	//------------------------------------
	private int parseServerResponse(){
		int replyCode = 0;

		try{
			//parse status line and extract the reply_code:
			String statusLine = RTSPBufferedReader.readLine();
			System.out.println("RTSP Client - Received from Server:");
			System.out.println(statusLine);

			StringTokenizer tokens = new StringTokenizer(statusLine);
			tokens.nextToken(); //skip over the RTSP version
			replyCode = Integer.parseInt(tokens.nextToken());

			//if reply code is OK get and print the 2 other lines
			if (replyCode == 200)
			{
				String seqNumLine = RTSPBufferedReader.readLine();
				System.out.println(seqNumLine);

				String sessionLine = RTSPBufferedReader.readLine();
				System.out.println(sessionLine);

				//if state == INIT gets the Session Id from the SessionLine
				tokens = new StringTokenizer(sessionLine);
				tokens.nextToken(); //skip over the Session:
				RTSPId = Integer.parseInt(tokens.nextToken());
			}
		}
		catch(Exception ex)
		{
			System.out.println("Exception when parsing server response: " + ex);
			ex.printStackTrace();
			System.exit(0);
		}

		return(replyCode);
	}

	//------------------------------------
	//Send RTSP Request
	//------------------------------------

	//.............
	//TO COMPLETE
	//.............

	// Change 1394
	// Implementing method
	private void sendRTSPRequest(String requestType)
	{	  
		requestType = requestType.toUpperCase();		
		try{
			System.out.println("Sending RTSP request of type " + requestType);

			RTSPBufferedWriter.write(requestType + " " + videoFileName + CRLF);
			RTSPBufferedWriter.write("CSeq: " + RTSPSequenceNumber + CRLF);
			
			if(requestType.equalsIgnoreCase("SETUP")){
				// MOD 2.04 
				// Especifica o transporte e a porta caso seja a requisicao SETUP
				RTSPBufferedWriter.write("Transport: RTP/AVP;unicast;client_port=" + rtpRcvPort + CRLF);
				// Transport: RTP/AVP;unicast;client_port=4588-4589
				// FIM MOD 2.04
				    		    		    	

			}else{
				RTSPBufferedWriter.write("Session: " + RTSPId + CRLF);
			}

			RTSPBufferedWriter.flush();
		}
		catch(Exception ex){
			System.out.println("Exception caught when sending RTSP request: " + ex);
			ex.printStackTrace();			
		}
		
		// MOD 2.05
		// Cliente incrementa o numero de sequencia a cada requisicao enviada.
		RTSPSequenceNumber++;
		// MOD 2.05
	}

	public void setRemotePort(int remotePort) {
		this.remotePort = remotePort;
	}

	public int getRemotePort() {
		return remotePort;
	}

	public void setRemoteIP(String remoteIP) {
		this.remoteIP = remoteIP;
	}

	public String getRemoteIP() {
		return remoteIP;
	}


//	public void setServerAddress(InetAddress address){
//		this.remoteIP = address;
//	}
//
//	public void setServerPort(int port){
//		this.remotePort = port;
//	}

}//end of Class Client

