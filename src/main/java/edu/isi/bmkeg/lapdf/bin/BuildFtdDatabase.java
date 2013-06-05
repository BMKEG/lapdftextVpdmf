package edu.isi.bmkeg.lapdf.bin;

import java.io.File;
import java.net.URL;

import org.apache.log4j.Logger;

import edu.isi.bmkeg.lapdf.controller.LapdfVpdmfEngine;
import edu.isi.bmkeg.vpdmf.bin.BuildDatabaseFromVpdmfArchive;
import edu.isi.bmkeg.vpdmf.model.definitions.VPDMf;

public class BuildFtdDatabase {

	public static String USAGE = "arguments: <dbName> <login> <password>"; 

	private static Logger logger = Logger.getLogger(BuildFtdDatabase.class);

	private VPDMf top;
	
	public static void main(String[] args) throws Exception {

		if( args.length != 3 ) {
			System.err.println(USAGE);
			System.exit(-1);
		}
		
		URL url = BuildFtdDatabase.class.getClassLoader().getResource("edu/isi/bmkeg/lapdf/ftd_VPDMf.zip");
		String buildFilePath = url.getFile();
		File buildFile = new File( buildFilePath );

		String dbName = args[0];
		String login = args[1];
		String password = args[2];
		
		String[] newArgs = new String[] { 
				buildFile.getPath(), args[0], args[1], args[2] 
				};
		
		BuildDatabaseFromVpdmfArchive.main(newArgs);
					
		logger.info("Digital Library Database " + args[0] + " successfully created.");

		LapdfVpdmfEngine lapdfEng = new LapdfVpdmfEngine();
		lapdfEng.initializeVpdmfDao(login, password, dbName);
		
		// need to upload the default rule file.
		File f = lapdfEng.getRuleFile();
		
		String[] args2 = {
				f.getPath(),
				dbName, login, password
			};

		AddFTDRuleSet.main(args2);
		
	}

}
