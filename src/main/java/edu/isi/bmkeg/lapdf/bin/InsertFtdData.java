package edu.isi.bmkeg.lapdf.bin;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import edu.isi.bmkeg.lapdf.controller.LapdfVpdmfEngine;
import edu.isi.bmkeg.lapdf.model.LapdfDocument;
import edu.isi.bmkeg.lapdf.uima.cpe.CommandLineFitPipeline;
import edu.isi.bmkeg.utils.Converters;

public class InsertFtdData
{

	private static Logger logger = Logger.getLogger(InsertFtdData.class);
	
	private static String USAGE = "usage: <input-file/dir> <dbName> <login> <password> [<rule-file>]";

	public static void main(String args[]) throws Exception	{

		if (args.length < 4 || args.length > 5 ) {
			System.err.println(USAGE);
			System.exit(1);
		}
		
		String inputFileOrFolderPath = args[0];
		String dbName = args[1];
		String login = args[2];
		String password = args[3];
		String ruleFileLocation = null;
		
		if (args.length == 5) 
			ruleFileLocation = args[4];
 	
		LapdfVpdmfEngine lapdfEng = null;
		if (ruleFileLocation != null) {
			logger.info("Using rulefile " + ruleFileLocation);
			lapdfEng = new LapdfVpdmfEngine(new File(ruleFileLocation));
		} else {
			lapdfEng = new LapdfVpdmfEngine();
		}
		
		lapdfEng.initializeVpdmfDao(login, password, dbName);
		
		File fOrD = new File( inputFileOrFolderPath );
		if( !fOrD.exists() ) { 
			System.err.print( inputFileOrFolderPath + " does not exist.");
			System.exit(-1);
		}
	
		if( fOrD.isDirectory() ) {
			
			Pattern p = Pattern.compile("\\.pdf$");
			Map<String, File> m = Converters.recursivelyListFiles(
					fOrD, new HashMap<String, File>(), p
					);
			Iterator<File> fIt = m.values().iterator();
			while( fIt.hasNext() ) {
				File f = fIt.next();
				
				LapdfDocument doc = lapdfEng.blockifyPdfFile(f);
				String basicText = lapdfEng.readBasicText(doc);
				lapdfEng.getFtdDao().insertLapdfDocument(doc, f, basicText);
						
			}
			
		} else {
		
			LapdfDocument doc = lapdfEng.blockifyPdfFile(fOrD);
			String basicText = lapdfEng.readBasicText(doc);
			lapdfEng.getFtdDao().insertLapdfDocument(doc, fOrD, basicText);
			
		}
	
	}

}
