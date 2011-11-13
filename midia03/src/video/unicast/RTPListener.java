package video.unicast;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import javax.swing.Timer;


// MOD 1.03
// Classe RTPListener encapsula o código para tratamento do 
// protocolo RTP.
public class RTPListener implements ActionListener {

	//RTP variables:
	//----------------
	DatagramSocket RTPsocket; //socket to be used to send and receive UDP packets
	DatagramPacket sendPacket; //UDP packet containing the video frames
	int port;
	InetAddress clientIPAddress;
	
	//Video variables:
	//----------------
	String resource;
	int imageNumber = 0; //image nb of the image currently transmitted
	VideoStream video; //VideoStream object used to access video frames
	static int MJPEG_TYPE = 26; //RTP payload type for MJPEG video
	static int FRAME_PERIOD = 100; //Frame period of the video to stream, in ms
	static int VIDEO_LENGTH = 500; //length of the video in frames
	Timer timer; //timer used to send the images at the video frame rate
	byte[] buf; //buffer used to store the images to send to the client
	
	
	public RTPListener(InetAddress clientAddress, int destinationPort, String resource){
		//init Timer
		timer = new Timer(FRAME_PERIOD, this);
		timer.setInitialDelay(0);
		timer.setCoalesce(true);
		port = destinationPort;
		clientIPAddress = clientAddress;
		this.resource = resource;
		
		//allocate memory for the sending buffer
		buf = new byte[15000];
		//video = new VideoStream(resource);

	}
	
	public void allocateResources() throws Exception{
		video = new VideoStream(resource);
		RTPsocket = new DatagramSocket();
	}
	
	
	public void actionPerformed(ActionEvent arg0) {
		
		if (imageNumber < VIDEO_LENGTH){
			//update current imagenb
			imageNumber++;

			try {
				//get next frame to send from the video, as well as its size
				int imageLength = video.getNextFrame(buf);

				//Builds an RTPpacket object containing the frame
				RTPPacket rtpPacket = new RTPPacket(MJPEG_TYPE, imageNumber, imageNumber*FRAME_PERIOD, buf, imageLength);

				//get to total length of the full rtp packet to send
				int packetLength = rtpPacket.getLength();

				//retrieve the packet bitstream and store it in an array of bytes
				byte[] packetBits = new byte[packetLength];
				rtpPacket.getPacket(packetBits);

				//send the packet as a DatagramPacket over the UDP socket
				sendPacket = new DatagramPacket(packetBits, packetLength, clientIPAddress, port);
				RTPsocket.send(sendPacket);

				System.out.println("Send frame #"+ imageNumber+ "to port "  + port + " at " + clientIPAddress);
				//print the header bitstream
				//rtpPacket.printHeader();

				//update GUI
				//label.setText("Send frame #" + imageNumber);
			}
			catch(Exception ex){
				System.out.println("Exception caught at Server.actionPerformed: "+ex);
				ex.printStackTrace();
	//			System.exit(0);
			}
		}
		else{
			//if we have reached the end of the video file, stop the timer
			timer.stop();
		}
		
	}

}


// FIM MOD 2.03