package org.evansnet.ingredient.persistence.repository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jface.preference.IPreferenceStore;
import org.evansnet.dataconnector.internal.core.DBType;
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
	ResultSet   resultSet;
	Ingredient  ingredient;
	
	public IngredientRepository() {
		connStr = new String();
		repoName = new String("");
		repoVersion = new String("1.0");
		isDefault = false;
		contents = new HashMap<Integer, Ingredient>();
	}
	
	/**
	 * Retrieves the default repository connection string from the default properties
	 * storage.
	 * @return	The connection string for the default ingredient repository.
	 */
	public String fetchDefaultRepo() {
		RepositoryHelper helper = new RepositoryHelper();
		repo = helper.getDefaultRepository();
		isDefault = true;
		setRepoName("Default Ingredient Repository");
		repoVersion = "1.0";
		connStr = repo.getConnectionString();
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
		if (!contents.isEmpty()) {
			return (HashMap<Integer, Ingredient>) contents;
		} else {
			try {
				fetchAll();
			} catch (Exception e) {
				javaLogger.logp(Level.SEVERE, THIS_CLASS_NAME, "getTreeIngredients()" ,
						"An exception was thrown when retrieving ingredients from the repository " +
						e.getMessage());
				e.printStackTrace();
			}
		}
		return null;
	}
	
	/**
	 * Returns the first ingredient in the repository that has the given name. 
	 * @param name 
	 * @return The ingredient object that has the name, or null if it is not found.
	 */
	public Ingredient getIngredient(String name) {
		// Check the map for the ingredient name.
		for (Entry<Integer, Ingredient> ing : contents.entrySet())
			if (ing.getValue().getIngredientName().equals(name)) {
				return ing.getValue();
			}
		return null;
	}
	
	public Ingredient getIngredient(int id) throws SQLException, Exception {
		if (checkExists(id)) {
			
		}
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

	public void setRepo(IDatabase database) {
		repo = database;
	}
	
	/** 
	 * Fetches all the ingredients in the repository.
	 * @return A Map object containing the IDs and names of all of the ingredients in the repository.
	 */
	public Map<Integer, Ingredient> fetchAll() throws Exception {
		Connection conn = repo.getConnection();
		String s = "SELECT * FROM " + repo.getSchema() + "." + "INGREDIENT";
		Statement stmt = null;
		stmt = conn.createStatement();
		resultSet = stmt.executeQuery(s);

		
		Ingredient i = new Ingredient();
		while (resultSet.next()) {
			i.setID(resultSet.getInt("ID"));
			i.setIngredientName(resultSet.getString("ING_NAME"));
			i.setIngredientDescription(resultSet.getString("ING_DESC"));
			i.setStrUom(resultSet.getString("UNIT_OF_MEASURE"));
			i.setPkgUom(resultSet.getString("PKG_UOM"));
			i.setUnitPrice(resultSet.getBigDecimal("UNIT_PRICE"));
			i.setPkgPrice(resultSet.getBigDecimal("PKG_PRICE"));
			i.setRecipe(resultSet.getBoolean("IS_RECIPE"));
		contents.put(i.getID(), i);
		}
		conn.close();
		return contents;
	}

	/**
	 * Get an ingredient from the ingredient map that has the ingredient id provided. Since 
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
	 * Gets the ingredients from the ingredient map that have the name provided. Since the name 
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
		if (i.isRecipe()) {
			if (repo.getDBMS().equals(DBType.MS_SQLSrv)) {
				insert.append("1)"); 
			} else if (repo.getDBMS().equals(DBType.MySQL)) {
				insert.append("true");
			}
		} else {
			if (repo.getDBMS().equals(DBType.MS_SQLSrv)) {
				insert.append("0)"); 
			} else if (repo.getDBMS().equals(DBType.MySQL)) {
				insert.append("false");
			}			
		}
		Connection con = repo.getConnection();
		Statement stmt = con.createStatement();
		try {
			rowsInserted = stmt.executeUpdate(insert.toString());
			if (rowsInserted > 0) {
				//The insert was successful so add the ingredient to the map.
				contents.put(i.getID(), i);
			}
		} catch (Exception e) {
			javaLogger.log(Level.SEVERE, THIS_CLASS_NAME, 
					"Faiied to insert ingredient record. Rows inserted: : " + rowsInserted );
			e.printStackTrace();
		} finally {
			con.close();
		}
	}
	
	
	/**
	 * Updates the repository object with the contents of the ingredient object provide.
	 * @param i The ingredient object that contains the updated data.
	 */
	public void doUpdate(Ingredient i) throws SQLException, Exception {
		StringBuilder update = new StringBuilder();
		// Update dbo.ingredient set (id = ingredient.getID())... where ID = i.getID()...
		update.append("UPDATE " + repo.getSchema() + ".INGREDIENT SET(");
		update.append("ID=" + i.getID() + ", ");
		update.append("ING_NAME=" + i.getIngredientName() + ", ");
		update.append("ING_DESC=" + i.getIngredientDescription() + ", ");
		update.append("UNIT_OF_MEASURE=" + i.getStrUom() + ", ");
		update.append("PKG_UOM=" + i.getPkgUom() + ", ");
		update.append("UNIT_PRICE=" + i.getUnitPrice() + ", ");
		update.append("PKG_PRICE=" + i.getPkgPrice());
		if(i.isRecipe()) {
			if (repo.getDBMS().equals(DBType.MS_SQLSrv)) {
				update.append("IS_RECIPE=1");				
			} else if (repo.getDBMS().equals(DBType.MySQL)) {
				update.append("IS_RECIPE=true");
			}
		} else {
			if (repo.getDBMS().equals(DBType.MS_SQLSrv)) {
				update.append("IS_RECIPE=0");				
			} else if (repo.getDBMS().equals(DBType.MySQL)) {
				update.append("IS_RECIPE=false");
			}
		}
		update.append(")");
		
		Connection conn = repo.getConnection();
		Statement s = conn.createStatement();
		int rowsUpd = s.executeUpdate(update.toString());
		if(rowsUpd < 1) {
			throw new SQLException("An error occurred while updating ingredient with ID " + i.getID());
		}
		conn.close();
	}
	
	/**
	 * Removes the ingredient with the ID provided from the repository
	 * @param i The integer ID value of the ingredient
	 */
	public void doDelete(int i) throws SQLException, Exception {
		StringBuilder delete = new StringBuilder("DELETE FROM " + repo.getSchema() + "." +
				"INGREDIENT WHERE ID = " + i);
		Connection conn = repo.getConnection();
		Statement s = conn.createStatement();
		int rowsDel = s.executeUpdate(delete.toString());
		if (rowsDel < 1) {
			throw new SQLException("An error occurred while deleting ingredient with ID " + i);
		}
		conn.close();
	}
	
	/**
	 * Checks the repository for the existence of an ingredient given that ingredients
	 * integer ID number.
	 * @param i The ID number of the ingredient to check for
	 */
	public boolean checkExists(int i) throws SQLException, Exception {
		Connection conn = repo.getConnection();
		String s = "SELECT * FROM " + repo.getSchema() + "." + "INGREDIENT WHERE ID=" + i;
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(s);
			if (rs.next()) {
				conn.close();
				return true;
			}		
		conn.close();
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
	 * @throws Exception 
	 */
	public boolean isEmpty() throws Exception {
		if (contents.isEmpty()) {			//Check the map first if empty than run the query.
			if (fetchAll().isEmpty()) {
				return true;
			}
		} 
		return false;
	}
}
