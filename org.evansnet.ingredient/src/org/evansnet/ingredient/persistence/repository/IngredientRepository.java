package org.evansnet.ingredient.persistence.repository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.evansnet.dataconnector.internal.core.IDatabase;
import org.evansnet.ingredient.model.Ingredient;

/**
 * Implements the usage of the ingredient repository. 
 * The repository is a database table that stores the ingredients 
 * created by the user. 
 * 
 * @author Dan Evans
 *
 */
public class IngredientRepository {	
	String 		connStr;				//The JDBC connection string for the repository.
	IDatabase 	repo;			//A connection to the ingredient repository table.
	String 		repoName;
	String 		repoVersion;
	boolean 	isDefault;
	
	public IngredientRepository() {
		connStr = fetchDefaultRepo();
		repoName = new String("");
		isDefault = false;
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
		/*
		 * 1. Connect
		 * 2. Select * query
		 * 3. Populate map from the result set.
		 * 4. Return.
		 */
		Map<Integer, Ingredient> theMap = new HashMap<Integer, Ingredient>();
		Connection conn = repo.getConnection();
		String s = "SELECT * FROM " + repo.getSchema() + "." + "INGREDIENT";
		Statement stmt = null;
		stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(s);
		
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
		theMap.put(i.getID(), i);
		}
		return theMap;
	}

	public Map<Integer, Ingredient> fetch(String name) {
		// TODO Fetch ingredients from the repository that have the name provided.
		return null;
	}

	public Ingredient fetch(int id) {
		// TODO Fetch the ingredient with the ID given, from the repository.
		return null;
	}
}
