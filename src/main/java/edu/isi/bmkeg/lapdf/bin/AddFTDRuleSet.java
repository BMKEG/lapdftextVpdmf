package edu.isi.bmkeg.lapdf.bin;

import java.io.File;

import org.apache.log4j.Logger;

import edu.isi.bmkeg.ftd.model.FTDRuleSet;
import edu.isi.bmkeg.lapdf.controller.LapdfVpdmfEngine;

public class AddFTDRuleSet
{

	private static Logger logger = Logger.getLogger(AddFTDRuleSet.class);
	
	private static String USAGE = "usage: <path-to-rule-file> <dbName> <login> <password> ";

	public static void main(String args[]) throws Exception	{

		if (args.length != 6  ) {
			System.err.println(USAGE);
			System.exit(1);
		}
		
		String ruleFileLocation = args[0];
		String name = ruleFileLocation;
		String desc = "";
		String dbName = args[3];
		String login = args[4];
		String password = args[5];
 	
		LapdfVpdmfEngine lapdfEng = null;
		if (ruleFileLocation != null) {
			logger.info("Using rulefile " + ruleFileLocation);
			lapdfEng = new LapdfVpdmfEngine(new File(ruleFileLocation));
		} else {
			lapdfEng = new LapdfVpdmfEngine();
		}
		
		lapdfEng.initializeVpdmfDao(login, password, dbName);
		
		FTDRuleSet rs = lapdfEng.buildDrlRuleSet(name, desc, new File(ruleFileLocation));
				
		lapdfEng.getFtdDao().getCoreDao().insert(rs, "FTDRuleSet");
			
	}

}
