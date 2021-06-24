package main;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import contract.Contract;
import contract.ContractOperators;

public class Main {
	
	public Contract c1, c2;
	public static String testfilename;
	
	public static void main(String [] args) {
		
		Options options = new Options();

        Option opt = new Option("tfn", "testfilename", true, "Name of the test file");
        opt.setRequired(true);
        options.addOption(opt);
        
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("", options);

            System.exit(1);
        }

        testfilename = cmd.getOptionValue("testfilename");
		
        new Main().start();
	}
	
	Main() {
		c1 = new Contract();
		c2 = new Contract();
	}
	
	public void start() {
		
		Parser parser = new Parser();
		ContractOperators op = new ContractOperators();
		
		parser.readContract(c1);
		Parser.readFirstContract = true;
		parser.readContract(c2);
		
		parser.parseContract(c1, op);
		parser.parseContract(c2, op);
		
		boolean result = op.checkRefinement(c1, c2);
		
		if(result)
			System.out.println("\n\n**** Contract c1 refines Contract c2\n\n");
		else
			System.out.println("\n\n**** Contract c1 does not refine Contract c2\n\n");
	}
}