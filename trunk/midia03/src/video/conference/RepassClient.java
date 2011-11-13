package video.conference;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.StringTokenizer;

import javax.swing.Timer;

public class RepassClient{



	//RTP variables:
	//----------------
	DatagramPacket rcvdp; //UDP packet received from the server
	DatagramSocket RTPsocket; //socket to be used to send and receive UDP packets
	int RTP_RCV_PORT = 25000; //port where the client will receive the RTP packets

	Timer timer; //timer used to receive data from the UDP socket
	byte[] buf; //buffer used to store data received from the server

	//RTSP variables
	//----------------
	//rtsp states
	final static int INIT = 0;
	final static int READY = 1;
	final static int PLAYING = 2;
	int state; //RTSP state == INIT or READY or PLAYING
	Socket RTSPsocket; //socket used to send/receive RTSP messages
	//input and output stream filters
	BufferedReader RTSPBufferedReader; //FIXME 
	BufferedWriter RTSPBufferedWriter;
	static String VideoFileName; //video file to request to the server
	int RTSPSeqNb = 0; //Sequence number of RTSP messages within the session
	int RTSPid = 0; //ID of the RTSP session (given by the RTSP Server)

	final static String CRLF = "\r\n";

	//Video constants:
	//------------------
	static int MJPEG_TYPE = 26; //RTP payload type for MJPEG video

	//--------------------------
	//Constructor
	//--------------------------
	public RepassClient() {

		//build GUI
		//--------------------------

		//Frame

		//Buttons
//		buttonPanel.add(playButton);
//		buttonPanel.add(pauseButton);
//		playButton.addActionListener(new playButtonListener()); //CHANGE
//		pauseButton.addActionListener(new pauseButtonListener());

		//init timer
		//--------------------------
		timer = new Timer(20, new timerListener());
		timer.setInitialDelay(0);
		timer.setCoalesce(true);

		//allocate enough memory for the buffer used to receive data from the server
		buf = new byte[15000];
	}

	//------------------------------------
	//main
	//------------------------------------
	public static void main(String[] args) throws Exception{
		main(args[0], Integer.parseInt(args[1]));
		
	}
	public static int main(String ip, int videoPort) throws Exception
	{
			//Create a Client object
			RepassClient theClient = new RepassClient();
			theClient.RTPsocket = new DatagramSocket(0);//new Socket("Server", RTP_RCV_PORT);
			
			theClient.RTP_RCV_PORT = theClient.RTPsocket.getLocalPort(); 

//			theClient.RTP_RCV_PORT += i;

			//get server RTSP port and IP address from the command line
			//------------------
			int RTSP_server_port = videoPort;
			String ServerHost = ip;
			InetAddress ServerIPAddr = InetAddress.getByName(ServerHost);

			//get video filename to request:
			VideoFileName = "movie.Mjpeg";

			System.out.println("Calls up main...");

			//Establish a TCP connection with the server to exchange RTSP messages
			//------------------
			theClient.RTSPsocket = new Socket(ServerIPAddr, RTSP_server_port);

			//Set input and output stream filters:
			theClient.RTSPBufferedReader = new BufferedReader(new InputStreamReader(theClient.RTSPsocket.getInputStream()) );
			theClient.RTSPBufferedWriter = new BufferedWriter(new OutputStreamWriter(theClient.RTSPsocket.getOutputStream()) );
//			theClient.f.setTitle("CLIENT CODE "+i);

			//init RTSP state:
			theClient.state = INIT;
			theClient.new SetupButtonListener().actionPerformed(null);
			Thread.sleep(100);
			theClient.new playButtonListener().actionPerformed(null);
			
			//this line has been added for debug, it starts an infinite while if you`re using a fixed port
			if(videoPort != 0) while(true); //if you tried to use a fixed port, it locks the code, used for debugging
			
			return MutiServer.main(0);
//		}
	}

	//------------------------------------
	//Handler for buttons
	//------------------------------------

	//.............
	//TO COMPLETE
	//.............

