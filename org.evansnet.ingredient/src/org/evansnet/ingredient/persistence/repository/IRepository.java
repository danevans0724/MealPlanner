package org.evansnet.ingredient.persistence.repository;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.evansnet.dataconnector.internal.core.IDatabase;

public interface IRepository {

	/**
	 * Retrieves the default repository connection string from the default properties
	 * storage.
	 * @return	The connection string for the default repository.
	 */
	String fetchDefaultRepo() throws Exception;

	String getRepoVersion();

	void setConnectStr(String c);

	String getConnectStr();

	void setRepoName(String name);

	String getRepoName();

	void setDefault();

	boolean isDefault();

	void setRepo(IDatabase database);

	/** 
	 * Fetches all the ingredients in the repository.
	 * @return A Map object containing the IDs and names of all of the ingredients in the repository.
	 */
	Map<Integer, Object> fetchAll() throws Exception;

	/**
	 * Get an Object from the Object map that has the Object id provided. Since 
	 * the Object ID is the primary key of the Object repository table, there is 
	 * guaranteed to be only one record with that ID. 
	 * @param id The integer identifier of the Object.
	 * @return The Object with the matching ID or null if the id is not found.
	 * @throws Exception
	 */
	Object fetchById(int id) throws Exception;

	/**
	 * Gets the ingredients from the Object map that have the name provided. Since the name 
	 * field does not have a unique constraint, it is possible to get more than one Object. 
	 * Therefore, the method returns a HashMap object that contains all the ingredients in the 
	 * repository table that have the name requested. 
	 * 
	 * @param n A string value representing the Object name to search for.
	 * @return An ArrayList that contains the ingredients that have the name provided.
	 */
	List<Object> fetchByName(String n);

	/**
	 * Inserts an Object object into the repository table. 
	 * @param i The Object object that is to be added to the repository.
	 * @throws Exception 
	 */
	int doInsertNew(Object i) throws Exception;

	/**
	 * Updates the repository object with the contents of the Object object provide.
	 * @param i The Object object that contains the updated data.
	 */
	int doUpdate(Object i) throws SQLException, Exception;

	/**
	 * Removes the object with the ID provided from the repository
	 * @param i The integer ID value of the object in the repository table.
	 */
	int doDelete(int i) throws SQLException, Exception;

	/**
	 * Checks the repository for the existence of an Object given that object's ID.
	 * integer ID number.
	 * @param i The ID number of the Object to check for
	 */
	boolean checkExists(int i) throws SQLException, Exception;

	/**
	 * Checks the repository for records.
	 * @return Returns true if there are no Object records in the repository table. 
	 * @throws Exception 
	 */
	boolean isEmpty() throws Exception;
	
	public void removeMappedItem(int i);

}