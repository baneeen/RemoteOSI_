import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


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
		
		FileInputStream data = openFile("file.txt");
		
	
	}

	public static FileInputStream openFile(String fileName){
		
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
