/*
 *This class is responsible for: 
 * -- parallel transmission between nodes
 *
 */
public class Node implements Runnable{
	private byte[] scAdd= new byte[32]; // source address
	private byte[] desAdd= new byte[32]; // destination address
	
	public Node(byte[] scAdd,byte[] desAdd) {
       this.scAdd=scAdd;
       this.desAdd= desAdd;
       
     }

	@Override
	public void run() {
		 
		
	}
	
	

}
