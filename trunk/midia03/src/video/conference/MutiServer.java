package video.conference;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.StringTokenizer;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.Timer;

public class MutiServer extends JFrame implements ActionListener {

	/**
	 * 
	 */
	//	private static final long serialVersionUID = 1L;
	//RTP variables:
	//----------------
	DatagramSocket RTPsocket; //socket to be used to send and receive UDP packets
	DatagramPacket senddp; //UDP packet containing the video frames

	InetAddress ClientIPAddr; //Client IP address
	int RTP_dest_port = 0; //destination port for RTP packets  (given by the RTSP Client)

	//GUI:
	//----------------
	JLabel label;

	//Video variables:
	//----------------
	/*### MOD 1.1*/
	static int imagenb = 0; //image nb of the image currently transmitted
	static VideoStream video; //VideoStream object used to access video frames CHANGE
	static int MJPEG_TYPE = 26; //RTP payload type for MJPEG video
	static int FRAME_PERIOD = 100; //Frame period of the video to stream, in ms
	static int VIDEO_LENGTH = 500; //length of the video in frames

	//	static Timer timer; //timer used to send the images at the video frame rate CHANGE
	static byte[] buf; //buffer used to store the images to send to the client CHANGE

	//RTSP variables
	//----------------
	//rtsp states
	final static int INIT = 0;
	final static int READY = 1;
	final static int PLAYING = 2;
	//rtsp message types
	final static int SETUP = 3;
	final static int PLAY = 4;
	final static int PAUSE = 5;
	final static int TEARDOWN = 6;

	int state; //RTSP Server state == INIT or READY or PLAY
	Socket RTSPsocket; //socket used to send/receive RTSP messages
	//input and output stream filters
	BufferedReader RTSPBufferedReader;
	BufferedWriter RTSPBufferedWriter;
	static String VideoFileName; //video file requested from the client
	int RTSP_ID = 123456; //ID of the RTSP session
	int RTSPSeqNb = 0; //Sequence number of RTSP messages within the session
	private static DatagramPacket currentPack;

	final static String CRLF = "\r\n";
	private static MultiActionListener multiActionListener; //change
	private static int image_length; //CHANGE
	private static int rtspPortUsed;

	public static void triggerMultiAction(){
		multiActionListener.actionPerformed(null);
	}

	public static void setCurrentPack(DatagramPacket currentPack) {
		MutiServer.currentPack = currentPack;
	}

	/*### FIM DA MOD 1.1*/

