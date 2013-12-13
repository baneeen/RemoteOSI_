import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

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
	private byte data[];
	private byte segNO[]=new byte[2] ; // segment number -- part of TCP Header
	
	// IP Header
	private byte[] iden = new byte[2]; // used for fragmentation if needed
	private byte[] off = new byte[2]; // used for offset fragmentation if needed
	
	private byte[] scAdd= new byte[32]; // source address
	private byte[] desAdd= new byte[32]; // destination address
	
	// Ethernet Frame
	private byte[] preamble=new byte [7]; // indicate the start of frame
	private byte[] scMAC =new byte[6]; // source MAC address
	private byte[] desMAC =new byte[6]; // destination MAC address
	private byte[] checksum=new byte[2]; // checksum for entire header ( error detection)
	private byte[] crc = new byte[4]; // used for entire frame ( error detection)
	
	

	
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

	
	/** This function do :
	 * 1- setting the 
	 * 2- break data to segments using MSS as size of segment
	 * 3- construct the TCP header 
	 * 4- send each segment to lower layer
	 * 
	 * @param : data to be segmented (file name)
	 * @return : segment to be packeted ( to Network)
	 */
	public PDU[] tarnsportToLower(String fileName){
		
		
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
		// close channel 
		try {
			data.close();
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
	
	/** This function do :
	 * 1- Received each segment from Transport layer 
	 * 2- Encapsulate each segment with IP header
	 * 3- send each packet to lower layer
	 * 
	 * @param source address, destination address, segments from transport
	 * @return  packets to be framed (to MAC)
	 */
	public PDU[] networkToLower(byte[] sc, byte[] des,PDU[] packet){
		
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
	
	/** This function do :
	 * 1- Received each packet from Network layer 
	 * 2- Encapsulate each packet with Ethernet header
	 * 3- send each frame to lower layer
	 * 
	 * @param source MAC, destination MAC, PDU[] packet
	 * @return  frame to be encoded (to Physical)
	 */
	public PDU[] macToLower(byte[] sc, byte[] des, PDU[] frame ){
	
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
			
			
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
			
			try {
				outputStream.write(frame[i].getPreamble());
				outputStream.write(frame[i].getScMAC());
				outputStream.write(frame[i].getDesMAC());
				outputStream.write(frame[i].getIden());
				outputStream.write(frame[i].getOff());
				outputStream.write(frame[i].getScAdd());
				outputStream.write(frame[i].getDesAdd());
				outputStream.write(frame[i].getSegNO());
				
			} catch (IOException e) {

				e.printStackTrace();
			}
			
			// set checksum
			byte[] header =outputStream.toByteArray( );
			long checksum=calculateChecksum(header);
			frame[i].setChecksum((checksum+"").getBytes());
			
			// set CRC-32
			try {
				outputStream.write(frame[i].getChecksum());
				outputStream.write(frame[i].getData());				
			} catch (IOException e) {
				e.printStackTrace();
			}
			byte[] frameData =outputStream.toByteArray( );
			long crc=calculateCRC(frameData);
			frame[i].setCrc((crc+"").getBytes());
			
		}

		return frame;
	}
	
	/** This function do :
	 * 1- Received each frame from MAC layer 
	 * 2- transform frame to bits array
	 * 3- send each frame to lower layer
	 * 
	 * @param PDU[] frame
	 * @return  bits  (to be sent)
	 */
	 public BitSet[] physicalToLower(PDU[] frame)
	 {
			
			int len = frame.length;
			BitSet bits[] =new BitSet[len];
			for(int i=0;i<len;i++)
			{
		
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
				
				try {
					outputStream.write(frame[i].getPreamble());
					outputStream.write(frame[i].getScMAC());
					outputStream.write(frame[i].getDesMAC());
					outputStream.write(frame[i].getIden());
					outputStream.write(frame[i].getOff());
					outputStream.write(frame[i].getScAdd());
					outputStream.write(frame[i].getDesAdd());
					outputStream.write(frame[i].getSegNO());
					outputStream.write(frame[i].getChecksum());
					outputStream.write(frame[i].getData());	
					outputStream.write(frame[i].getCrc());	
				} catch (IOException e) {
					e.printStackTrace();
				}
				 byte[] frameData =outputStream.toByteArray( );
				
			bits[i]=BitSet.valueOf(frameData);
			}
			
		return bits;
		 
	 }
	
	
	

	/** this method for calculate CRC ( for entire frame)
	 * the code source :
	 * http://www.java-examples.com/generate-crc32-checksum-byte-array-example	
	 * 
	 * @param buf ( header data)
	 * @return CRC
	 */
	public long calculateCRC(byte[] buf) {
	
		Checksum crc = new CRC32();
		
		// update buff for Checksum method
		crc.update(buf,0,buf.length);
		
		
		long lngCRC = crc.getValue();
		
		return lngCRC;
	}
	
	
	
	

	/** this method for create checksum ( for the header)
	 * the code source :
	 * http://pastebin.com/fKkAvvrS	
	 * 
	 * @param buf ( header data)
	 * @return checksum 
	 */
	public long calculateChecksum(byte[] buf) {
	    int length = buf.length;
	    int i = 0;
	 
	    long sum = 0;
	    long data;
	 
	    // Handle all pairs
	    while (length > 1) {
	      // Corrected to include @Andy's edits and various comments on Stack Overflow
	      data = (((buf[i] << 8) & 0xFF00) | ((buf[i + 1]) & 0xFF));
	      sum += data;
	      // 1's complement carry bit correction in 16-bits (detecting sign extension)
	      if ((sum & 0xFFFF0000) > 0) {
	        sum = sum & 0xFFFF;
	        sum += 1;
	      }
	 
	      i += 2;
	      length -= 2;
	    }
	 
	    // Handle remaining byte in odd length buffers
	    if (length > 0) {
	      // Corrected to include @Andy's edits and various comments on Stack Overflow
	      sum += (buf[i] << 8 & 0xFF00);
	      // 1's complement carry bit correction in 16-bits (detecting sign extension)
	      if ((sum & 0xFFFF0000) > 0) {
	        sum = sum & 0xFFFF;
	        sum += 1;
	      }
	    }
	 
	    // Final 1's complement value correction to 16-bits
	    sum = ~sum;
	    sum = sum & 0xFFFF;
	    return sum;
	 
	  }
	 

	       	
	
	
/** this method for open file to extract data ( in transport)
 * 	
 * @param fileName as string
 * @return FilInputStream
 */
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
	
	
	