	//Handler for Setup button
	//-----------------------
	public class SetupButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent e){

			System.out.println("Setup Button pressed !");
			new Thread(){
				public void run() {pressSetup();}
				
			}.start();
//			pressSetup();
		}

		private void pressSetup() {
			if (state == INIT)
			{
				//Init non-blocking RTPsocket that will be used to receive data
				try{
					//construct a new DatagramSocket to receive RTP packets from the server, on port RTP_RCV_PORT
					//RTPsocket = ...	
//					RTPsocket = new DatagramSocket(RTP_RCV_PORT);//new Socket("Server", RTP_RCV_PORT);

					//set TimeOut value of the socket to 5msec. Or 30...
					//....
					RTPsocket.setSoTimeout(5);
				}
				catch (SocketException se)
				{
					se.printStackTrace();
					//System.out.println("Socket exception: "+se);
					System.exit(0);
				}

				//init RTSP sequence number
				RTSPSeqNb = 1;

				//Send SETUP message to the server
				send_RTSP_request("SETUP");
				/* ### MOD 5.2 */
				timer.start(); //CHANGE
				/* ### FIM DA MOD 5.2 */

				//Wait for the response
				if (parse_server_response() != 200)
					System.out.println("Invalid Server Response");
				else
				{
					state = READY;//CHANGE
					//change RTSP state and print new state
					//state = ....
					System.out.println("New RTSP state: READY");
				}
			}//else if state != INIT then do nothing
		}
	}

	//Handler for Play button
	//-----------------------
	class playButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e){

			System.out.println("Play Button pressed !");
			new Thread(){
				public void run() {pressPlay();}
			}.start();
			
		}

		private void pressPlay() {
			if (state == READY)
			{
				RTSPSeqNb++;


				//Send PLAY message to the server
				send_RTSP_request("PLAY");

				//Wait for the response
				if (parse_server_response() != 200)
					System.out.println("Invalid Server Response");
				else
				{
					//change RTSP state and print out new state
					state = PLAYING;
					System.out.println("New RTSP state: PLAYING");

					//start the timer
					timer.start();
				}
			}//else if state != READY then do nothing
		}
	}


	//Handler for Pause button
	//-----------------------
	class pauseButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e){

			//System.out.println("Pause Button pressed !");

			if (state == PLAYING)
			{
				RTSPSeqNb++;

				//Send PAUSE message to the server
				send_RTSP_request("PAUSE");

				//Wait for the response
				if (parse_server_response() != 200)
					System.out.println("Invalid Server Response");
				else
				{
					//change RTSP state and print out new state
					state = READY;
					System.out.println("New RTSP state: READY");

					//stop the timer
					timer.stop();
				}
			}
			//else if state != PLAYING then do nothing
		}
	}

	//Handler for Teardown button
	//-----------------------
	class tearButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e){

			System.out.println("Teardown Button pressed !");

			//increase RTSP sequence number
			RTSPSeqNb++;


			//Send TEARDOWN message to the server
			send_RTSP_request("TEARDOWN");

			//Wait for the response
			if (parse_server_response() != 200)
				System.out.println("Invalid Server Response");
			else
			{
				//change RTSP state and print out new state
				state = INIT;
				System.out.println("New RTSP state: INIT");

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

	class timerListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			//Construct a DatagramPacket to receive data from the UDP socket
			rcvdp = new DatagramPacket(buf, buf.length);

			try{
				//receive the DP from the socket:
				RTPsocket.receive(rcvdp);
//				RTPpacket rtp_packet = new RTPpacket(rcvdp.getData(), rcvdp.getLength());

				//create an RTPpacket object from the DP
				RTPpacket rtp_packet = new RTPpacket(rcvdp.getData(), rcvdp.getLength());

				//print important header fields of the RTP packet received:
				System.out.println("Got RTP packet with SeqNum # "+rtp_packet.getsequencenumber()+" TimeStamp "+rtp_packet.gettimestamp()+" ms, of type "+rtp_packet.getpayloadtype());

				//print header bitstream:
				rtp_packet.printheader();

				//get the payload bitstream from the RTPpacket object
				int payload_length = rtp_packet.getpayload_length();
				byte [] payload = new byte[payload_length];
				rtp_packet.getpayload(payload);

				//get an Image object from the payload bitstream
				Toolkit toolkit = Toolkit.getDefaultToolkit();
				Image image = toolkit.createImage(payload, 0, payload_length);
				MutiServer.setCurrentPack(rcvdp);
				MutiServer.triggerMultiAction();
			}
			catch (InterruptedIOException iioe){
				//System.out.println("Nothing to read");
			}
			catch (IOException ioe) {
				System.out.println("Exception caught: "+ioe);
			}
		}
	}

	//------------------------------------
	//Parse Server Response
	//------------------------------------
	private int parse_server_response()
	{
		int reply_code = 0;

		try{
			//parse status line and extract the reply_code:
			String StatusLine = RTSPBufferedReader.readLine();
			//System.out.println("RTSP Client - Received from Server:");
			System.out.println(StatusLine);

			StringTokenizer tokens = new StringTokenizer(StatusLine);
			tokens.nextToken(); //skip over the RTSP version
			reply_code = Integer.parseInt(tokens.nextToken());

			//if reply code is OK get and print the 2 other lines
			if (reply_code == 200)
			{
				String SeqNumLine = RTSPBufferedReader.readLine();
				System.out.println(SeqNumLine);

				String SessionLine = RTSPBufferedReader.readLine();
				System.out.println(SessionLine);

				//if state == INIT gets the Session Id from the SessionLine
				tokens = new StringTokenizer(SessionLine);
				tokens.nextToken(); //skip over the Session:
				RTSPid = Integer.parseInt(tokens.nextToken());
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			//	System.out.println("Exception caught: "+ex);
			System.exit(0);
		}

		return(reply_code);
	}

	//------------------------------------
	//Send RTSP Request
	//------------------------------------

	//.............
	//TO COMPLETE
	//.............

	private void send_RTSP_request(String request_type)
	{
		try{
			//    	RTSPBufferedWriter.write(cbuf);
			//Use the RTSPBufferedWriter to write to the RTSP socket

			//write the request line:
			//RTSPBufferedWriter.write(...);
			boolean isSetup = request_type.equals("SETUP");
			StringBuffer string = new StringBuffer();
			string.append(request_type);

			//    	if(request_type.equals("SETUP")){
			string.append(" "+VideoFileName + " RSTP/1.0");
			//    	}

			//write the CSeq line:
			//......
			string.append("\n");
			string.append("Cseq: "+RTSPSeqNb);
			string.append("\n");

			//check if request_type is equal to "SETUP" and in this case write the Transport: line advertising to the server the port used to receive the RTP packets RTP_RCV_PORT
			//if ....
			if(isSetup){
				string.append("Transport: RTP/UDP; client_port= " + RTP_RCV_PORT);
			} else{
				//otherwise, write the Session line from the RTSPid field
				//else ....
				string.append("Session: "+ RTSPid);
			}
			System.out.println(string.toString());
			RTSPBufferedWriter.append(string.toString()+"\n");

			RTSPBufferedWriter.flush();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			//System.out.println("Exception caught: "+ex);
			System.exit(0);
		}
	}

}//end of Class Client

