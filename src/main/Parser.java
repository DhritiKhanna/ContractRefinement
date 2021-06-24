package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;
import java.util.StringTokenizer;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.RealExpr;
import com.microsoft.z3.Solver;

import contract.Contract;
import contract.ContractOperators;
import util.Pair;

public class Parser {
	
	static boolean readFirstContract = false;
		
	/**
	 * This method reads a text file that contains the information about the contracts separated with a line '-----'.
	 * @param c
	 */
	public void readContract(Contract c) {
		
		String line = "";
		try {
			File file = new File(Main.testfilename);     
			FileReader fr = new FileReader(file);    
			BufferedReader br = new BufferedReader(fr); 
			
			if(readFirstContract) {
				// scan the file till you encounter the line '-----'.
				line = br.readLine();  
				while(line!=null && !line.equals("-----")) {  
					line = br.readLine(); // Skipping the lines
				}
			}			
			while((line=br.readLine())!=null && !line.equals("-----")) {
				StringTokenizer str = new StringTokenizer(line, ":");
				String key = str.nextToken();
				String value = str.nextToken();
				if(key.equals("input")) {
					StringTokenizer st = new StringTokenizer(value, ";,<> ");
					while(st.hasMoreTokens()) {
						String varName = st.nextToken();
						String type = st.nextToken();
						c.setInputVar(new Pair<String, String>(varName, type)); // This will throw an error if file not written properly
					}
				}
				else if(key.equals("output")) {
					StringTokenizer st = new StringTokenizer(value, ";,<> ");
					while(st.hasMoreTokens()) {
						c.setOutputVar(new Pair<String, String>(st.nextToken(), st.nextToken())); // This will throw an error if file not written properly
					}
				}
				else if(key.equals("assumption")) { // To be taken in FOL formula. Must be separated with conjunctions
					c.setAssumption(value);
				}
				else if(key.equals("guarantee")) { // To be taken in FOL formula. Must be separated with conjunctions
					c.setGuarantee(value);	
				}
			}  
			fr.close();  
		}  
		catch(IOException e) { System.out.println("Error in reading the test file!"); }
	}
	
	/**
	 * For now I have assumed that the string user passed as assumption and guarantee are syntactically and semantically correct.
	 * Assumption1: assume and guarantee formulas are made of expressions joined with conjunctions: the token "and"
	 * Assumption2: each token of one conjunct is separated with spaces. Like: tz <= max ( tx , ty ) + 1
	 * Here, I declare the input and output constants, and form expressions corresponding to the assumptions and guarantees.  
	 * @param c Contract that is to be parsed.
	 */
	public void parseContract(Contract c, ContractOperators op) {

		Solver solver = op.getSolver();
		Context ctx = op.getContext();
		
		// First step is to declare the conditions based on the types of input and output variables
		ArrayList<Pair<String, String>> inputVars = c.getInputVars();
		ArrayList<Pair<String, String>> outputVars = c.getInputVars();
		
		Iterator<Pair<String, String>> it = inputVars.iterator();
		while(it.hasNext()) {
			Pair<String, String> p = it.next(); // input variable name and its type
			String name = p.first;
			String type = p.second; // Can be bool, int, int+, real, real+. We are only concerned with int+ and real+.
			
			IntExpr zero = ctx.mkInt(0);
			
			if(type.equalsIgnoreCase("int+")) {
				IntExpr var = ctx.mkIntConst(name); 
				solver.add(ctx.mkGt(var, zero)); // var>0
			}
			else if(type.equalsIgnoreCase("real+")) {
				RealExpr var = ctx.mkReal(name);
				solver.add(ctx.mkGt(var, zero)); // var>0
			}
		}
		
		it = outputVars.iterator();
		
		// Second step is to form expressions for assume assertions
		String assumption = c.getAssumption();
		String [] splitString = assumption.split("and");
		//StringTokenizer str = new StringTokenizer(assumption, "\\sand\\s", true); // 3rd argument 'true' means Consider 'and' as a token
		
		ArrayList<BoolExpr> conjuncts = new ArrayList<BoolExpr>();
		
		for(int i=0; i<splitString.length; ++i) {
			// Form the z3 expression which is an infix expression to a prefix expression
			String infix = splitString[i];
			BoolExpr z3Expr = (BoolExpr) parseInfixExpressionIntoZ3String(infix, ctx);
			conjuncts.add(z3Expr);			
		}
		BoolExpr[] conjunctsArr = returnArray(conjuncts);
		solver.add(conjunctsArr);
		c.setAssumptionZ3Expr(conjunctsArr);
		
		// Third step is to form expressions for guarantee assertions
		String guarantee = c.getGuarantee();
		splitString = guarantee.split("and");
		//str = new StringTokenizer(guarantee, "\\sand\\s", true); // 3rd argument 'true' means Consider 'and' as a token
		
		conjuncts = new ArrayList<BoolExpr>();
		
		for(int i=0; i<splitString.length; ++i) {
			// Form the z3 expression which is an infix expression to a prefix expression
			String infix = splitString[i];
			Object obj = parseInfixExpressionIntoZ3String(infix, ctx);
			if(obj != null) {
				BoolExpr z3Expr = (BoolExpr) obj;
				conjuncts.add(z3Expr);
			}
		}
		conjunctsArr = returnArray(conjuncts);
		solver.add(conjunctsArr);
		c.setGuaranteeZ3Expr(conjunctsArr);
	}
	
