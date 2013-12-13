package OSI;

import java.util.BitSet;

/*
 *This class is responsible for: 
 * -- parallel transmission between nodes
 *
 */
public class Node implements Runnable{
	private byte[] scAdd= new byte[32]; // source address
	private byte[] desAdd= new byte[32]; // destination address
	
	private int ipSize=32; // IP size in bytes (column)
	private int ipNum=5; // numbers of IPs (rows)
	private int ipField=3; // number of fields for a row
	private int macField=2;
	
	private byte[][][] routingTable=new byte[ipNum][ipSize][ipField];
	private byte[][][] macMapTable=new byte[ipNum][ipSize][macField];

	
	public Node(byte[] scAdd,byte[] desAdd) {
      
		this.scAdd=scAdd;
		this.desAdd= desAdd;

		createRoutingTable();
		createMacMapping();
     }
	
	@Override
	public void run() {
		
		
		String fileName="file.txt";
		PDU data =new PDU();
		// 1- break file into segments + Construct TCP header
		PDU[] segment=data.tarnsportToLower(fileName);
		// 2- Construct IP header
		PDU[] packet = data.networkToLower(scAdd, desAdd, segment);
		// 3- Construct Ethernet header 
		PDU[] frame= data.macToLower("eth1".getBytes(), "eth0".getBytes(), packet);
		// 4- transform frame to bits
		BitSet[] bits=data.physicalToLower(frame);
		
		//System.out.println("SEQ-NO:"+new String(segment[i].getSegNO()));
		//System.out.println("Data:"+new String(segment[i].getData()));
	    
	for(int i=0;i<bits.length;i++){
		System.out.println("Data:"+new String(bits[i].toByteArray()));
		
	}
		
	}
	public void createRoutingTable(){
		
		// Node A
	       routingTable[0][0]=("10.10.20.1").getBytes();
	       routingTable[0][1]=("255.255.255.0").getBytes();
	       routingTable[0][2]=("10.10.20.2").getBytes();
	       
	       // Node R - interface 1
	       routingTable[1][0]=("10.10.20.2").getBytes();
	       routingTable[1][1]=("255.255.255.0").getBytes();
	       routingTable[1][2]=("192.168.25.10").getBytes();
	       
	       // Node R - interface 2
	       routingTable[2][0]=("192.168.25.10").getBytes();
	       routingTable[2][1]=("255.255.255.0").getBytes();
	       routingTable[2][2]=("10.10.20.2").getBytes();
	       
	       // Node B 
	       routingTable[3][0]=("192.168.25.20").getBytes();
	       routingTable[3][1]=("255.255.255.0").getBytes();
	       routingTable[3][2]=("192.168.25.10").getBytes();
	       
	       // Node C 
	       routingTable[4][0]=("192.168.25.15").getBytes();
	       routingTable[4][1]=("255.255.255.0").getBytes();
	       routingTable[4][2]=("192.168.25.10").getBytes();
	       
	}
	
	public void createMacMapping(){
		// Node A
		   macMapTable[0][0]=("10.10.20.1").getBytes();
		   macMapTable[0][1]=("00A0C9").getBytes();
	       
	       // Node R - interface 1
		   macMapTable[1][0]=("10.10.20.2").getBytes();
		   macMapTable[1][1]=("00A0C8").getBytes();
	       
	       // Node R - interface 2
		   macMapTable[2][0]=("192.168.25.10").getBytes();
		   macMapTable[2][1]=("00B0C9").getBytes();	    
		   
	       // Node B 
		   macMapTable[3][0]=("192.168.25.20").getBytes();
		   macMapTable[3][1]=("00B0C8").getBytes();
	       
	       // Node C 
		   macMapTable[4][0]=("192.168.25.15").getBytes();
		   macMapTable[4][1]=("00B0C7").getBytes();	      
	}

}
