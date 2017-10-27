package org.evansnet.ingredient.persistence.repository;

import java.util.HashMap;

import org.eclipse.jface.preference.IPreferenceStore;
import org.evansnet.dataconnector.internal.core.DBMS;
import org.evansnet.dataconnector.internal.core.Host;
import org.evansnet.ingredient.app.Activator;
import org.evansnet.ingredient.model.Ingredient;
import org.evansnet.ingredient.persistence.PersistenceProvider;

/**
 * Implements the usage of the ingredient repository. 
 * The repository is a database table that stores the ingredients 
 * created by the user. 
 * 
 * @author Dan Evans
 *
 */
public class IngredientRepository {
	
	String 	connStr;				//The JDBC connection string for the repository.
	DBMS 	repoConnection;			//A connection to the ingredient repository table.
	String 	repoName;
	String 	repoVersion;
	boolean isDefault;
	
	public IngredientRepository() {
		connStr = fetchDefaultRepo();
		repoName = new String("");
		repoConnection = new DBMS(new Host());
		isDefault = false;
	}
	
	/**
	 * Retrieves the default repository connection string from the default properties
	 * storage.
	 * @return	The connection string for the default ingredient repository.
	 */
	private String fetchDefaultRepo() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		connStr = store.getDefaultString(getConnectStr());
	
		return null;
	}

	public HashMap<Integer, Ingredient> getTreeIngredients() {
		//TODO: provide a list of ingredients from the repository table.
		PersistenceProvider provider = new PersistenceProvider();
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
}
