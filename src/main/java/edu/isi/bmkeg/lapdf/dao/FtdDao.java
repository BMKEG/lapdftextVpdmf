package edu.isi.bmkeg.lapdf.dao;

import java.io.File;
import java.io.IOException;

import edu.isi.bmkeg.lapdf.model.LapdfDocument;
import edu.isi.bmkeg.vpdmf.dao.VpdmfDao;


/**
 * Defines the interface to a Data Access Object that manage the data persistent
 * storage. A Spring bean implementing this interface can be injected in other
 * Spring beans.
 */
public interface FtdDao extends VpdmfDao {

	// ~~~~~~~~~~~~~~~
	// Count functions
	// ~~~~~~~~~~~~~~~

	// ~~~~~~~~~~~~~~~~~~~
	// Insert Functions
	// ~~~~~~~~~~~~~~~~~~~

	void insertLapdfDocument(LapdfDocument doc, File pdf, String text)
			throws Exception;


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