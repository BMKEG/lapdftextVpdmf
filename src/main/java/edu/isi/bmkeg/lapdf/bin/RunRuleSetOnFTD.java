package edu.isi.bmkeg.lapdf.bin;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import edu.isi.bmkeg.ftd.model.FTD;
import edu.isi.bmkeg.ftd.model.FTDRuleSet;
import edu.isi.bmkeg.ftd.model.qo.FTDRuleSet_qo;
import edu.isi.bmkeg.ftd.model.qo.FTD_qo;
import edu.isi.bmkeg.lapdf.controller.LapdfMode;
import edu.isi.bmkeg.lapdf.controller.LapdfVpdmfEngine;
import edu.isi.bmkeg.lapdf.model.LapdfDocument;
import edu.isi.bmkeg.utils.Converters;
import edu.isi.bmkeg.vpdmf.model.instances.LightViewInstance;

public class RunRuleSetOnFTD {

	private static Logger logger = Logger.getLogger(RunRuleSetOnFTD.class);
	
	private static String USAGE = "usage: <ftdName> <ftdRuleSet> <dbName> <login> <password>";

	public static void main(String args[]) throws Exception	{

		if ( args.length != 5 ) {
			System.err.println(USAGE);
			System.exit(1);
		}
		
		String ftdName = args[0];
		String ftdRuleSetName = args[1];
		String dbName = args[2];
		String login = args[3];
		String password = args[4];
 	
		LapdfVpdmfEngine lapdfEng = new LapdfVpdmfEngine();		
		lapdfEng.initializeVpdmfDao(login, password, dbName);
				
		FTDRuleSet_qo qrs = new FTDRuleSet_qo();
		qrs.setFileName(ftdRuleSetName);
		List<LightViewInstance> l = lapdfEng.getFtdDao().getCoreDao().list(qrs, "FTDRuleSet");
		if( l.size() == 0 ) {
			throw new Exception("The appropriate rule file (" + ftdRuleSetName + 
					"has NOT been uploaded to the database, please run AddFTDRuleSet");
		} else if( l.size() > 1 ) {
			throw new Exception("The appropriate rule file (" + ftdRuleSetName + 
					"appears more than once in the database, correct this manually");
		}
		
		Long vpdmfId = l.get(0).getVpdmfId();
		FTDRuleSet rs = lapdfEng.getFtdDao().getCoreDao().findById(vpdmfId, new FTDRuleSet(), "FTDRuleSet");

		FTD_qo qftd = new FTD_qo();
		qftd.setName(ftdName);
		List<LightViewInstance> l2 = lapdfEng.getFtdDao().getCoreDao().list(qftd, "FTD");
		if( l.size() != 1 ) {
			throw new Exception("The appropriate pdf file(" + ftdName + 
					"has NOT been uploaded to the database, please run AddFTDRuleSet");
		}
		
		Long vpdmfId2 = l2.get(0).getVpdmfId();
		FTD ftd = lapdfEng.getFtdDao().getCoreDao().findById(vpdmfId2, new FTD(), "FTD");
		
		lapdfEng.getFtdDao().runRuleSetOnFtd(ftd, rs);
	
	}

}
