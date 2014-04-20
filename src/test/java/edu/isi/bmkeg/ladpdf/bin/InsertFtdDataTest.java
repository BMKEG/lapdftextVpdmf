package edu.isi.bmkeg.ladpdf.bin;

import java.io.File;
import java.net.URL;

import edu.isi.bmkeg.ftd.model.FTDRuleSet;
import edu.isi.bmkeg.lapdf.bin.AddFTD;
import edu.isi.bmkeg.lapdf.controller.LapdfVpdmfEngine;
import edu.isi.bmkeg.utils.springContext.BmkegProperties;
import edu.isi.bmkeg.vpdmf.test.VPDMfTestCase;

public class InsertFtdDataTest extends VPDMfTestCase
{

	BmkegProperties prop;
	String login, password, dbUrl, workingDirectory;

	File inputFile, outputFile, ruleFile;
	File f1, f2, f3;
	
	protected void setUp() throws Exception
	{ 
		super.setUp("edu/isi/bmkeg/lapdf/ftd-mysql.zip", true);
				
		this.prop = new BmkegProperties(true);
		
		login = prop.getDbUser();
		password = prop.getDbPassword();
		dbUrl = prop.getDbUrl();
		workingDirectory = prop.getWorkingDirectory();
		
		int l = dbUrl.lastIndexOf("/");
		if (l != -1)
			dbUrl = dbUrl.substring(l + 1, dbUrl.length());

		URL u = this.getClass().getClassLoader().getResource("sampleData/plos/8_8");
		inputFile = new File( u.getPath() );
				
		u = this.getClass().getClassLoader().getResource(
				"rules/general.drl"
				);
		ruleFile = new File(u.getPath());
		
		u = this.getClass().getClassLoader().getResource("sampleData/plos/8_8/pbio.1000441.pdf");
		f1 = new File(u.getPath());	
				
	}

	protected void tearDown() throws Exception
	{
		super.tearDown();
	}

//	FIXME: Fails with java.lang.Exception: The appropriate rule file (general.drlhas NOT been uploaded to the database, please run AddFTDRuleSet
//	public void testInsertFtdData() throws Exception
//	{
//		System.out.println("In side ignored test");
//		String[] args = {
//				inputFile.getPath(), this.dbUrl, this.login, this.password
//			};
//
//		AddFTD.main(args);
//
//	}

	public void testInsertFtdDataWithRules() throws Exception
	{
		LapdfVpdmfEngine lapdfEng = new LapdfVpdmfEngine();
		lapdfEng.initializeVpdmfDao(login, password, dbUrl, workingDirectory);
		
		FTDRuleSet rs = lapdfEng.buildDrlRuleSet("General","Insert Notes Here", ruleFile);
		
		lapdfEng.getFtdDao().getCoreDao().insert(rs, "FTDRuleSet");	

		String[] args = {
				inputFile.getPath(), this.dbUrl, this.login, this.password,
				
			};

		AddFTD.main(args);

	}

}
