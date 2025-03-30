package wrimsv2.external;

import java.util.*;

import calsim.surrogate.SalinitySurrogateManager;
import wrimsv2.components.ControlData;
import wrimsv2.components.TimeUsage;

public class Functionrequiredflowx2 extends ExternalFunction{
	private final boolean DEBUG = false;
	private static int cpuTime=0;
	private static int nCalls=0;
	private SalinitySurrogateManager ssm;


	public Functionrequiredflowx2(){
		long t1 = Calendar.getInstance().getTimeInMillis();
		ssm=SalinitySurrogateSetup.getManager();
		long t2 = Calendar.getInstance().getTimeInMillis();
		cpuTime=cpuTime+(int) (t2-t1);
		nCalls++;
		TimeUsage.cpuTimeMap.put("requiredflowx2", cpuTime);
		TimeUsage.nCallsMap.put("requiredflowx2", nCalls);
	}

	public void execute(Stack stack) {
		
		long t1 = Calendar.getInstance().getTimeInMillis();
		
		//values in reverse order:
		Object param19 = stack.pop();
		Object param18 = stack.pop();
		Object param17 = stack.pop();
		Object param16 = stack.pop();
		Object param15 = stack.pop();
		Object param14 = stack.pop();
		Object param13 = stack.pop();
		Object param12 = stack.pop();
		Object param3 = stack.pop();
		Object param2 = stack.pop();
		Object param1 = stack.pop();
		Object param0 = stack.pop();

		//cast params to correct types:
		int year = ((Number) param19).intValue();
		int month = ((Number) param18).intValue();
		int ave_type = ((Number) param17).intValue();
		int location = ((Number) param16).intValue();
		double ndoHiBound = ((Number) param15).doubleValue();
		double ndoLoBound = ((Number) param14).doubleValue();
		double SMSCG_fut = ((Number) param13).doubleValue();
		Number[] SMSCG_prv_Arr = (Number[])param12;
		int size_SMSCG_prv=SMSCG_prv_Arr.length;
		double[] SMSCG_prv=new double[size_SMSCG_prv];
		for (int i=0; i<size_SMSCG_prv; i++){
			SMSCG_prv[i]=SMSCG_prv_Arr[i].doubleValue();
		}
		double Qndoi_est = ((Number) param3).doubleValue();
		Number[] Qndoi_prv_Arr = (Number[])param2;
		int size_Qndoi_prv=Qndoi_prv_Arr.length;
		double[] Qndoi_prv=new double[size_Qndoi_prv];
		for (int i=0; i<size_Qndoi_prv; i++){
			Qndoi_prv[i]=Qndoi_prv_Arr[i].doubleValue();
		}
		int nday = ((Number) param1).intValue();
		double target = ((Number) param0).doubleValue();

		double result = requiredflowX2(target, nday, Qndoi_prv, Qndoi_est,
                                    SMSCG_prv, SMSCG_fut, 
                                    ndoLoBound, ndoHiBound, location, ave_type, month, year);

		// push the result on the Stack
		stack.push(new Double(result));
		
		long t2 = Calendar.getInstance().getTimeInMillis();
		cpuTime=cpuTime+(int) (t2-t1);
		nCalls++;
		TimeUsage.cpuTimeMap.put("requiredsac", cpuTime);
		TimeUsage.nCallsMap.put("requiredsac", nCalls);

	}

	public double requiredflowX2(double target, int nday, double[] Qndoi_prv, double Qndoi_est,
                              double[] SMSCG_prv, double SMSCG_fut, 
                              double ndoLoBound, double ndoHiBound, 
                              int location, int ave_type, int month, int year){
		int NHIST = 5;
		double[][] ndo = new double[1][NHIST];
		double[][] tidal_energy = new double[1][NHIST];
		double[][] tidal_filter = new double[1][NHIST];		
		double[][] smscg = new double[1][NHIST];

		// Batching isn't very useful here, and thus hard to remember what this index
		// is for. This is just a reminder it is for batching.
		final int BATCHZERO = 0;
		ndo[BATCHZERO][0] = (double) (Qndoi_est);
		tidal_energy[BATCHZERO][0] = -999.;
		tidal_filter[BATCHZERO][0] = -999.;
		smscg[BATCHZERO][0] = (double) SMSCG_fut;
       
		double aggParam = (double) nday;
		for (int ihist = 1; ihist < NHIST; ihist++) {
			ndo[BATCHZERO][ihist] = (double) (Qndoi_prv[ihist-1]); 
			tidal_energy[BATCHZERO][ihist] = 0.;
			tidal_filter[BATCHZERO][ihist] = 0.;
			smscg[BATCHZERO][ihist] = (double) SMSCG_prv[ihist-1];
		}

		ArrayList<double[][]> monthlyInput = new ArrayList<double[][]>(
				Arrays.asList(ndo,tidal_energy, tidal_filter, smscg));

		System.out.println("Query for location "+location+" aggParam " + aggParam);
		float out = (float) ssm.requiredFlow(target, monthlyInput, 
				ndoLoBound, ndoHiBound, location, ave_type, month, year, aggParam);	
		
		return out;
	}
}
