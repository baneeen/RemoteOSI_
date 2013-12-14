package OSI;

import java.util.ArrayList;
import java.util.List;

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
	
	private String signal=null;

	
	public Node(byte[] scAdd,byte[] desAdd) {
      
		this.scAdd=scAdd;
		this.desAdd= desAdd;

		createRoutingTable();
		createMacMapping();
		signal="Ende of File";
     }
	
	@Override
	public void run() {
		
		
		String fileName="file.txt";
		PDU data =new PDU();
		// break file into segments + Construct TCP header
		PDU[] pdu=data.tarnsportToLower(fileName);
		
		for (int i=0;i< pdu.length ;i++){
		
		// 1- construct TCP header
		System.out.println("-----------------------------------");
		System.out.println("Node IP: "+scAdd+"is sending");
		System.out.println("SEQ-NO:"+new String(pdu[i].getSegNO()));
		
		// 2- Construct IP header
		PDU packet = data.networkToLower(scAdd, desAdd, pdu[i]);
		System.out.println("Identfication:"+new String(packet.getIden()));
		System.out.println("Offest:"+new String(packet.getOff()));
		System.out.println("Source Address:"+new String(packet.getScAdd()));
		System.out.println("Destination Address:"+new String(packet.getDesAdd()));
		
		// 3- Construct Ethernet header 
		byte[] scMAC= findMAC(scAdd);
		byte[] desMAC= findMAC(desAdd);
		PDU frame= data.macToLower(scMAC, desMAC, packet);
		System.out.println("Preamble:"+new String(frame.getPreamble()));
		System.out.println("Source MAC:"+new String(frame.getScMAC()));
		System.out.println("Destination MAC:"+new String(frame.getDesMAC()));
		System.out.println("Checksum:"+frame.getChecksum());
		System.out.println("CRC:"+frame.getCrc());
		
		
		// 4- send frame to the network medium
		
		
		}
		List <PDU> arryPDU =new ArrayList<PDU>();
		while (true){
		
	    System.out.println("-----------------------------------");
	    //1- take PDU from the network 
	    PDU pdu1 = null;
	    System.out.println("Node IP: "+scAdd+"is receiving");

	    // 2- check correctness of frame
	    System.out.println("FN:Checking the correctness of the frame..");
	    int flag=data.macToUpper(pdu1);
	    int correct =1;
	    if ( flag!=correct){
	    	System.out.println("FN-R:Corrupted!The frame is dropped from the netwrok..");
	    	continue;
	    } 
	    System.out.println("FN-R:The frame is not Corrupted ");
	    
		System.out.println("Preamble:"+new String(pdu1.getPreamble()));
		System.out.println("Source MAC:"+new String(pdu1.getScMAC()));
		System.out.println("Destination MAC:"+new String(pdu1.getDesMAC()));
		System.out.println("Checksum:"+pdu1.getChecksum());
		System.out.println("CRC:"+pdu1.getCrc());
	
	    	
	    //3- check network range
	    
	    byte[] scMask=findMask(scAdd);
	    byte[] desMask=findMask(pdu1.getDesAdd());
	    int myNode=1;
	    int lanNodes=2;
	    int otherNetwork=3;
	    
	    System.out.println("FN:Checking the network range of the frame's destination..");
	    flag= data.networkToUpper(scAdd, scMask, desMask, pdu1);
	    
	    if(flag ==myNode)
	    {
	    	arryPDU.add(pdu1);
		    System.out.println("FN-R:The frame is for the current node ");
		    
	    }else if (flag==lanNodes){
	    	
	    	System.out.println("FN-R:The frame is for the other node in the LAN");
	    	// put in the queue
	    }else {
	    	System.out.println("FN-R:The frame is for other network ");
	    	// pass to router
	    }
	 
		}
		
	}
	public byte[] findMask(byte[] ip){
		byte[] mask=null;
		for (int i=0;i< routingTable.length;i++)
			if( routingTable[i][0].equals(ip))
				mask= routingTable[i][1];
		return mask;
		
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
public byte[] findMAC(byte[] ip){
	
	int ipIndex=0;
	int macIndex=1;
	
	for(int i=0;i<ipNum;i++)
	{
		
		if (new String(macMapTable[i][ipIndex]).equals(new String(ip)))
		{
	
		
			return macMapTable[i][macIndex];
			
		}
	}

	return "0000".getBytes();
}
}
