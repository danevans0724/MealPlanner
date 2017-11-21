package org.evansnet.ingredient.persistence.repository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jface.preference.IPreferenceStore;
import org.evansnet.dataconnector.internal.core.IDatabase;
import org.evansnet.ingredient.app.Activator;
import org.evansnet.ingredient.model.Ingredient;
import org.evansnet.ingredient.persistence.preferences.PreferenceConstants;

/**
 * Implements the usage of the ingredient repository. 
 * The repository is a database table that stores the ingredients 
 * created by the user. 
 * 
 * @author Dan Evans
 *
 */
public class IngredientRepository {	
	
	public static String THIS_CLASS_NAME = IngredientRepository.class.getName();
	public static Logger javaLogger = Logger.getLogger(THIS_CLASS_NAME);
	
	String 		connStr;		//The JDBC connection string for the repository.
	IDatabase 	repo;			//A connection to the ingredient repository table.
	String 		repoName;
	String 		repoVersion;
	boolean 	isDefault;
	Map<Integer, Ingredient> contents;
	
	public IngredientRepository() {
		connStr = new String();
		repoName = new String("");
		isDefault = false;
		contents = new HashMap<Integer, Ingredient>();
	}
	
	/**
	 * Retrieves the default repository connection string from the default properties
	 * storage.
	 * @return	The connection string for the default ingredient repository.
	 */
	private String fetchDefaultRepo() {
		RepositoryHelper helper = new RepositoryHelper();
		repo = helper.getDefaultRepository();
		connStr = repo.getConnectionString();
		//TODO: If we retrieve the connection string from storage we need to construct the db object.
		return connStr;
	}
	
	/**
	 * Retrieve a list of the ingredients stored in the repository table. This
	 * is uses, among other things, to populate the tree in the ingredient explorer
	 * view.
	 * 
	 * @return A list of the ingredients contained in the repository tree.
	 */
	public HashMap<Integer, Ingredient> getTreeIngredients() {
		//TODO: provide a list of ingredients from the repository table.
		return null;
	}
	
	public Ingredient getIngredient(String name) {
		//TODO: Implement the Get an ingredient by name method
		return null;
	}
	
	public Ingredient getIngredient(int id) {
		//TODO: Implement the get ingredient by ID method
		return null;
	}
	
	public void store(Ingredient i) {
		//TODO: Implement the store ingredient method
	}
	
	protected String fetchVersion() {
		//Get the repository version from the database
		return null;
	}
	
	public String getRepoVersion() {
		return repoVersion;
	}
	
	public void setConnectStr(String c) {
		connStr = c;
	}
	
	public String getConnectStr() {
		return connStr;
	}
	
	public void setRepoName(String name) {
		repoName = name;
	}
	
	public String getRepoName() {
		return repoName;
	}
	
	public void setDefault() {
		isDefault = true;
		persistDefault();	// Store the default repo connection string in the plug-in properties.
	}
	
	public boolean isDefault() {
		return isDefault;
	}

	public void setRepoConnection(IDatabase database) {
		// TODO Auto-generated method stub
	}
	
	/** 
	 * Fetches all the ingredients in the repository.
	 * @return A Map object containing the IDs and names of all of the ingredients in the repository.
	 */
	public Map<Integer, Ingredient> fetchAll() throws Exception {
		ResultSet rs = doSelectAll();
		
		Ingredient i = new Ingredient();
		while (rs.next()) {
			i.setID(rs.getInt("ID"));
			i.setIngredientName(rs.getString("ING_NAME"));
			i.setIngredientDescription(rs.getString("ING_DESC"));
			i.setStrUom(rs.getString("UNIT_OF_MEASURE"));
			i.setPkgUom(rs.getString("PKG_UOM"));
			i.setUnitPrice(rs.getBigDecimal("UNIT_PRICE"));
			i.setPkgPrice(rs.getBigDecimal("PKG_PRICE"));
			i.setRecipe(rs.getBoolean("IS_RECIPE"));
		contents.put(i.getID(), i);
		}
		return contents;
	}

	/**
	 * Get an ingredient from the repository that has the ingredient id provided. Since 
	 * the ingredient ID is the primary key of the ingredient repository table, there is 
	 * guaranteed to be only one record with that ID. 
	 * @param id The integer identifier of the ingredient.
	 * @return The ingredient with the matching ID or null if the id is not found.
	 * @throws Exception
	 */
	public Ingredient fetch(int id) throws Exception {
		if (contents.isEmpty()) {
			fetchAll();
		}
		if (contents.containsKey(id)) {
			return contents.get(id);
		} 
		return null;
	}
	
