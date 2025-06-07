package wrimsv2.external;

import java.io.File;
import java.util.*;
import java.io.*;

import wrimsv2.components.ControlData;
import wrimsv2.components.TimeUsage;
import wrimsv2.external.SalinitySurrogateSetup;
import calsim.surrogate.AggregateMonths;
import calsim.surrogate.DailyToSurrogate;
import calsim.surrogate.DailyToSurrogateBlocked;
import calsim.surrogate.DisaggregateMonths;
import calsim.surrogate.DisaggregateMonthsDaysToOps;
import calsim.surrogate.DisaggregateMonthsRepeat;
import calsim.surrogate.DisaggregateMonthsSpline;
import calsim.surrogate.Surrogate;
import calsim.surrogate.SurrogateMonth;
import calsim.surrogate.TensorWrapper;
import calsim.surrogate.examples.EmmatonExampleTensorFlowANN;
import calsim.surrogate.SalinitySurrogateManager;


public class Functionsurrogateec extends ExternalFunction{
	private final boolean DEBUG = false;
	private static int cpuTime=0;
	private static int nCalls=0;
	private SalinitySurrogateManager ssm;

	public Functionsurrogateec(){
		long t1 = Calendar.getInstance().getTimeInMillis();
		
		ssm=SalinitySurrogateSetup.getManager();
		long t2 = Calendar.getInstance().getTimeInMillis();
		cpuTime=cpuTime+(int) (t2-t1);
		nCalls++;
		TimeUsage.cpuTimeMap.put("surrogateec", cpuTime);
		TimeUsage.nCallsMap.put("surrogateec", nCalls);
	}

	public void execute(Stack stack) {

		long t1 = Calendar.getInstance().getTimeInMillis();
		
		//values in reverse order:
		Object param11 = stack.pop();  // year
		Object param10 = stack.pop();  // mon
		Object param9 = stack.pop();   // ave_type
		//Object param8 = stack.pop();
		Object param7 = stack.pop();   // location
		Object param6 = stack.pop();   // smscg current
		Object param6b = stack.pop();   // smscg arr
		Object param5 = stack.pop();   // sjr current
		Object param5b = stack.pop();   // sjr arr
		Object param4 = stack.pop();   // CD current
		Object param4b = stack.pop();   // channel depletion arr
		Object param3 = stack.pop();   // dcc curr
		Object param3b = stack.pop();   // dcc arr
		Object param2 = stack.pop();   // export current
		Object param2b = stack.pop();  // export arr history
		Object param1 = stack.pop();   // sac current
		Object param1b = stack.pop();  // sac arr history

		//cast params to correct types:
		int year = ((Number) param11).intValue();
		int month = ((Number) param10).intValue();
		int ave_type = ((Number) param9).intValue();
		//int variable = ((Number) param8).intValue();
		int location = ((Number) param7).intValue();
		
		double smscg_current = ((Number) param6).doubleValue();				
		Number[] smscg_Arr = (Number[])param6b;
		int size_smscg=smscg_Arr.length+1;
		double[] smscg=new double[size_smscg];
		smscg[0] = smscg_current;
		for (int i=1; i<size_smscg; i++){
			smscg[i]=smscg_Arr[i-1].doubleValue();
		}
		
		double sjr_current = ((Number) param5).doubleValue();		
		Number[] sjr_Arr = (Number[])param5b;
		int size_sjr=sjr_Arr.length+1;
		double[] sjr=new double[size_sjr];
		sjr[0] = sjr_current;
		for (int i=1; i<size_sjr; i++){
			sjr[i]=sjr_Arr[i-1].doubleValue();
		}
		
		double cd_current = ((Number) param4).doubleValue();		
		Number[] net_dcd_Arr = (Number[])param4b;
		int size_net_dcd=net_dcd_Arr.length + 1;
		double[] net_dcd=new double[size_net_dcd];
		net_dcd[0] = cd_current;
		for (int i=1; i<size_net_dcd; i++){
			net_dcd[i]=net_dcd_Arr[i-1].doubleValue();
		}
		
		double dcc_current = ((Number) param3).doubleValue();
		Number[] dcc_Arr = (Number[])param3b;
		int size_dcc=dcc_Arr.length+1;
		double[] dcc=new double[size_dcc];
		dcc[0] = dcc_current;
		for (int i=1; i<size_dcc; i++){
			dcc[i]=dcc_Arr[i-1].doubleValue();
		}
		
		double exp_current = ((Number) param2).doubleValue();
		Number[] exp_Arr = (Number[])param2b;
		int size_exp=exp_Arr.length + 1;
		double[] exp=new double[size_exp];
		exp[0] = exp_current;
		
		for (int i=1; i<size_exp; i++){
			exp[i]=exp_Arr[i-1].doubleValue();
		}
		
		double sac_current = ((Number) param1).doubleValue();
		Number[] sac_Arr = (Number[])param1b;
		int size_sac=sac_Arr.length+1;
		double[] sac=new double[size_sac];
		sac[0] = sac_current;
		for (int i=1; i<size_sac; i++){
			sac[i]=sac_Arr[i-1].doubleValue();
		}

		float result = surrogateec(sac, exp, dcc, net_dcd, sjr, smscg, location, ave_type, month, year);

		// push the result on the Stack
		stack.push(Double.valueOf(result));
		
		long t2 = Calendar.getInstance().getTimeInMillis();
		cpuTime=cpuTime+(int) (t2-t1);
		nCalls++;
		TimeUsage.cpuTimeMap.put("surrogateec", cpuTime);
		TimeUsage.nCallsMap.put("surrogateec", nCalls);
	}

	public float surrogateec(double[] sac, double[] exp, double[] dcc, double[] net_dcd, double[] sjr, double[] smscg, 
			int location, int ave_type, int month, int year){	
		
		double[][] sac1 = { sac };
		double[][] exp1 = { exp };

		double[][] dcc1 = { dcc };
		double[][] net_dcd1 = { net_dcd };
		double[][] sjr1 = { sjr };
		double[][] tide_energy = { { 6.560, 6.184, 5.508, 5.083, 6.913 } };
		double[][] tide_filter = { { 1. ,1. , 1., 1., 1. } };
		double[][] smscg1 = { smscg };
		
		if (DEBUG){
			System.out.println("sac " + Arrays.toString(sac));
			System.out.println("smscg " + Arrays.toString(smscg));
			System.out.println("Month " + month);
			System.out.println("Year " + year);
		}
		
		ArrayList<double[][]> floatInput = new ArrayList<double[][]>();
		floatInput.add(sac1);
		floatInput.add(exp1);
		floatInput.add(sjr1);
		floatInput.add(net_dcd1);
		floatInput.add(tide_energy);
		floatInput.add(tide_filter);	
		floatInput.add(dcc1);		
		floatInput.add(smscg1);

		float out = ssm.annEC(floatInput, location, ave_type, month, year);
		return out;
	}
}
