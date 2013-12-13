package OSI;

import java.util.BitSet;

/*
 *This class is responsible for: 
 * -- parallel transmission between nodes
 *
 */
public class Node implements Runnable{
	//private byte[] scAdd= new byte[32]; // source address
	//private byte[] desAdd= new byte[32]; // destination address
	
	public Node(byte[] scAdd,byte[] desAdd) {
       //this.scAdd=scAdd;
       //this.desAdd= desAdd;
       
     }

	@Override
	public void run() {
		String fileName="file.txt";
		PDU data =new PDU();
		
		PDU[] segment=data.tarnsportToLower(fileName);
		PDU[] packet = data.networkToLower("10.10.20.2".getBytes(), "10.10.20.1".getBytes(), segment);
		PDU[] frame= data.macToLower("eth1".getBytes(), "eth0".getBytes(), packet);
		BitSet[] bits=data.physicalToLower(frame);
		//System.out.println("SEQ-NO:"+new String(segment[i].getSegNO()));
		//System.out.println("Data:"+new String(segment[i].getData()));
	    
	for(int i=0;i<bits.length;i++){
		System.out.println("Data:"+new String(bits[i].toByteArray()));
		
	}
		
	}
	
	

}
