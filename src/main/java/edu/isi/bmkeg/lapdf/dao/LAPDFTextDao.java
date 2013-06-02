package edu.isi.bmkeg.lapdf.dao;

import java.io.File;
import java.io.IOException;

import edu.isi.bmkeg.ftd.model.FTD;
import edu.isi.bmkeg.ftd.model.FTDRuleSet;
import edu.isi.bmkeg.lapdf.model.LapdfDocument;
import edu.isi.bmkeg.vpdmf.dao.VpdmfDao;


/**
 * Defines the interface to a Data Access Object that manage the data persistent
 * storage. A Spring bean implementing this interface can be injected in other
 * Spring beans.
 */
public interface LAPDFTextDao extends VpdmfDao {

	void insertLapdfDocument(LapdfDocument doc, File pdf, String text)
			throws Exception;

	FTD runRuleSetOnFtd(FTD ftd, FTDRuleSet ftdRuleSet) 
			throws Exception;	

}