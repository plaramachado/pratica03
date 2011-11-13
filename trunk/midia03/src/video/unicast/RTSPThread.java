package video.unicast;


import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.StringTokenizer;
import javax.swing.Timer;

// MOD 2.02
// Criacao da classe RTSPThread, que trata as requisicoes RTSP de um cliente.
// Esta classe herda de Obervable, assim suas instancias podem notificar objetos registrados
// sobre mudancas no estado da conexao RTSP,
public class RTSPThread extends Observable implements Runnable{

	// Flags
	private boolean quit = false;	

	//RTSP variables
	//----------------
	private Map<Integer, String> statusCodes;
	final static int FRAME_PERIOD = 100;
	private RTSPState state;
	
	Socket RTSPsocket; //socket used to send/receive RTSP messages
	String resource;
	int RTPDestinationPort;
	
	//input and output stream filters
	private BufferedReader RTSPBufferedReader;
	private BufferedWriter RTSPBufferedWriter;	
	static int RTSP_ID = 123456; //ID of the RTSP session
	int RTSPSeqNb = 0; //Sequence number of RTSP messages within the session
	//Socket RTSPsocket;

	Timer streamingTimer;
	RTPListener streamingListener;

	final static String CRLF = "\r\n";



	public RTSPThread(Socket client) throws IOException{

		quit = false;
		RTSPsocket = client;

		//Initiate RTSPstate
		state = RTSPState.INIT;

		//Set input and output stream filters:
		RTSPBufferedReader = new BufferedReader(new InputStreamReader(RTSPsocket.getInputStream()) );
		RTSPBufferedWriter = new BufferedWriter(new OutputStreamWriter(RTSPsocket.getOutputStream()) );

		// Initialize status codes
		statusCodes = new HashMap<Integer, String>();
		statusCodes.put(200, "RTSP/1.0 200 OK");
		statusCodes.put(400, "RTSP/1.0 400 Bad Request");
		statusCodes.put(401, "RTSP/1.0 401 Unauthorized");
		statusCodes.put(404, "RTSP/1.0 404 Not Found");
		statusCodes.put(454, "RTSP/1.0 454 Session Not Found");
		statusCodes.put(500, "RTSP/1.0 500 Internal Server Error");


	}
	
	// MOD 1.01
	// Alteracao do codigo original do servidor para
	// tratar melhor a transicao entre os estados 
	public void handleConnection(){
		//Wait for the SETUP message from the client		
		RTSPMessageType requestType;		
		while(state != RTSPState.READY && !quit){

			requestType = this.parseRTSPRequest(); //blocking

			if (requestType == RTSPMessageType.SETUP){

				//update RTSP state
				state = RTSPState.READY;
				System.out.println("New RTSP state: " + state);

				streamingListener = new RTPListener(RTSPsocket.getInetAddress(), RTPDestinationPort, resource);
				try {
					streamingListener.allocateResources();
					streamingTimer = new Timer(FRAME_PERIOD, streamingListener);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("Error: unable to start streaming");
					this.sendRTSPResponse(500);
				}

				//Send response
				this.sendRTSPResponse();

			}else{
				System.out.println("Error: got message type " + requestType + " when expected SETUP");
			}
			
			//this.setChanged();			
			//this.notifyObservers(state);
		}



		//loop to handle RTSP requests, after client has entered INIT state
		while(!quit){
			
			//parse the request
			requestType = parseRTSPRequest(); //blocking
			if(requestType == null){
				System.out.println("Got unexpected message from " + RTSPsocket.getInetAddress().toString() + ":" + RTSPsocket.getLocalPort() + " :" + requestType);
				sendRTSPResponse(500); // Got an error !
				continue;
			}

			switch(requestType){
			case PLAY:
				if( state == RTSPState.READY ){					
					streamingTimer.start();
					state = RTSPState.PLAYING;
					sendRTSPResponse();
				}else{
					sendRTSPResponse(400);
				}

				break;
			case PAUSE:
				if(state == RTSPState.READY || state == RTSPState.PLAYING){
					sendRTSPResponse();
					state = RTSPState.READY;
					streamingTimer.stop();
				}else{
					sendRTSPResponse(400);
				}

				break;
			case SETUP: 
				state = RTSPState.READY;
				sendRTSPResponse();
				break;
			case TEARDOWN:
				state = RTSPState.DONE;
				sendRTSPResponse();
				streamingTimer.stop();				
				quit = true;
				break;
			default:

				System.out.println("Got unexpected message from " + RTSPsocket.getInetAddress().toString() + ":" + RTSPsocket.getLocalPort() + " :" + requestType);
				sendRTSPResponse(500); // Got an error !
				break;
			}
			
			// Notify UI to update
			this.setChanged();			
			this.notifyObservers(state);
		}
		
		// Ao sair do loop, encerra a conexï¿½o 
		try {
			RTSPsocket.close();
		} catch (IOException e) {
			System.out.println("Error closing connection: " + e);
		}
		
	}

