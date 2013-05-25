package edu.isi.bmkeg.lapdf.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jpedal.exception.PdfException;

import edu.isi.bmkeg.lapdf.classification.ruleBased.RuleBasedChunkClassifier;
import edu.isi.bmkeg.lapdf.dao.FtdDao;
import edu.isi.bmkeg.lapdf.dao.vpdmf.VpdmfFtdDao;
import edu.isi.bmkeg.lapdf.extraction.exceptions.AccessException;
import edu.isi.bmkeg.lapdf.extraction.exceptions.ClassificationException;
import edu.isi.bmkeg.lapdf.extraction.exceptions.EncryptionException;
import edu.isi.bmkeg.lapdf.model.ChunkBlock;
import edu.isi.bmkeg.lapdf.model.LapdfDocument;
import edu.isi.bmkeg.lapdf.model.PageBlock;
import edu.isi.bmkeg.lapdf.model.RTree.RTModelFactory;
import edu.isi.bmkeg.lapdf.model.ordering.SpatialOrdering;
import edu.isi.bmkeg.lapdf.parser.RuleBasedParser;
import edu.isi.bmkeg.lapdf.text.SectionsTextWriter;
import edu.isi.bmkeg.lapdf.text.SpatialLayoutFeaturesReportGenerator;
import edu.isi.bmkeg.lapdf.text.SpatiallyOrderedChunkTextWriter;
import edu.isi.bmkeg.lapdf.text.SpatiallyOrderedChunkTypeFilteredTextWriter;
import edu.isi.bmkeg.lapdf.utils.JPedalPDFRenderer;
import edu.isi.bmkeg.lapdf.utils.PageImageOutlineRenderer;
import edu.isi.bmkeg.lapdf.xml.OpenAccessXMLWriter;
import edu.isi.bmkeg.lapdf.xml.SpatialXMLWriter;
import edu.isi.bmkeg.vpdmf.dao.CoreDao;
import edu.isi.bmkeg.vpdmf.dao.CoreDaoImpl;
import edu.isi.bmkeg.vpdmf.dao.VpdmfEngine;

/**
 * Basic Java API to high-level LAPDFText functionality, including:
 *
 * 1) Gathering layout statistics for the PDF file
 * 2) Running Block-based spatial chunker on PDF.
 * 3) Classifying texts of blocks in the file to categories based on a rule file.
 * 4) Outputting text or XML to file
 * 5) Rendering pages images of text layout or the original PDF file as PNG files
 * 6) Serializing LAPDFText object to a VPDMf database record.
 * 
 * @author burns
 *
 */
public class LapdfVpdmfEngine extends LapdfEngine implements VpdmfEngine  {

	private static Logger logger = Logger.getLogger(LapdfVpdmfEngine.class);

	private FtdDao ftdDao;
		

	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	public LapdfVpdmfEngine() 
			throws Exception {

		super();

	}

	public LapdfVpdmfEngine(File ruleFile) 
			throws Exception {

		super(ruleFile);
		
	}
	
	public LapdfVpdmfEngine(boolean imgFlag) 
			throws Exception  {

		super(imgFlag);
		
	}
	
	public LapdfVpdmfEngine(File ruleFile, boolean imgFlag) throws Exception {

		super(ruleFile, imgFlag);
		
	}	
	
	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// VPDMf functions
	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	/**
	 * Builds dao objects to input and output data to a VPDMf store.
	 */
	public void initializeVpdmfDao(String login, String password, String dbName) throws Exception {

		CoreDao coreDao = new CoreDaoImpl();
		coreDao.init(login, password, dbName);
		
		this.setFtdDao(new VpdmfFtdDao(coreDao));
		this.getFtdDao().setCoreDao(coreDao);

	}

	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	public FtdDao getFtdDao() {
		return ftdDao;
	}

	public void setFtdDao(FtdDao ftdDao) {
		this.ftdDao = ftdDao;
	}

}
