package edu.isi.bmkeg.lapdf.bin;

import java.io.File;

import org.apache.log4j.Logger;

import edu.isi.bmkeg.ftd.model.FTDRuleSet;
import edu.isi.bmkeg.lapdf.controller.LapdfVpdmfEngine;

public class AddFTDRuleSet
{

	private static Logger logger = Logger.getLogger(AddFTDRuleSet.class);
	
	private static String USAGE = "usage: <path-to-rule-file> <dbName> <login> <password> <workingDirectory>";

	public static void main(String args[]) throws Exception	{

		if (args.length != 5  ) {
			System.err.println(USAGE);
			System.exit(1);
		}
		
		String ruleFileLocation = args[0];
		String name = ruleFileLocation;
		String desc = "";
		String dbName = args[1];
		String login = args[2];
		String password = args[3];
		String workingDirectory = args[4];
 	
		LapdfVpdmfEngine lapdfEng = null;
		if (ruleFileLocation != null) {
			logger.info("Using rulefile " + ruleFileLocation);
			lapdfEng = new LapdfVpdmfEngine(new File(ruleFileLocation));
		} else {
			lapdfEng = new LapdfVpdmfEngine();
		}
		
		lapdfEng.initializeVpdmfDao(login, password, dbName, workingDirectory);
		
		FTDRuleSet rs = lapdfEng.buildDrlRuleSet(name, desc, new File(ruleFileLocation));
				
		lapdfEng.getFtdDao().getCoreDao().insert(rs, "FTDRuleSet");
			
	}

}
