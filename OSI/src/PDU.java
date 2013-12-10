import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
 *This class is responsible for: 
 *--Transport layer:
 *----breaking the message into segments 
 *----Constructing TCP Header
 *----providing a service to lower layer
 *----providing a service to Upper layer
 *----------------------------------------
 *--Network layer:
 *----create a packet from Segment
 *----Constructing IP Header
 *----providing a service to lower layer
 *----providing a service to Upper layer
 *----------------------------------------
 *--MAC Layer:
 *----create a frame from packet 
 *----Constructing Ethernet Header
 *----providing a service to lower layer
 *----providing a service to Upper layer
 */

public class PDU {
	// TCP Header
	byte data[];
	byte segNO[]=new byte[2] ; // segment number -- part of TCP Header
	
	// IP Header
	byte[] iden = new byte[2]; // used for fragmentation if needed
	byte[] off = new byte[2]; // used for offset fragmentation if needed
	
	byte[] scAdd= new byte[32]; // source address
	byte[] desAdd= new byte[32]; // destination address
	
	// Ethernet Frame
	byte[] preamble=new byte [7]; // indicate the start of frame
	byte[] scMAC =new byte[6]; // source MAC address
	byte[] desMAC =new byte[6]; // destination MAC address
	byte[] checksum=new byte[2]; // checksum for entire header ( error detection)
	byte[] crc = new byte[4]; // used for entire frame ( error detection)
	
	

	
	// setter and getter method for data member
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
	public byte[] getSegNO() {
		return segNO;
	}
	public void setSegNO(byte[] segNO) {
		this.segNO = segNO;
	}
	public byte[] getIden() {
		return iden;
	}
	public void setIden(byte[] iden) {
		this.iden = iden;
	}
	public byte[] getOff() {
		return off;
	}
	public void setOff(byte[] off) {
		this.off = off;
	}
	public byte[] getScAdd() {
		return scAdd;
	}
	public void setScAdd(byte[] scAdd) {
		this.scAdd = scAdd;
	}
	public byte[] getDesAdd() {
		return desAdd;
	}
	public void setDesAdd(byte[] desAdd) {
		this.desAdd = desAdd;
	}
	public byte[] getPreamble() {
		return preamble;
	}
	public void setPreamble(byte[] preamble) {
		this.preamble = preamble;
	}
	public byte[] getScMAC() {
		return scMAC;
	}
	public void setScMAC(byte[] scMAC) {
		this.scMAC = scMAC;
	}
	public byte[] getDesMAC() {
		return desMAC;
	}
	public void setDesMAC(byte[] desMAC) {
		this.desMAC = desMAC;
	}
	public byte[] getChecksum() {
		return checksum;
	}
	public void setChecksum(byte[] checksum) {
		this.checksum = checksum;
	}
	public byte[] getCrc() {
		return crc;
	}
	public void setCrc(byte[] crc) {
		this.crc = crc;
	}

	
	/* This function do :
	 * 1- break data to segments using MSS as size of segment
	 * 2- construct the TCP header 
	 * 3- send each segment to lower layer
	 * 
	 * In : data to be segmented (file name)
	 * Out : segment to be packeted ( to Network)
	 */
	public PDU[] tarnsportToLower(){
		
		String fileName= "file.txt";
		FileInputStream data = openFile(fileName);
		
		int mss= 1350; // Maximum Segment  
		List<byte[]> chunk = new ArrayList<byte[]>(); 

		// Define number of segments
		int start = 0;
		int end = 0;
		double size =0;
		try {
			size =(double)data.getChannel().size();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		end = (int)Math.ceil(size/mss);
		
		
	  //1-  break data to segments
		while (start < end ) {
	   	       
	        byte[]b=new byte[mss];
	        System.out.println("chunk"+start);
	        
	        try {
	        	data.read(b);
	        } catch (IOException e) {
	        	e.printStackTrace();
	        }

	        chunk.add(b);
	        start += 1;
	    }
	    
		
	    //2- construct the TCP header
		
		PDU segment[] = new PDU[end];
	    start =0;
	 
	    while (start < end) {
	    	
	    	segment[start]= new PDU();
	    	byte[] no = (""+start).getBytes();
	    	segment[start].setSegNO(no);
	    	segment[start].setData(chunk.get(start));
	        start += 1;
	    }
	    
	//3- send segment array
		return segment;
	}
	
	/* This function do :
	 * 1- Received each segment from Transport layer 
	 * 2- Encapsulate each segment with IP header
	 * 3- send each packet to lower layer
	 * 
	 * In : source address, destination address
	 * Out : packets to be framed (to MAC)
	 */
	public PDU[] networkToLower(byte[] sc, byte[] des ){
		
		PDU[] packet =tarnsportToLower();
		int len=packet.length; 
		
		for(int i=0;i<len;i++)
		{
			// set Identification to default value
			byte[] iden=("0").getBytes();
			packet[i].setIden(iden);
			
			// set fragmentation offset to default value 
			byte[] offest=("0").getBytes();
			packet[i].setOff(offest);
			
			// set source address from table in the thread
			packet[i].setScAdd(sc);
			
			// set destination address from table in the thread
			packet[i].setScAdd(des);
	
		}
		
		return packet;
	}
	
	public PDU[] macToLower(byte[] sc, byte[] des ){
		
		byte[] scAdd=("10.10.20.1").getBytes();
		byte[] desAdd=("10.10.20.2").getBytes();
		

		PDU[] frame =networkToLower(scAdd,desAdd);
		int len=frame.length; 
		byte[] pre =("10101010").getBytes();

		for(int i=0;i<len;i++)
		{
			// fixed pattern to indicate the start of frame
			frame[i].setPreamble(pre);
			
			// set the source MAC address
			frame[i].setScMAC(sc);
			
			// set the destination MAC address
			frame[i].setDesMAC(des);
		}

		return frame;
	}
	
	
	public  FileInputStream openFile(String fileName){
		
		FileInputStream data = null;
		
		//find path
		String filePath = new File(fileName).getAbsolutePath();
		filePath.concat(fileName);
		
		try{
	         
	         data = new FileInputStream(filePath);
		 
		}catch(Exception ex){
			 
	         ex.printStackTrace();
	      }
		return data;
	}		
   
}
	
	
	
