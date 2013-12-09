/*
 *This class is responsible for: 
 *-- breaking the message into segments 
 *-- Constructing TCP Header
 *-- providing a service to lower layer
 *-- providing a service to Upper layer
 */
package osi;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Transport {
	byte data[];
	Network packet;
	int mss= 1350; // Maximum Segment  
	
	byte segNO[]=new byte[2] ; // segment number -- part of TCP Header
	
	
	//providing a service to upper layer
	Transport(byte[] data ){
		this.data=data;
	}
	
	//providing a service to lower layer
	Transport(Network packet){
		this.packet=packet;	
	}
	
	
	public void serveUpper(){
		
	}
	
	
	/* This function do :
	 * 1- break data to segments using MSS as size of segment
	 * 2- construct the TCP header 
	 * 3- send each segment to lower layer
	 * 
	 * In : data to be segmented
	 * Out : segment to packeted ( to Network)
	 */
	public List<ArrayList<byte[]>> serveLower(byte[] data ){

		
		//1-  break data to segments
		List<byte[]> chunk = new ArrayList<byte[]>(); 
		
		int start = 0;
	    while (start < data.length) {
	        int end = Math.min(data.length, start + mss);
	        chunk.add(Arrays.copyOfRange(data, start, end));
	        start += mss;
	    }
	    
	    //2- construct the TCP header
	    List<ArrayList<byte[]>> segment = new ArrayList<ArrayList<byte[]>>(); 
	    start =0;
	    int index1=0;
	    int index2=1;
	    while (start < chunk.size()) {
	    	segNO= (""+start).getBytes();	    			    	
	    	segment.get(start).add(index1,segNO);
	    	segment.get(start).add(index2,chunk.get(start));
	        start += 1;
	    }
	    
	
		return segment;
	}	
	
   
}
