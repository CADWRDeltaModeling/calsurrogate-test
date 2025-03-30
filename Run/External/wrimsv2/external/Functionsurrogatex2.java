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


public class Functionsurrogatex2 extends ExternalFunction{
	private final boolean DEBUG = false;
	private static int cpuTime=0;
	private static int nCalls=0;
	private SalinitySurrogateManager ssm;

	public Functionsurrogatex2(){
		long t1 = Calendar.getInstance().getTimeInMillis();
		
		ssm=SalinitySurrogateSetup.getManager();
		long t2 = Calendar.getInstance().getTimeInMillis();
		cpuTime=cpuTime+(int) (t2-t1);
		nCalls++;
		TimeUsage.cpuTimeMap.put("surrogatex2", cpuTime);
		TimeUsage.nCallsMap.put("surrogatex2", nCalls);
	}

	public void execute(Stack stack) {

		long t1 = Calendar.getInstance().getTimeInMillis();
		
		//values in reverse order:
		Object param12 = stack.pop(); //aggParam
		Object param11 = stack.pop(); //year
		Object param10 = stack.pop(); //month
		Object param9 = stack.pop();  //ave_type
		//Object param8 = stack.pop(); 
		Object param7 = stack.pop();  //location
		Object param6 = stack.pop();  // smscg
		Object param1 = stack.pop();  // ndoi

		//cast params to correct types:
		double aggParam = ((Number) param12).doubleValue();
		int year = ((Number) param11).intValue();
		int month = ((Number) param10).intValue();
		int ave_type = ((Number) param9).intValue();
		//int variable = ((Number) param8).intValue();
		int location = ((Number) param7).intValue();
		Number[] smscg_Arr = (Number[])param6;
		int size_smscg=smscg_Arr.length;
		double[] smscg=new double[size_smscg];
		for (int i=0; i<size_smscg; i++){
			smscg[i]=smscg_Arr[i].doubleValue();
		}
		Number[] ndoi_Arr = (Number[])param1;
		int size_ndoi=ndoi_Arr.length;
		double[] ndoi=new double[size_ndoi];
		for (int i=0; i<size_ndoi; i++){
			ndoi[i]=ndoi_Arr[i].doubleValue();
		}
        // location might be omitted? It is X2
		float result = surrogatex2(ndoi, smscg, location, ave_type, month, year, aggParam);

		// push the result on the Stack
		stack.push(new Double(result));
		
		long t2 = Calendar.getInstance().getTimeInMillis();
		cpuTime=cpuTime+(int) (t2-t1);
		nCalls++;
		TimeUsage.cpuTimeMap.put("surrogatex2", cpuTime);
		TimeUsage.nCallsMap.put("surrogatex2", nCalls);
	}

	public float surrogatex2(double[] ndoi, double[] smscg, 
			int location, int ave_type, int month, int year, double aggParam){	
		
		double[][] ndoi1 = { ndoi };
		double[][] tide_energy = { { 6.560, 6.184, 5.508, 5.083, 6.913 } };
		double[][] tide_filter = { { 1. ,1. , 1., 1., 1. } };
		double[][] smscg1 = { smscg };
		
		if (DEBUG){
			System.out.println("ndoi " + Arrays.toString(ndoi));
			System.out.println("smscg " + Arrays.toString(smscg));
			System.out.println("Month " + month);
			System.out.println("Year " + year);
		}
		
		ArrayList<double[][]> floatInput = new ArrayList<double[][]>();
		floatInput.add(ndoi1);
		floatInput.add(tide_energy);
		floatInput.add(tide_filter);	
		floatInput.add(smscg1);

		float out = ssm.annEC(floatInput, location, ave_type, month, year,aggParam);
		return out;
	}
}
