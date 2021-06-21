package contract;

import java.util.ArrayList;

import util.Pair;

public class Contract {
	String assumption;
	String guarantee;
	ArrayList<Pair<String, String>> inputVars; // input variables and their types
	ArrayList<Pair<String, String>> outputVars; // output variables and their types
	
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
}