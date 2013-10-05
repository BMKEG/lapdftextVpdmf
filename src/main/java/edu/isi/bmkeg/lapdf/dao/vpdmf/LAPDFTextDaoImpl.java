package edu.isi.bmkeg.lapdf.dao.vpdmf;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.common.io.Files;

import edu.isi.bmkeg.ftd.model.FTD;
import edu.isi.bmkeg.ftd.model.FTDRuleSet;
import edu.isi.bmkeg.ftd.model.qo.FTDRuleSet_qo;
import edu.isi.bmkeg.ftd.model.qo.FTD_qo;
import edu.isi.bmkeg.lapdf.controller.LapdfMode;
import edu.isi.bmkeg.lapdf.controller.LapdfVpdmfEngine;
import edu.isi.bmkeg.lapdf.dao.LAPDFTextDao;
import edu.isi.bmkeg.lapdf.model.LapdfDocument;
import edu.isi.bmkeg.utils.Converters;
import edu.isi.bmkeg.vpdmf.controller.queryEngineTools.VPDMfChangeEngineInterface;
import edu.isi.bmkeg.vpdmf.dao.CoreDao;
import edu.isi.bmkeg.vpdmf.model.definitions.VPDMf;
import edu.isi.bmkeg.vpdmf.model.instances.LightViewInstance;
import edu.isi.bmkeg.vpdmf.model.instances.ViewBasedObjectGraph;

@Repository
public class LAPDFTextDaoImpl implements LAPDFTextDao {

	private static Logger logger = Logger.getLogger(LAPDFTextDaoImpl.class);
	
	// ~~~~~~~~~
	// Constants
	// ~~~~~~~~~

	@Autowired
	private CoreDao coreDao;
	
	private LapdfVpdmfEngine lapdfEng;

	// ~~~~~~~~~~~~
	// Constructors
	// ~~~~~~~~~~~~
	public LAPDFTextDaoImpl() {
	}	

	public LAPDFTextDaoImpl(CoreDao coreDao) {
		this.coreDao = coreDao;
	}	

	// ~~~~~~~~~~~~~~~~~~~
	// Getters and Setters
	// ~~~~~~~~~~~~~~~~~~~
	public void setCoreDao(CoreDao dlVpdmf) {
		this.coreDao = dlVpdmf;
	}

	public CoreDao getCoreDao() {
		return coreDao;
	}

	private VPDMfChangeEngineInterface getCe() {
		return coreDao.getCe();
	}

	private Map<String, ViewBasedObjectGraph> generateVbogs() throws Exception {
		return coreDao.generateVbogs();
	}

	private VPDMf getTop() {
		return coreDao.getTop();
	}
	
	@Override
	public void insertLapdfDocument(LapdfDocument doc, File pdf, String text) throws Exception {

		FTD ftd = new FTD();
		
		//ftd.setPdfFile( Converters.fileContentsToBytesArray(pdf) );
		ftd.setChecksum( Converters.checksum(pdf) );
		ftd.setName( pdf.getPath() );
		ftd.setText( text );
	
		doc.packForSerialization();
		ftd.setLapdf( Converters.objectToByteArray( doc ) );
		doc.unpackFromSerialization();

		getCoreDao().insert(ftd, "FTD");
		
	}
	
	public FTD runRuleSetOnFtd(FTD ftd, FTDRuleSet ftdRuleSet) throws Exception {
		
		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		// Update the representation of the ruleset in the database.
		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~		
		FTDRuleSet_qo ftdRs_qo = new FTDRuleSet_qo();
		ftdRs_qo.setFileName(ftdRuleSet.getFileName());
		List<LightViewInstance> l = this.coreDao.list(ftdRs_qo, "FTDRuleSet");
		
		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		// Dump rulefile to disk on server
		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		File tempDir = Files.createTempDir();
		File ruleFile = new File( tempDir.getPath() + "/" 
				+ ftdRuleSet.getFileName());
		String s = ftdRuleSet.getFileName();
		ftdRuleSet.setRsName(s.substring(0,s.length()-4));
		ftdRuleSet.setRsDescription("");
		
		if( s.endsWith(".drl") ) {			
		
			FileUtils.writeStringToFile(ruleFile, ftdRuleSet.getRuleBody());
			
		} else if( ftdRuleSet.getFileName().endsWith("csv") )  {

			FileUtils.writeStringToFile(ruleFile, ftdRuleSet.getExcelRuleFile().toString());
		
		} else if( ftdRuleSet.getFileName().endsWith("xls") )  {

			FileOutputStream output = new FileOutputStream(ruleFile);
			IOUtils.write(ftdRuleSet.getExcelRuleFile(), output);

		}

		if( l.size() == 1 ) {
			
			FTDRuleSet rsInDb = new FTDRuleSet();
			rsInDb = this.coreDao.findById(
					l.get(0).getVpdmfId(), 
					rsInDb, 
					"FTDRuleSet");
			
			rsInDb.setRsName( ftdRuleSet.getRsName() );
			rsInDb.setRsDescription( ftdRuleSet.getRsDescription() );
			rsInDb.setRuleBody( ftdRuleSet.getRuleBody() );
			rsInDb.setExcelRuleFile( ftdRuleSet.getExcelRuleFile() );
			this.coreDao.update(rsInDb, "FTDRuleSet");
			
		} else if(l.size() == 0) {

			long vpdmfId = this.coreDao.insert(ftdRuleSet, "FTDRuleSet");
			ftdRuleSet.setVpdmfId(vpdmfId);
			
		} else {
			throw new Exception("Ambiguous Number of Rule Sets for file " 
					+ ftdRuleSet.getFileName());
		}
		
		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		// Get the original LAPDFtext Document
		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		this.lapdfEng = new LapdfVpdmfEngine(ruleFile);
		
		LapdfDocument document = (LapdfDocument) 
				Converters.byteArrayToObject( ftd.getLapdf() );
		document.unpackFromSerialization();		
		
		this.lapdfEng.classifyDocument(document, ruleFile);
		
		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		// Update the representation of the ruleset in the database.
		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~				
		FTD_qo ftd_qo = new FTD_qo();
		ftd_qo.setName( ftd.getName() );
		ftd_qo.setChecksum( ftd.getChecksum() );
		l = this.coreDao.list(ftd_qo, "FTD");
		
		if( l.size() == 1 ) {

			FTD ftdInDb = new FTD();
			ftdInDb = this.coreDao.findById(
					l.get(0).getVpdmfId(), 
					ftdInDb, 
					"FTD");
			
			ftdInDb.setText( this.lapdfEng.readCompleteText(document) );

			document.packForSerialization();
			ftdInDb.setLapdf( Converters.objectToByteArray(document) );
			ftdInDb.setRuleSet( ftdRuleSet );
			
			this.coreDao.update(ftdInDb, "FTD");

		} else if( l.size() == 0 ){
			
			ftd.setText( this.lapdfEng.readCompleteText(document) );

			document.packForSerialization();
			ftd.setLapdf( Converters.objectToByteArray(document) );
			ftd.setRuleSet( ftdRuleSet );
			this.coreDao.insert(ftd, "FTD");
			
		}
		
		Converters.recursivelyDeleteFiles(tempDir);
		
		return ftd;
		
	}

}
