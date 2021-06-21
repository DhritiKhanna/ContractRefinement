package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

import contract.Contract;
import util.Pair;

public class Parser {
	
	boolean readFirstContract = false;
	
	public void readContracts() {
		Main.c1 = new Contract();
		Main.c2 = new Contract();
		readContract(Main.c1);
		readFirstContract = true;
		readContract(Main.c2);
	}
	
	/**
	 * This method reads a text file that contains the information about the contracts separated with a line '-----'.
	 * @param c
	 */
	private void readContract(Contract c) {
		if(readFirstContract) {
			// scan the file till you encounter the line '-----'.
			try {  
				File file = new File("Contracts.txt");     
				FileReader fr = new FileReader(file);    
				BufferedReader br = new BufferedReader(fr);    
				String line;  
				while((line=br.readLine())!=null && line.equals("-----")) {  
					; // Skipping the lines
				}  
				fr.close();   
			}  
			catch(IOException e) { }
		}
		try {  
			File file = new File("Contracts.txt");     
			FileReader fr = new FileReader(file);    
			BufferedReader br = new BufferedReader(fr);    
			String line;  
			
			while((line=br.readLine())!=null && !line.equals("-----")) {
				StringTokenizer str = new StringTokenizer(line, ": ");
				String key = str.nextToken();
				String value = str.nextToken();
				if(key.startsWith("inputs")) {
					StringTokenizer st = new StringTokenizer(value, ";,<> ");
					while(st.hasMoreTokens()) {
						c.setInputVar(new Pair<String, String>(st.nextToken(), st.nextToken())); // This will throw an error if file not written properly
					}
				}
				else if(key.startsWith("outputs")) {
					StringTokenizer st = new StringTokenizer(value, ";,<> ");
					while(st.hasMoreTokens()) {
						c.setOutputVar(new Pair<String, String>(st.nextToken(), st.nextToken())); // This will throw an error if file not written properly
					}
				}
				else if(key.startsWith("assumptions")) { // To be taken in FOL formula. Must be separated with conjunctions
					c.setAssumption(value);
				}
				else if(key.startsWith("guarantees")) { // To be taken in FOL formula. Must be separated with conjunctions
					c.setAssumption(value);	
				}
			}  
			fr.close();  
		}  
		catch(IOException e) { }
	}
	
	public void parseContracts() {
		
	}
}