package OSI;

//============================================================================
//Team Members:
//Huang, Weijun 
//AL Mubarak, Baneen
//
//CSE 5231	Computer Networks
// Project
// Description: this project is a simplification of OSI Model
//==========================

public class Main {

	public static void main(String[] args) {
		
		System.out.println("Welcome");
		
		//Node A
		byte[] aScIP=("10.10.20.1").getBytes();
		byte[] aDesIP=("192.168.25.15").getBytes();
		Thread a =new Thread(new Node(aScIP,aDesIP));
			
		
		
		a.start();
		
		
		
		
		
		
		
		
		
	
	}


}
