package contract;

import java.util.ArrayList;

import com.microsoft.z3.BoolExpr;

import util.Pair;

public class Contract {
	private String assumption; // As input by the user
	private String guarantee;  // As input by the user
	private ArrayList<Pair<String, String>> inputVars;  // input variables and their types
	private ArrayList<Pair<String, String>> outputVars; // output variables and their types
	
	private BoolExpr[] assumptionZ3Expr; // Filled in by the parser
	private BoolExpr[] guaranteeZ3Expr;  // Filled in by the parser
	
	public Contract() {
		inputVars = new ArrayList<Pair<String, String>>();
		outputVars = new ArrayList<Pair<String, String>>();
	}
	
	public String getAssumption() {
		return assumption;
	}
	public void setAssumption(String assumption) {
		this.assumption = assumption;
	}
	public String getGuarantee() {
		return guarantee;
	}
	public void setGuarantee(String guarantee) {
		this.guarantee = guarantee;
	}
	public ArrayList<Pair<String, String>> getInputVars() {
		return inputVars;
	}
	public Pair<String, String> getInputVar(int i) {
		return inputVars.get(i);
	}
	public void setInputVars(ArrayList<Pair<String, String>> inputVars) {
		this.inputVars = inputVars;
	}
	public void setInputVar(Pair<String, String> inputVar) {
		inputVars.add(inputVar);
	}
	public ArrayList<Pair<String, String>> getOutputVars() {
		return outputVars;
	}
	public Pair<String, String> getOutputVar(int i) {
		return outputVars.get(i);
	}
	public void setOutputVars(ArrayList<Pair<String, String>> outputVars) {
		this.outputVars = outputVars;
	}
	public void setOutputVar(Pair<String, String> outputVar) {
		outputVars.add(outputVar);
	}

	public BoolExpr[] getAssumptionZ3Expr() {
		return assumptionZ3Expr;
	}
	public void setAssumptionZ3Expr(BoolExpr[] assumptionZ3Expr) {
		this.assumptionZ3Expr = assumptionZ3Expr;
	}
	public BoolExpr[] getGuaranteeZ3Expr() {
		return guaranteeZ3Expr;
	}
	public void setGuaranteeZ3Expr(BoolExpr[] guaranteeZ3Expr) {
		this.guaranteeZ3Expr = guaranteeZ3Expr;
	}
}