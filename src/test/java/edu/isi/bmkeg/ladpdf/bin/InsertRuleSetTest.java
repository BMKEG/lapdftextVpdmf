package edu.isi.bmkeg.ladpdf.bin;

import java.io.File;
import java.net.URL;

import org.junit.Test;

import edu.isi.bmkeg.ftd.model.FTDRuleSet;
import edu.isi.bmkeg.lapdf.bin.AddFTD;
import edu.isi.bmkeg.lapdf.controller.LapdfVpdmfEngine;
import edu.isi.bmkeg.utils.springContext.BmkegProperties;
import edu.isi.bmkeg.vpdmf.test.VPDMfTestCase;

public class InsertRuleSetTest extends VPDMfTestCase
{

	BmkegProperties prop;
	String login, password, dbUrl;

	File inputFile, outputFile, ruleFile;
	File f1, f2, f3;
	
	protected void setUp() throws Exception
	{ 
		super.setUp("edu/isi/bmkeg/lapdf/ftd_VPDMf.zip", true);
				
		this.prop = new BmkegProperties(true);
		
		login = prop.getDbUser();
		password = prop.getDbPassword();
		dbUrl = prop.getDbUrl();

		int l = dbUrl.lastIndexOf("/");
		if (l != -1)
			dbUrl = dbUrl.substring(l + 1, dbUrl.length());

		URL u = this.getClass().getClassLoader().getResource("sampleData/plos/8_8");
		inputFile = new File( u.getPath() );
				
		u = this.getClass().getClassLoader().getResource(
				"rules/general.xls"
				);
		ruleFile = new File(u.getPath());
		
		u = this.getClass().getClassLoader().getResource("sampleData/plos/8_8/pbio.1000441.pdf");
		f1 = new File(u.getPath());	
		
	}

	protected void tearDown() throws Exception
	{
		super.tearDown();
	}

	@Test
	public void testInsertFtdData() throws Exception
	{
		
		LapdfVpdmfEngine lapdfEng = new LapdfVpdmfEngine();
		lapdfEng.initializeVpdmfDao(login, password, dbUrl);
		
		FTDRuleSet rs = lapdfEng.buildDrlRuleSet("General","Insert Notes Here", ruleFile);
		
		lapdfEng.getFtdDao().getCoreDao().insert(rs, "FTDRuleSet");
		
	}
	
}
