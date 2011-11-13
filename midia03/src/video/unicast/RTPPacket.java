package video.unicast;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


//class RTPpacket

public class RTPPacket{
	
	// Masks to isolate specific fields in the header
	// Each one should be AND-combined to the appropriate integer that contains the field
	// We use one integer per 2 bytes in the header
	public static final int VERSION_MASK = (int)( Math.pow(2, 15) + Math.pow(2, 14) );
	public static final int PADDING_MASK = (int) ( Math.pow(2, 13) ) ;
	public static final int EXTENSIONS_MASK = (int) (Math.pow(2, 12));
	public static final int CC_MASK = (int)( Math.pow(2, 11) + Math.pow(2, 10) + Math.pow(2, 9) + Math.pow(2, 8) );
	public static final int MARKER_MASK = (int) Math.pow(2, 7);
	public static final int PAYLOAD_TYPE_MASK = 27;
	public static final int SEQUENCE_NUMBER_MASK = (int)( Math.pow(2, 16) - 1);
	public static final int HIGH_16BIT_MASK = (int)( (Math.pow(2, 32) - 1) - (Math.pow(2, 16) - 1) );
	public static final int LOW_16BIT_MASK = (int)( Math.pow(2, 16) - 1);
	
	//size of the RTP header:
	static int HEADER_SIZE = 12;

	//Fields that compose the RTP header
	private int version; // 2 bits
	private int padding;
	private int extension;
	private int CC;
	private int marker;
	private int payloadType;
	private int sequenceNumber;
	private  int timestamp;
	private int SSRC;
  
	//Bitstream of the RTP header
	public byte[] header;

	//size of the RTP payload
	public int payloadSize;
	//Bitstream of the RTP payload
	public byte[] payload;

  
  //--------------------------
  //Constructor of an RTPpacket object from header fields and payload bitstream
  //--------------------------
    
  public RTPPacket(int payloadType, int frameNumber, int timestamp, byte[] data, int dataLength){
    //fill by default header fields:
    this.version = 2;
    this.padding = 0;
    this.extension = 0;
    this.CC = 0;
    this.marker = 0;
    this.SSRC = 0;

    //fill changing header fields:
    this.sequenceNumber = frameNumber;
    this.timestamp = timestamp;
    this.payloadType = payloadType;

    //build the header bistream:
    //--------------------------
    header = new byte[HEADER_SIZE];
    
    // change 02    
    // filling header
    
    header[0] = (byte) ( 
    		this.version & VERSION_MASK + 
    		this.padding & PADDING_MASK +
    		this.extension & EXTENSIONS_MASK +
    		this.CC & CC_MASK     		
    		);
    
    header[1] = (byte) (
    		this.marker & MARKER_MASK +
    		this.payloadType & PAYLOAD_TYPE_MASK
    		);
    header[2] = (byte) ((this.sequenceNumber & SEQUENCE_NUMBER_MASK) >> 8);
    header[3] = (byte) ((this.sequenceNumber & SEQUENCE_NUMBER_MASK));
    
    header[4] = (byte) (this.timestamp >> 24); 
    header[5] = (byte) (this.timestamp >> 16);
    header[6] = (byte) (this.timestamp >> 8);
    header[7] = (byte) (this.timestamp);
    
    header[8] = (byte) (this.SSRC >> 24);
    header[9] = (byte) (this.SSRC >> 16);
    header[10] = (byte) (this.SSRC >> 8);
    header[11] = (byte) (this.SSRC);
        
    

    //fill the payload bitstream:
    //--------------------------
    payloadSize = dataLength;
    payload = new byte[dataLength];
    for(int i=0; i< dataLength; i++){
    	payload[i] = data[i];
    }

  }

  //--------------------------
  //Constructor of an RTPpacket object from the packet bistream
  //--------------------------
  public RTPPacket(byte[] packet, int packetSize)
  {
    //fill default fields:
    version = 2;
    padding = 0;
    extension = 0;
    CC = 0;
    marker = 0;
    SSRC = 0;

    //check if total packet size is lower than the header size
    if (packetSize >= HEADER_SIZE){

		//get the header bitsream:
		header = new byte[HEADER_SIZE];
		for (int i=0; i < HEADER_SIZE; i++)
		  header[i] = packet[i];
	
		//get the payload bitstream:
		payloadSize = packetSize - HEADER_SIZE;
		payload = new byte[payloadSize];
		for (int i=HEADER_SIZE; i < packetSize; i++)
		  payload[i-HEADER_SIZE] = packet[i];
	
		//interpret the changing fields of the header:
		payloadType = header[1] & 127;
		sequenceNumber = unsigned_int(header[3]) + 256*unsigned_int(header[2]);
		timestamp = unsigned_int(header[7]) + 256*unsigned_int(header[6]) + 65536*unsigned_int(header[5]) + 16777216*unsigned_int(header[4]);
    }else {
    	// Change 03
    	// Logging of invalid packets
    	System.out.println("Invalid or corrupted packet received");
    }
 }

  //--------------------------
  //getpayload: return the payload bistream of the RTPpacket and its size
  //--------------------------
  public int getPayload(byte[] data) {

    for (int i=0; i < payloadSize; i++)
      data[i] = payload[i];

    return(payloadSize);
  }

  //--------------------------
  //getpayload_length: return the length of the payload
  //--------------------------
  public int getPayloadLength() {
    return(payloadSize);
  }

  //--------------------------
  //getlength: return the total length of the RTP packet
  //--------------------------
  public int getLength() {
    return(payloadSize + HEADER_SIZE);
  }

  //--------------------------
  //getpacket: returns the packet bitstream and its length
  //--------------------------
  public int getPacket(byte[] packet)
  {
    //construct the packet = header + payload
    for (int i=0; i < HEADER_SIZE; i++)
	packet[i] = header[i];
    for (int i=0; i < payloadSize; i++)
	packet[i+HEADER_SIZE] = payload[i];

    //return total size of the packet
    return(payloadSize + HEADER_SIZE);
  }

  //--------------------------
  //gettimestamp
  //--------------------------

  public int getTimestamp() {
    return(timestamp);
  }

  //--------------------------
  //getsequencenumber
  //--------------------------
  public int getSequenceNumber() {
    return(sequenceNumber);
  }

  //--------------------------
  //getpayloadtype
  //--------------------------
  public int getPayloadType() {
    return(payloadType);
  }


  //--------------------------
  //print headers without the SSRC
  //--------------------------
	public void printHeader()
  {
    for (int i=0; i < (HEADER_SIZE-4); i++)
      {
	for (int j = 7; j>=0 ; j--)
	  if (((1<<j) & header[i] ) != 0)
	    System.out.print("1");
	else
	  System.out.print("0");
	System.out.print(" ");
      }

    System.out.println();
  }


  //return the unsigned value of 8-bit integer nb
  static int unsigned_int(int nb) {
    if (nb >= 0)
      return(nb);
    else
      return(256+nb);
  }
}


  //--------------------------
  //print headers without the SSRC
  //--------------------------
//  public void printheader()
//  {
//    //TO DO: uncomment
//    /*
//    for (int i=0; i < (HEADER_SIZE-4); i++)
//      {
//	for (int j = 7; j>=0 ; j--)
//	  if (((1<= 0)
//      return(nb);
//    else
//      return(256+nb);
//  }
//}