	private Expr parseInfixExpressionIntoZ3String(String infix, Context ctx) {
		
		if(infix.contains("in")) {
			System.out.println(infix);
			String[] splitString = infix.split("[\\sin]");
			if(splitString.length != 2)
				return null;
			String varName = splitString[0];
			String domain = splitString[1];
			if(domain.equalsIgnoreCase("int")) {
				ctx.mkIntConst(varName);
				return null;
			}
			else if(domain.equalsIgnoreCase("real")) {
				ctx.mkRealConst(varName);
				return null;
			}
		}
		
		// First, reverse the given infix expression.
		StringBuilder infixStrBldr = new StringBuilder(); 
		
		StringTokenizer st = new StringTokenizer(infix, " ");
		while(st.hasMoreTokens()) {
			infixStrBldr.append(st.nextToken() + " ");
		}
		String reversedInfix = infixStrBldr.reverse().toString();
		// Replace ( with ) and ( with ) 
		reversedInfix.replaceAll("\\(", "@");
		reversedInfix.replaceAll("\\)", "\\(");
		reversedInfix.replaceAll("@", "\\)");
		/*
			Scan the tokens one by one.
			If the character is an operand, copy it to the prefix notation output.
			If the character is a closing parenthesis, then push it to the stack.
			If the character is an opening parenthesis, pop the elements in the stack until we find the corresponding closing parenthesis.
			If the character scanned is an operator
			If the operator has precedence greater than or equal to the top of the stack, push the operator to the stack.
			If the operator has precedence lesser than the top of the stack, pop the operator and output it to the prefix notation output and then check the above condition again with the new top of the stack.
			After all the characters are scanned, reverse the prefix notation output.
		*/
		StringTokenizer tokenizer = new StringTokenizer(reversedInfix, " ");
		Stack<String> stack = new Stack<String>();
		StringBuilder revPrefix = new StringBuilder();
		while(tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			if(token.equals("(")) {
				String popped = stack.pop();
				while(!popped.equals(")") && !stack.isEmpty()) {
					popped = stack.pop();
				}
			}
			else if(token.equals(")")) {
				stack.push(token);
			}
			else if(isOperator(token)) {
				if(!stack.isEmpty()) {
					String topStack = stack.peek();
				
					while(!precedes(token, topStack)) {
						String popped = stack.pop();
						revPrefix.append(popped + " ");
						if(stack.isEmpty())
							break;
						topStack = stack.peek();
					}
				}
				stack.push(token);
			}
			else  { // must be an operand
				revPrefix.append(token + " ");
			}
		}
		
		while(!stack.isEmpty())
			revPrefix.append(stack.pop() + " ");
		
		String prefix = revPrefix.reverse().toString();
		// There might be a space character at the start of this prefix expression, remove that.
		if(prefix.charAt(0) == ' ')
			prefix =  prefix.substring(1, prefix.length());
		
		System.out.println(prefix);
		
		// Convert the prefix string to a z3 expression. This is a recursive algorithm.
		Expr z3Expr = convertPrefixStringToZ3(prefix, ctx);		
		
		return z3Expr;
	}
	
