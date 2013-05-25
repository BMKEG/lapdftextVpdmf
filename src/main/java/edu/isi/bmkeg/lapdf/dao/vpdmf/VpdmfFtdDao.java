package edu.isi.bmkeg.lapdf.dao.vpdmf;

import java.io.File;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import edu.isi.bmkeg.ftd.model.FTD;
import edu.isi.bmkeg.lapdf.dao.FtdDao;
import edu.isi.bmkeg.lapdf.model.LapdfDocument;
import edu.isi.bmkeg.lapdf.model.RTree.RTPageBlock;
import edu.isi.bmkeg.utils.Converters;
import edu.isi.bmkeg.vpdmf.controller.queryEngineTools.VPDMfChangeEngineInterface;
import edu.isi.bmkeg.vpdmf.dao.CoreDao;
import edu.isi.bmkeg.vpdmf.model.definitions.VPDMf;
import edu.isi.bmkeg.vpdmf.model.instances.ViewBasedObjectGraph;

@Repository
public class VpdmfFtdDao implements FtdDao {

	private static Logger logger = Logger.getLogger(VpdmfFtdDao.class);
	
	// ~~~~~~~~~
	// Constants
	// ~~~~~~~~~

	@Autowired
	private CoreDao coreDao;

	// ~~~~~~~~~~~~
	// Constructors
	// ~~~~~~~~~~~~
	public VpdmfFtdDao() {
	}	

	public VpdmfFtdDao(CoreDao coreDao) {
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
	
	// ~~~~~~~~~~~~~~~
	// Count functions
	// ~~~~~~~~~~~~~~~
	
	// ~~~~~~~~~~~~~~~~~~~
	// Insert Functions
	// ~~~~~~~~~~~~~~~~~~~
	
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


		getCoreDao().insertVBOG(ftd, "FTD");
		
	}
	
	// ~~~~~~~~~~~~~~~~~~~
	// Update Functions
	// ~~~~~~~~~~~~~~~~~~~

	// ~~~~~~~~~~~~~~~~~~~
	// Delete Functions
	// ~~~~~~~~~~~~~~~~~~~

	// ~~~~~~~~~~~~~~~~~~~~
	// Find by id Functions
	// ~~~~~~~~~~~~~~~~~~~~

	// ~~~~~~~~~~~~~~~~~~~~
	// Retrieve functions
	// ~~~~~~~~~~~~~~~~~~~~

	// ~~~~~~~~~~~~~~
	// List functions
	// ~~~~~~~~~~~~~~
	
	// ~~~~~~~~~~~~~~~~~~~~
	// Add x to y functions
	// ~~~~~~~~~~~~~~~~~~~~


}