	// FIM MOD 1.01
	

	public void run(){
		handleConnection();
		
	}



	//------------------------------------
	//Parse RTSP Request
	//------------------------------------
	private RTSPMessageType parseRTSPRequest(){

		System.out.println("Server parsing RSTP request");
		RTSPMessageType requestType = null;
		try{
			//parse request line and extract the request_type:
			String requestLine = RTSPBufferedReader.readLine();
			System.out.println("RTSP Server - Received from Client:");
			System.out.println(requestLine);

			StringTokenizer tokens = new StringTokenizer(requestLine);
			String requestTypeString = tokens.nextToken();

			//convert to request_type structure:
			if ((new String(requestTypeString)).compareTo("SETUP") == 0)
				requestType = RTSPMessageType.SETUP;
			else if ((new String(requestTypeString)).compareTo("PLAY") == 0)
				requestType = RTSPMessageType.PLAY;
			else if ((new String(requestTypeString)).compareTo("PAUSE") == 0)
				requestType = RTSPMessageType.PAUSE;
			else if ((new String(requestTypeString)).compareTo("TEARDOWN") == 0)
				requestType = RTSPMessageType.TEARDOWN;

			if (requestType == RTSPMessageType.SETUP){
				//extract VideoFileName from RequestLine
				resource = tokens.nextToken();
			}

			//parse the SeqNumLine and extract CSeq field
			String seqNumLine = RTSPBufferedReader.readLine();
			System.out.println(seqNumLine);
			tokens = new StringTokenizer(seqNumLine);
			tokens.nextToken();
			RTSPSeqNb = Integer.parseInt(tokens.nextToken());

			//get LastLine
			String lastLine = RTSPBufferedReader.readLine();
			System.out.println(" Last line:" + lastLine);

			if(requestType == RTSPMessageType.SETUP){
				tokens = new StringTokenizer(lastLine, ";");
				for (int i=0; i<2; i++)
					tokens.nextToken(); //skip unused stuff
				tokens = new StringTokenizer(tokens.nextToken(), "=");
				tokens.nextToken();
				RTPDestinationPort = Integer.parseInt(tokens.nextToken());
			}

		}
		catch(Exception ex){
			System.out.println("Exception caught at Server.parseRTSPRequest: "+ex);
			System.out.print("Closing connection with the client");
			requestType = RTSPMessageType.TEARDOWN;
			ex.printStackTrace();
			//throw ex;
			//System.exit(0);
		}
		return(requestType);
	}

	//------------------------------------
	//Send RTSP Response
	//------------------------------------
	//
	//
	private void sendRTSPResponse(){
		sendRTSPResponse(200);
	}
	
	// MOD 1.02
	// Permite especificar o código de status a ser enviado para o cliente.
	private void sendRTSPResponse(int status ){
		try{
			if(statusCodes.containsKey(status)){
				RTSPBufferedWriter.write(statusCodes.get(status) + CRLF);	
			}else{
				RTSPBufferedWriter.write(statusCodes.get(500) + CRLF);
			}


			RTSPBufferedWriter.write("CSeq: " + RTSPSeqNb + CRLF);
			RTSPBufferedWriter.write("Session: " + RTSP_ID + CRLF);
			RTSPBufferedWriter.flush();
			//System.out.println("RTSP Server - Sent response to Client.");
		}catch(Exception ex){
			System.out.println("Exception caught when sending response: " + ex);
			ex.printStackTrace();
			//System.exit(0);
		}
	}
	
	// FIM MOD 1.02

}

// FIM MOD 2.2
