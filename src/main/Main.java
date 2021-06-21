package main;

import contract.Contract;
import contract.ContractOperators;

public class Main {
	
	public static Contract c1, c2;
	
	public static void main(String [] args) {
		
		Parser parser = new Parser();
		parser.readContracts();
		parser.parseContracts();
		
		ContractOperators operators = new ContractOperators();
		operators.checkRefinement(c1, c2);
	}
}
