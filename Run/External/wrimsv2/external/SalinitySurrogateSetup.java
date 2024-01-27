package wrimsv2.external;

import java.io.*;
import wrimsv2.components.ControlData;
import wrimsv2.components.TimeUsage;
import wrimsv2.external.ExternalFunction;
import calsim.surrogate.*;
import wrimsv2.ilp.ILP;
import wrimsv2.config.ConfigUtils;

public class SalinitySurrogateSetup{
    static boolean isInitialized = false;
    static SalinitySurrogateManager ssm;

	public SalinitySurrogateSetup(){}

	public static SalinitySurrogateManager getManager(){
        ssm = SalinitySurrogateManager.INSTANCE;
        // Initialization should only be done once
        if (isInitialized) return ssm;
        
        // Set up call logging, which is done here in the run rather than in the library 
        // because we are using CalSIM's active logging directory
		String loggingSurrogateKey = "loggingSurrogate"; //default is false
		boolean logSurrogate = ConfigUtils.readBoolean(ConfigUtils.configMap, loggingSurrogateKey, false);
		if (logSurrogate){
			//Log output path in File format for WRIMS 2 can be obtained by the following method:
			File ilpDir=ILP.getIlpDir();  
			String logPathDir = ilpDir.getAbsolutePath() + File.separatorChar+"surrogate.log";
			ssm.enableLogging(logPathDir);
		} 

		//set up an ANN surrogate month for emmaton	
		int location = ssm.EMM_CALSIM;
		DisaggregateMonths spline = new DisaggregateMonthsSpline(5);
		DisaggregateMonths repeat = new DisaggregateMonthsRepeat(5);
		DisaggregateMonths daysOps = new DisaggregateMonthsDaysToOps(5, 1., 0.);
        //TODO this 31 above is because of weird TF implementation
        // below was {spline, spline, daysOps but 3rd member daysOps changed to repeat
		DisaggregateMonths[] disagg = { spline, spline, daysOps, spline, spline, spline, repeat };
		String dir = "ann_calsim-main/emmaton";
        Surrogate emm = loadSurrogate(dir);
        for (AggregateMonths agg : AggregateMonths.values()){
            int aggCode = agg.calsimCode;
            System.out.println("Emm call location " + location + " agg code "+aggCode);
    		if (! ssm.hasSurrogateForSite(location, aggCode)){
                System.out.println("Loading Emmaton "+aggCode);
			    SurrogateMonth month = new SurrogateMonth(disagg, emm, agg);
			    ssm.setSurrogateForSite(location, aggCode, month);
                ssm.setIndexForSite(location,0);   // 0 because univariate has only one index
            }
        }
		//set up an ANN surrogate month for emmaton	
		location = ssm.RSL_CALSIM;
		//DisaggregateMonths[] disagg = { spline, spline, daysOps, spline, spline, spline, repeat };
		dir = "ann_calsim-main/rock_slough";
        Surrogate annRS = loadSurrogate(dir);
        for (AggregateMonths agg : AggregateMonths.values()){
            int aggCode = agg.calsimCode;
            System.out.println("Rock Slough location " + location + " agg code "+aggCode);
    		if (! ssm.hasSurrogateForSite(location, aggCode)){
    			System.out.println("Loading Rockslough "+aggCode);
			    SurrogateMonth monthRS = new SurrogateMonth(disagg, annRS, agg);
			    ssm.setSurrogateForSite(location, aggCode, monthRS);
                ssm.setIndexForSite(location,0);   // 0 because univariate has only one index
            }
        }	        
		return ssm;

	}


	public static Surrogate loadSurrogate(String dir) {
		String fname = ExternalFunction.externalDir+dir;
		String[] tensorNames = { "serving_default_sac_input:0", "serving_default_exports_input:0",
				"serving_default_dcc_input:0", "serving_default_net_dcd_input:0", "serving_default_sjr_input:0",
				"serving_default_tide_input:0", "serving_default_smscg_input:0", };

		String[] tensorNamesInt = new String[0];
		String outName = "StatefulPartitionedCall:0";
		DailyToSurrogate dayToANN = new DailyToSurrogateBlocked(8, 10, 11);
		Surrogate wrap = new TensorWrapper(fname, tensorNames, tensorNamesInt, outName, dayToANN);
		return wrap;
	}	



}