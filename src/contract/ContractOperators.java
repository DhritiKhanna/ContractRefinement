package contract;

import java.util.HashMap;

import com.microsoft.z3.Context;
import com.microsoft.z3.Log;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;
import com.microsoft.z3.Version;
import com.microsoft.z3.Z3Exception;

public class ContractOperators {
	
	private Context ctx;
	public Solver solver;
	
	public Context getContext() {
		return ctx;
	}
	
	public Solver getSolver() {
		return solver;
	}

	private boolean loggingOn = false; 
	
	private void log(Object x) {
		if(loggingOn)
			System.out.println(x);
	}
	
	public ContractOperators() {
		// Set the Z3 solver
		try {
            com.microsoft.z3.Global.ToggleWarningMessages(true);
            Log.open("test.log");

            log("Z3 Major Version: " + Version.getMajor());
            log("Z3 Full Version: " + Version.getString());

            HashMap<String, String> cfg = new HashMap<String, String>();
            cfg.put("model", "true");
            ctx = new Context(cfg);
            solver = ctx.mkSolver();
        }
		catch (Z3Exception ex) {
            log("Z3 Managed Exception: " + ex.getMessage());
            log("Stack trace: ");
            ex.printStackTrace(System.out);
        } 
		catch (Exception ex) {
            log("Unknown Exception: " + ex.getMessage());
            log("Stack trace: ");
            ex.printStackTrace(System.out);
        }		
		defineCommonFunctionMacros();
	}
	
	/**
	 * Define common macros: max, min etc. This list can increase in the future 
	 */
	private void defineCommonFunctionMacros() {
		
	}
	
	/**
	 * Here, I form rules for the refinement operator
	 * The refinement (C1 \po C2) is checked using z3 solver 
	 */
	public boolean checkRefinement(Contract c1, Contract c2) {

		// Rule 1: Assumption_C2 --> Assumption_C1
		solver.add(ctx.mkImplies(ctx.mkAnd(c2.getAssumptionZ3Expr()), ctx.mkAnd(c1.getAssumptionZ3Expr())));
		
		// Rule 2: Assumption_C' ==> Guarantee_C' implies Assumption_C ==> Guarantee_C
		solver.add(ctx.mkImplies(
				ctx.mkImplies(ctx.mkAnd(c1.getAssumptionZ3Expr()), ctx.mkAnd(c1.getGuaranteeZ3Expr())), 
				ctx.mkImplies(ctx.mkAnd(c2.getAssumptionZ3Expr()), ctx.mkAnd(c2.getGuaranteeZ3Expr()))
				));
		
		Status status = solver.check();
		if(status == Status.SATISFIABLE) {
			if(loggingOn)
				System.out.println(solver.getModel());
			return true;
		} 
		return false;
	}
}