	private Expr convertPrefixStringToZ3(String prefix, Context ctx) {
		
		String firstToken = "", rest = "";
		int firstSpace = prefix.indexOf(" ");
		if(firstSpace != -1) {
			firstToken = prefix.substring(0, firstSpace);
			rest = prefix.substring(firstSpace+1, prefix.length());
		}
		else {
			firstToken = prefix;
		}
		
		if(isIntConstant(firstToken)) {
			return ctx.mkInt(firstToken); 
		}
		else if(isRealConstant(firstToken)) {
			return ctx.mkReal(firstToken); 
		}
		else if(isOperator(firstToken)) {
			int spaceChar = rest.indexOf(" ");
			String operand1 = rest.substring(0, spaceChar);
			String operand2 = rest.substring(spaceChar+1, rest.length());
			IntExpr e1 = (IntExpr) convertPrefixStringToZ3(operand1, ctx);
			IntExpr e2 = (IntExpr) convertPrefixStringToZ3(operand2, ctx);
			if(firstToken.equals("+"))
				return ctx.mkAdd(e1, e2);
			else if(firstToken.equals("-"))
				return ctx.mkSub(e1, e2);
			else if(firstToken.equals("*"))
				return ctx.mkMul(e1, e2);
			else if(firstToken.equals("/"))
				return ctx.mkDiv(e1, e2);
			else if(firstToken.equals("="))
				return ctx.mkEq(e1, e2);
			else if(firstToken.equals("!="))
				return ctx.mkNot(ctx.mkEq(e1, e2));
			else if(firstToken.equals("<"))
				return ctx.mkLt(e1, e2);
			else if(firstToken.equals("<="))
				return ctx.mkLe(e1, e2);
			else if(firstToken.equals(">"))
				return ctx.mkGt(e1, e2);
			else if(firstToken.equals(">="))
				return ctx.mkGe(e1, e2);
		}
		else { // Must be a variable 
			// TODO: Check the data type before forming a constant. For now I am creating just the integer constant
			
			return ctx.mkIntConst(firstToken);
		}
		
		return ctx.mkFalse();
	}
	
	private boolean isIntConstant(String firstToken) {
		try {
			Integer.parseInt(firstToken);
			return true;
		}
		catch(NumberFormatException e) { return false; }
	}
	
	private boolean isRealConstant(String firstToken) {
		try {
			Double.parseDouble(firstToken);
			return true;
		}
		catch(NumberFormatException e) { return false; }
	}
	
	private BoolExpr[] returnArray(ArrayList<BoolExpr> arrayList) {
		BoolExpr[] arr = new BoolExpr[arrayList.size()];
		int i=0;
		Iterator<BoolExpr> iterator = arrayList.iterator();
		while(iterator.hasNext()) {
			arr[i] = iterator.next();
			i++;
		}
		return arr;
	}
	
	private boolean isOperator(String token) {
		// I have reversed the operator symbols here because when this method is called the string might be reversed.
		if(token.equals("=<") || token.equals("<") || token.equals("=>") || token.equals(">") || token.equals("==") || token.equals("=!"))
			return true;
		if(token.equals("<=") || token.equals("<") || token.equals(">=") || token.equals(">") || token.equals("==") || token.equals("!="))
			return true;
		if(token.equals("+") || token.equals("-") || token.equals("*") || token.equals("/") || token.equals("="))
			return true;
		return false;
	}
	
	private boolean precedes(String op1, String op2) { // Returns true if op1 has higher or equal precedence than op2
		switch(op1) {
			case "*":
			case "/":
				if(op2.equals("+") || op2.equals("-") || op2.equals("*") || op2.equals("/"))
					return true;
			case "+":
			case "-":
				if(op2.equals("+") || op2.equals("-"))
					return true;
			case "<=":
			case ">=":
			case "<":
			case ">":
			case "==":
			case "!=":
				if(op2.equals("<=") || op2.equals(">=") || op2.equals("<") || op2.equals(">") || op2.equals("==") || op2.equals("!="))
					return true;
		}
			
		return false;
	}
}