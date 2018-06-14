package org.evansnet.ingredient.persistence;

import java.sql.Connection;
import java.sql.SQLException;

import org.evansnet.dataconnector.internal.core.IDatabase;
import org.evansnet.ingredient.persistence.repository.IRepository;

/**
 * Base for the ingredient persistence mechanism.
 * Provides connectivity to a database containing an repository tables. 
 *  
 * @author pmidce0
 */
public interface IPersistenceProvider {


	/* Algorithm
	 * ------------------
	 * 1. get a connection to the repository database
	 * 2. Set the persistence type: Fetch, save, update, delete.
	 * 3. Set up a basic query for the type.
	 * 4. Allow the sub-class of the persistence type to execute the query.
	 * 
	 */
IDatabase getDb();

	Connection getConn();

	int doSave(Object i) throws SQLException;

	void doUpdate(Object i) throws SQLException, Exception;

	int doDelete(Object i) throws SQLException, Exception;

	void closeConnection() throws SQLException;

	void setRepsitory(IRepository repo);

	IRepository getRepository() throws Exception;

}