	//--------------------------------
	//Constructor
	//--------------------------------
	public MutiServer(){

		//init Frame
		super("Server");

		/*### MOD 3.1*/

		//allocate memory for the sending buffer
		buf = new byte[15000];

		//Handler to close the main window
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) { //CHANGE
				//stop the timer and exit 
				//				timer.stop();
				//				System.exit(0);
			}});
		/*### FIM DA MOD 3.1*/
		//GUI:
		label = new JLabel("Send frame #        ", JLabel.CENTER);
		getContentPane().add(label, BorderLayout.CENTER);
	}

	//------------------------------------
	//main
	//------------------------------------
	public static void main(String[] args) throws Exception{
		main(1100);
	}
	public static int main(int RTSPport) throws Exception
	{
		//	  File file = new File("movie.Mjpeg");
		//	  System.out.println(file.getAbsolutePath()+" exists "+file.exists());
		//    

		//get RTSP socket port from the command line

		/*### MOD 3.2*/
		//CHANGE
		multiActionListener = new MultiActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				super.actionPerformed(e);
			}
		};
		//		timer = new Timer(FRAME_PERIOD, multiActionListener);
		//		timer.setInitialDelay(0);
		//		timer.setCoalesce(true);
		/*### FIM DA MOD 3.2*/

		//Initiate TCP connection with the client for the RTSP session
		final ServerSocket listenSocket = new ServerSocket(RTSPport);
		rtspPortUsed = listenSocket.getLocalPort();
		new Thread("Server wait video"){
			public void run() {
				while(true){
					System.out.println("Waiting for Connection");
					Socket accept = null;
					try {
						accept = listenSocket.accept();
					} catch (IOException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
					final MutiServer theServer = new MutiServer();
					theServer.RTSPsocket = accept;
					System.out.println("Got Connection");
					new Thread("Single server thread"){
						public void run() {
							//create a Server object
							try {
								System.out.println("SERVER TO STRING " +theServer);
								singleMain(theServer);
							} catch (SocketException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								try {
									listenSocket.close();
								} catch (IOException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								try {
									listenSocket.close();
								} catch (IOException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								try {
									listenSocket.close();
								} catch (IOException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							}// finally{
							//					try {
							//						
							//					} catch (IOException e) {
							//						// TODO Auto-generated catch block
							//						e.printStackTrace();
							//					}
							//}
						}
					}.start();
				}

			};
		}.start();
		return rtspPortUsed;

	}
	/* ### MOD 3.3*/
	private static void controlVideo(){ //CHANGE
		if (imagenb < VIDEO_LENGTH)
		{
			//update current imagenb
			imagenb++;

			try {
				image_length = video.getnextframe(buf);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
		{
			//if we have reached the end of the video file, stop the timer
			//timer.stop();// CHANGE
			/*### MOD 1.2*/
			imagenb = 0;
			video.reset();
			/*### FIM DA MOD 1.2*/

		}
	}
	/* ### FIM DA MOD 3.3*/

	private static void singleMain(MutiServer theServer) throws IOException,
	Exception, SocketException {
		//	MutiServer theServer = new MutiServer();
		//    theServer.RTSPsocket = listenSocket.accept();

		//show GUI:
		theServer.pack();
		theServer.setVisible(true);
		//Get Client IP address
		theServer.ClientIPAddr = theServer.RTSPsocket.getInetAddress();

		//Initiate RTSPstate
		theServer.state = INIT;

		//Set input and output stream filters:
		theServer.RTSPBufferedReader = new BufferedReader(new InputStreamReader(theServer.RTSPsocket.getInputStream()) );
		theServer.RTSPBufferedWriter = new BufferedWriter(new OutputStreamWriter(theServer.RTSPsocket.getOutputStream()) );

		//Wait for the SETUP message from the client
		int request_type;
		boolean done = false;
		while(!done)
		{
			request_type = theServer.parse_RTSP_request(); //blocking

			if (request_type == SETUP)
			{
				done = true;

				//update RTSP state
				theServer.state = READY;
				System.out.println("New RTSP state: READY");

				//Send response
				theServer.send_RTSP_response();

				//init the VideoStream object:
				if(video == null)video = new VideoStream(VideoFileName);

				//init RTP socket
				theServer.RTPsocket = new DatagramSocket();
				/*### MOD 3.4*/
				multiActionListener.add(theServer); //CHANGE
				/*### FIM DA MOD 3.4*/
				/*### MOD 2.1*/
				//				timer.start(); //CHANGE
				/*### FIM DA MOD 2.1*/
			}
		}

		//loop to handle RTSP requests
		while(true)
		{
			//parse the request
			request_type = theServer.parse_RTSP_request(); //blocking
			if(request_type == -1) break; //CHANGE

			//			if ((request_type == PLAY) && (theServer.state == READY))
			//			{
			//				//send back response
			//				theServer.send_RTSP_response();
			//				//start timer
			//				theServer.timer.start();
			//				//update state
			//				theServer.state = PLAYING;
			//				System.out.println("New RTSP state: PLAYING");
			//			}
			//			else if ((request_type == PAUSE) && (theServer.state == PLAYING))
			//			{
			//				//send back response
			//				theServer.send_RTSP_response();
			//				//stop timer
			//				theServer.timer.stop();
			//				//update state
			//				theServer.state = READY;
			//				System.out.println("New RTSP state: READY");
			//			}
			else if (request_type == TEARDOWN)
			{
				/* ### MOD 4.1 */
				removeOneServer(theServer);
				/* ### FIM DA MOD 4.1 */

				//send back response
				theServer.send_RTSP_response();
				//stop timer CHANGE no longer stops timer for now...
				//	    theServer.timer.stop();
				//close sockets
				theServer.RTSPsocket.close();
				theServer.RTPsocket.close();

				break;
			}
		}

	}

	/* ### MOD 4.2 */
	private static void removeOneServer(MutiServer theServer) {//CHANGE
		multiActionListener.remove(theServer); //CHANGE
		if(multiActionListener.isEmpty()){
			stopVideo();
		}
	}

	private static void stopVideo() {//CHANGE
		//		timer.stop();
		video.reset();
	}
	/* ### FIM MOD 4.2 */

	//------------------------
	//Handler for timer
	//------------------------
	public void actionPerformed(ActionEvent e) {

		//change no longer mess with imagenb here
		//if the current image nb is less than the length of the video
		//no longer gets buffer image here
		//		if (imagenb < VIDEO_LENGTH)
		//		{

		try {
			if(currentPack != null){ 
				currentPack.setPort(RTP_dest_port);
				currentPack.setAddress(ClientIPAddr);
				RTPsocket.send(currentPack);
				System.out.println("sentPack - "+RTPsocket.getLocalPort() + "-"+RTPsocket.getPort());
			}

			//CHANGE 

			//				//Builds an RTPpacket object containing the frame
			//				RTPpacket rtp_packet = new RTPpacket(MJPEG_TYPE, imagenb, imagenb*FRAME_PERIOD, buf, image_length);
			//
			//				//get to total length of the full rtp packet to send
			//				int packet_length = rtp_packet.getlength();
			//
			//				//retrieve the packet bitstream and store it in an array of bytes
			//				byte[] packet_bits = new byte[packet_length];
			//				rtp_packet.getpacket(packet_bits);
			//
			//				//send the packet as a DatagramPacket over the UDP socket
			//				senddp = new DatagramPacket(packet_bits, packet_length, ClientIPAddr, RTP_dest_port);
			//				RTPsocket.send(senddp);
			//
			//				//System.out.println("Send frame #"+imagenb);
			//				//print the header bitstream
			//				rtp_packet.printheader();
			//
			//				//update GUI
			//				label.setText("Send frame #" + imagenb);
		}
		catch(Exception ex)
		{
			System.out.println("Exception caught: "+ex); ex.printStackTrace();
			//	    System.exit(0);
		}
		//		}
	}

	//------------------------------------
	//Parse RTSP Request
	//------------------------------------
	private int parse_RTSP_request()
	{
		int request_type = -1;
		try{
			//parse request line and extract the request_type:
			String RequestLine = RTSPBufferedReader.readLine();
			System.out.println("RTSP Server - Received from Client:");
			System.out.println(RequestLine);

			if(RequestLine == null){
				removeOneServer(this);
				return -1; //CHANGE}
			}


			StringTokenizer tokens = new StringTokenizer(RequestLine);
			String request_type_string = tokens.nextToken();
			System.out.println("request_string is: "+request_type_string);

			//convert to request_type structure:
			if ((new String(request_type_string)).compareTo("SETUP") == 0)
				request_type = SETUP;
			//			else if ((new String(request_type_string)).compareTo("PLAY") == 0)
			//				request_type = PLAY;
			//			else if ((new String(request_type_string)).compareTo("PAUSE") == 0)
			//				request_type = PAUSE;
			else if ((new String(request_type_string)).compareTo("TEARDOWN") == 0)
				request_type = TEARDOWN;

			if (request_type == SETUP)
			{
				//extract VideoFileName from RequestLine
				VideoFileName = tokens.nextToken();
				System.out.println("VideoFileName is: "+VideoFileName);
			}

			//parse the SeqNumLine and extract CSeq field
			String SeqNumLine = RTSPBufferedReader.readLine();
			System.out.println(SeqNumLine);
			tokens = new StringTokenizer(SeqNumLine);
			tokens.nextToken();
			RTSPSeqNb = Integer.parseInt(tokens.nextToken());
			System.out.println("RTSPSeqNb = "+ RTSPSeqNb);


			//get LastLine
			//      String LastLine = "bla";
			String LastLine = RTSPBufferedReader.readLine();
			System.out.println("Now showing Last Line: ");
			System.out.println(LastLine);

			if (request_type == SETUP)
			{
				//extract RTP_dest_port from LastLine
				tokens = new StringTokenizer(LastLine);
				for (int i=0; i<3; i++)
					tokens.nextToken(); //skip unused stuff
				RTP_dest_port = Integer.parseInt(tokens.nextToken());
			}
			//else LastLine will be the SessionId line ... do not check for now.
		}
		catch(Exception ex)
		{
			System.out.println("Exception caught: "+ex); ex.printStackTrace();
			removeOneServer(this); //CHANGE
			//	System.exit(0);
		}
		return(request_type);
	}

	//------------------------------------
	//Send RTSP Response
	//------------------------------------
	private void send_RTSP_response()
	{
		try{
			RTSPBufferedWriter.write("RTSP/1.0 200 OK"+CRLF);
			RTSPBufferedWriter.write("CSeq: "+RTSPSeqNb+CRLF);
			RTSPBufferedWriter.write("Session: "+RTSP_ID+CRLF);
			RTSPBufferedWriter.flush();
			//System.out.println("RTSP Server - Sent response to Client.");
		}
		catch(Exception ex)
		{
			System.out.println("Exception caught: "+ex); ex.printStackTrace();
			//	System.exit(0);
		}
	}
}