	/**
	 * Gets the ingredients from the repository that have the name provided. Since the name 
	 * field does not have a unique constraint, it is possible to get more than one ingredient. 
	 * Therefore, the method returns a HashMap object that contains all the ingredients in the 
	 * repository table that have the name requested. 
	 * 
	 * @param n A string value representing the ingredient name to search for.
	 * @return A HashMap that contains the ingredients that have the name provided.
	 */
	public List<Ingredient> fetchName(String n) {
		List<Ingredient> result = new ArrayList<Ingredient>();
		for (Ingredient i : contents.values()) {
			if (i.getIngredientName().equals(n)) {
				result.add(i);
			}
		}
		return result;
	}
	
	public ResultSet doSelectAll() throws Exception {
		Connection conn = repo.getConnection();
		String s = "SELECT * FROM " + repo.getSchema() + "." + "INGREDIENT";
		Statement stmt = null;
		stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(s);
		return rs;
	}
	
	public ResultSet doSelect(int i) throws Exception {
		Connection conn = repo.getConnection();
		String s = "SELECT * FROM " + repo.getSchema() + "." + "INGREDIENT WHERE ID=" + i;
		Statement stmt = null;
		stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(s);
		return rs;		
	}

	/**
	 * Inserts an ingredient object into the repository table. 
	 * @param i The ingredient object that is to be added to the repository.
	 * @throws Exception 
	 */
	public void doInsertNew(Ingredient i) throws Exception {
		int rowsInserted = 0;
		StringBuilder insert = new StringBuilder("INSERT INTO ");
		insert.append(repo.getSchema() + ".");
		insert.append(repo.getDatabaseName() + " VALUES(");
		insert.append(new String(i.getID() + ", "));
		insert.append("'" + i.getIngredientName() + "', ");
		insert.append("'" + i.getIngredientDescription() + "', ");
		insert.append("'" + i.getStrUom() + "', " );
		insert.append("'" + i.getPkgUom() + "', ");
		insert.append("'" + i.getUnitPrice() + "', ");
		insert.append("'" + i.getPkgPrice() + "', ");
		insert.append("0)"); 	//TODO: Add code to check for DBMS type and boolean value.
		
		Connection con = repo.getConnection();
		Statement stmt = con.createStatement();
		try {
			rowsInserted = stmt.executeUpdate(insert.toString());
		} catch (Exception e) {
			javaLogger.log(Level.SEVERE, THIS_CLASS_NAME, 
					"Faiied to insert ingredient record. Rows inserted: : " + rowsInserted );
			e.printStackTrace();
		}
		//TODO: Complete the query statement and process.
	}
	
	
	/**
	 * Updates the repository object with the contents of the ingredient object provide.
	 * @param i The ingredient object that contains the updated data.
	 */
	public void doUpdate(Ingredient i) {
		StringBuilder update = new StringBuilder();
		// Update dbo.ingredient set (id = ingredient.getID())... where ID = i.getID()...
		
	}
	
	/**
	 * Removes the ingredient with the ID provided from the repository
	 * @param i The integer ID value of the ingredient
	 */
	public void doDelete(int i) {
		StringBuilder delete = new StringBuilder("DELETE FROM ");
		// Delete from dbo.ingredient where ID = i
	}
	
	/**
	 * Checks the repository for the existence of an ingredient given that ingredients
	 * integer ID number.
	 * @paream i The ID number of the ingredient to check for
	 */
	public boolean checkExists(int i) {
		return false;
	}

	/**
	 * Persists the connection string to the plug-in preference store.
	 */
	private void persistDefault() {
		try {
			IPreferenceStore store = Activator.getDefault().getPreferenceStore();
			store.setValue(PreferenceConstants.PRE_REPO_CONN_STR, connStr);
		} catch (Exception e) {
			String msg = "An error occured while storing the default repository " +
					"connection. The error message is: " + e.getMessage();
			javaLogger.log(Level.SEVERE, THIS_CLASS_NAME, msg);
			e.printStackTrace();
		}
	}
	
	/**
	 * Checks the repository for records.
	 * @return Returns true if there are no ingredient records in the repository table. 
	 */
	public boolean isEmpty() {
		return true;
	}
}
