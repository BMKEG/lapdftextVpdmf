package edu.isi.bmkeg.lapdf.bin;

import java.io.File;
import java.io.FileWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import edu.isi.bmkeg.ftd.model.FTD;
import edu.isi.bmkeg.ftd.model.FTDRuleSet;
import edu.isi.bmkeg.ftd.model.qo.FTDRuleSet_qo;
import edu.isi.bmkeg.lapdf.controller.LapdfVpdmfEngine;
import edu.isi.bmkeg.lapdf.model.LapdfDocument;
import edu.isi.bmkeg.lapdf.pmcXml.PmcXmlArticle;
import edu.isi.bmkeg.lapdf.xml.model.LapdftextXMLDocument;
import edu.isi.bmkeg.utils.Converters;
import edu.isi.bmkeg.utils.xml.XmlBindingTools;
import edu.isi.bmkeg.vpdmf.model.instances.LightViewInstance;

public class AddFTD
{

	private static Logger logger = Logger.getLogger(AddFTD.class);
	
	private static String USAGE = "usage: <input-file/dir> <dbName> <login> <password> <workingDirectory> [<rule-file>]";

	public static void main(String args[]) throws Exception	{

		if (args.length < 4 || args.length > 5 ) {
			System.err.println(USAGE);
			System.exit(1);
		}
		
		String inputFileOrFolderPath = args[0];
		String dbName = args[1];
		String login = args[2];
		String password = args[3];
		String workingDirectory = args[4];
		String ruleFileLocation = null;
		
		if (args.length == 6) 
			ruleFileLocation = args[5];
 	
		LapdfVpdmfEngine lapdfEng = null;
		if (ruleFileLocation != null) {
			logger.info("Using rulefile " + ruleFileLocation);
			lapdfEng = new LapdfVpdmfEngine(new File(ruleFileLocation));
		} else {
			lapdfEng = new LapdfVpdmfEngine();
		}
		
		lapdfEng.initializeVpdmfDao(login, password, dbName, workingDirectory);
		
		File fOrD = new File( inputFileOrFolderPath );
		if( !fOrD.exists() ) { 
			System.err.print( inputFileOrFolderPath + " does not exist.");
			System.exit(-1);
		}
		
		File rf = lapdfEng.getRuleFile();			
		FTDRuleSet_qo qrs = new FTDRuleSet_qo();
		qrs.setFileName(rf.getName());
		List<LightViewInstance> l = lapdfEng.getFtdDao().getCoreDao().list(qrs, "FTDRuleSet");
		if( l.size() != 1 ) {
			throw new Exception("The appropriate rule file (" + rf.getName() + 
					"has NOT been uploaded to the database, please run AddFTDRuleSet");
		}
		
		Long vpdmfId = l.get(0).getVpdmfId();
		FTDRuleSet rs = lapdfEng.getFtdDao().getCoreDao().findById(vpdmfId, new FTDRuleSet(), "FTDRuleSet");
	
		if( fOrD.isDirectory() ) {
			
			Pattern p = Pattern.compile("\\.pdf$");
			Map<String, File> m = Converters.recursivelyListFiles(
					fOrD, new HashMap<String, File>(), p
					);
			Iterator<File> fIt = m.values().iterator();
			while( fIt.hasNext() ) {
				File f = fIt.next();
				
				String fName = f.getName();
				String f2Name = fName.replaceAll("\\s+", "_");
				if( !fName.equals(f2Name) ) {
					File d = f.getParentFile();
					File f2 = new File(d.getPath() + "/" + f2Name);
					Converters.copyFile(f,  f2);
					f.delete();
					f = f2;
				}
				
				FTD ftd = new FTD();
			
				LapdfDocument doc = lapdfEng.blockifyFile(f);
				lapdfEng.classifyDocument(doc, lapdfEng.getRuleFile());
				
				ftd.setChecksum( Converters.checksum(f) );
				
				ftd.setName( f.getName() );
				ftd.setRuleSet(rs);

				PmcXmlArticle pmcXml = doc.convertToPmcXmlFormat();
				String pmcName = fName.substring(0,fName.length()-4) + "_pmc.xml";
				File pmcFile = new File( workingDirectory + "/" + pmcName );
				FileWriter writer = new FileWriter(pmcFile);
				XmlBindingTools.generateXML(pmcXml, writer);
				ftd.setPmcXmlFile( pmcName );
	
				LapdftextXMLDocument xml = doc.convertToLapdftextXmlFormat();
				String lapdfName = fName.substring(0,fName.length()-4) + "_lapdf.xml";
				File lapdfFile = new File( workingDirectory + "/" + lapdfName );
				writer = new FileWriter(lapdfFile);
				XmlBindingTools.generateXML(xml, writer);
				ftd.setXmlFile( lapdfName );
				lapdfEng.getFtdDao().getCoreDao().insert(ftd, "FTD");
												
			}
			
		} else {
					
			LapdfDocument doc = lapdfEng.blockifyFile(fOrD);
			lapdfEng.classifyDocument(doc, lapdfEng.getRuleFile());
			String text = lapdfEng.readCompleteText(doc);
			
			FTD ftd = new FTD();

			ftd.setChecksum( Converters.checksum(fOrD) );
			ftd.setName( fOrD.getName() );
			ftd.setRuleSet(rs);
					
			lapdfEng.addSwfToFtd(fOrD, ftd);
			
			PmcXmlArticle pmcXml = doc.convertToPmcXmlFormat();
			String pmcName = fOrD.getName().substring(0,fOrD.getName().length()-4) + "_pmc.xml";
			File pmcFile = new File( workingDirectory + "/" + pmcName );
			FileWriter writer = new FileWriter(pmcFile);
			XmlBindingTools.generateXML(pmcXml, writer);
			ftd.setPmcXmlFile( pmcName );

			LapdftextXMLDocument xml = doc.convertToLapdftextXmlFormat();
			String lapdfName = fOrD.getName().substring(0,fOrD.getName().length()-4) + "_lapdf.xml";
			File lapdfFile = new File( workingDirectory + "/" + lapdfName );
			writer = new FileWriter(lapdfFile);
			XmlBindingTools.generateXML(xml, writer);
			
			lapdfEng.getFtdDao().getCoreDao().insert(ftd, "FTD");
			
		}
	
	}

}
