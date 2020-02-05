package org.evansnet.ingredient.repository;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.evansnet.dataconnector.internal.core.Credentials;
import org.evansnet.dataconnector.internal.core.DBType;
import org.evansnet.dataconnector.internal.core.IDatabase;
import org.evansnet.ingredient.app.Activator;
import org.evansnet.ingredient.model.Ingredient;
import org.evansnet.ingredient.persistence.preferences.PreferenceConstants;
import org.evansnet.repository.core.RepositoryHelper;
import org.evansnet.repository.core.RepositoryImpl;


/**
 * Implements the usage of the ingredient repository. 
 * The repository is a database table that stores the ingredients 
 * created by the user. 
 * 
 * @author Dan Evans
 *
 */
public class IngredientRepository extends RepositoryImpl /*implements IRepository */ {	
	
	private static final String INGREDIENT = "INGREDIENT";
	public final String THIS_CLASS_NAME = IngredientRepository.class.getName();
	
	public IngredientRepository() {
		super();
		setTableName(INGREDIENT);
	}
	
	/**
	 * Retrieve a hash map containing the ingredients stored in the repository table. This
	 * is used, among other things, to populate the tree in the ingredient explorer
	 * view and fill the list of ingredient types in the ingredient editor.
	 * 
	 * @return A list of the ingredients contained in the repository tree.
	 */
	public HashMap<Integer, Object> getTreeIngredients() {
		contents.clear();
			try {
				fetchAll();
				return  contents;
			} catch (Exception e) {
				javaLogger.logp(Level.SEVERE, THISCLASSNAME, "getTreeIngredients()" ,
						"An exception was thrown when retrieving ingredients from the repository " +
						e.getMessage());
				e.printStackTrace();
			}
		return null;
	}
	
	
	public Map<Integer, Object> fetchAll() throws Exception {
		contents.clear();
		Connection conn = connect();
		String s = "SELECT * FROM " + repo.getSchema() + "." + INGREDIENT;
		Statement stmt = null;
		stmt = conn.createStatement();
		resultSet = stmt.executeQuery(s);
	
		while (resultSet.next()) {
			Ingredient i = new Ingredient();
			i.setID(resultSet.getInt("ID"));
			i.setIngredientName(resultSet.getString("ING_NAME"));
			i.setIngredientDescription(resultSet.getString("ING_DESC"));
			i.setStrUom(resultSet.getString("UNIT_OF_MEASURE"));
			i.setPkgUom(resultSet.getString("PKG_UOM"));
			i.setUnitPrice(resultSet.getBigDecimal("UNIT_PRICE"));
			i.setPkgPrice(resultSet.getBigDecimal("PKG_PRICE"));
			i.setIsRecipe(resultSet.getBoolean("IS_RECIPE"));
			i.setIngredientType(resultSet.getInt("ING_TYPE"));
		contents.put(i.getID(), i);
		}
		conn.close();
		return contents;
	}

	
	public List<Object> fetchByName(String n) {
		List<Object> result = new ArrayList<>();
			try {
				fetchAll();
			} catch (Exception e) {
				javaLogger.logp(Level.INFO, THISCLASSNAME, "fetchName()", 
						"Fetching " + getTableName() + " list from the repository.");
			}
		for (Object i : contents.values()) {
			if (((Ingredient)i).getIngredientName().equals(n)) {
				result.add(i);
			}
		}
		return result;
	}
	
	
	public int doInsertNew(Object ing) throws SQLException {
		int rowsInserted = 0;
		int id = getNextID();
		Ingredient i = (Ingredient) ing;
		StringBuilder insert = new StringBuilder("INSERT INTO ");
		insert.append(repo.getSchema() + ".");
		insert.append(getTableName() + " VALUES(");
		insert.append(id + ", ");
		insert.append("'" + i.getIngredientName() + "', ");
		insert.append("'" + i.getIngredientDescription() + "', ");
		insert.append("'" + i.getStrUom() + "', " );
		insert.append("'" + i.getPkgUom() + "', ");
		insert.append("'" + i.getUnitPrice() + "', ");
		insert.append("'" + i.getPkgPrice() + "', ");
		if (i.isRecipe()) {
			if (repo.getDBMS().equals(DBType.MS_SQLSrv)) {
				insert.append("1"); 
			} else if (repo.getDBMS().equals(DBType.MySQL)) {
				insert.append("true");
			}
		} else {
			if (repo.getDBMS().equals(DBType.MS_SQLSrv)) {
				insert.append("0"); 
			} else if (repo.getDBMS().equals(DBType.MySQL)) {
				insert.append("false");
			}			
		}
		insert.append(", " + i.getIngredientType() + ")");
		javaLogger.logp(Level.INFO, THISCLASSNAME, "doInsertNew()",
				"Insert statement completed: \n" + insert.toString());
		javaLogger.logp(Level.INFO, THISCLASSNAME, "doInsertNew()",
				"Connecting to the repository database.");
		Connection con = connect();
		Statement stmt = con.createStatement();
		try {
			javaLogger.logp(Level.INFO, THISCLASSNAME, "doInsertNew()",
					"Executing insert query");
			rowsInserted = stmt.executeUpdate(insert.toString());
			if (rowsInserted > 0) {
				//The insert was successful so add the ingredient to the map.
				contents.put(i.getID(), i);
			javaLogger.logp(Level.INFO, THISCLASSNAME, "doInsertNew()",
					"Inserted " + rowsInserted + " rows successfully.");
			}
		} catch (Exception e) {
			javaLogger.logp(Level.SEVERE, THISCLASSNAME, "doInsertNew()",
					"Failed to insert ingredient record. Rows inserted:  " + rowsInserted +
					"\n" + e.getMessage());
			e.printStackTrace();
			throw new SQLException(e.getMessage());
		} finally {
			con.close();
		}
		return id;
	}

	
	public int doUpdate(Object ing) throws SQLException {
		int rowsUpd;
		Ingredient i = (Ingredient) ing;
		StringBuilder update = new StringBuilder();
		update.append("UPDATE " + repo.getSchema() + ".INGREDIENT SET ");
		update.append("ID=" + i.getID() + ", ");
		update.append("ING_NAME=\'" + i.getIngredientName() + "\', ");
		update.append("ING_DESC=\'" + i.getIngredientDescription() + "\', ");
		update.append("UNIT_OF_MEASURE=" + i.getStrUom() + ", ");
		update.append("PKG_UOM=" + i.getPkgUom() + ", ");
		update.append("UNIT_PRICE=" + i.getUnitPrice() + ", ");
		update.append("PKG_PRICE=" + i.getPkgPrice() + ", ");
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
		update.append(", ING_TYPE=" + i.getIngredientType());
		update.append(" WHERE ID=" + i.getID());
		
		javaLogger.logp(Level.INFO, THISCLASSNAME, "doUpdate()",
				"Update query constructed: \n" + update.toString());
		
		javaLogger.logp(Level.INFO, THISCLASSNAME, "doUpdate()", "Connecting to repository table. ");
		Connection conn = connect();
		Statement s = conn.createStatement();
		javaLogger.logp(Level.INFO, THISCLASSNAME, "doUpdate()", 
				"Executing update query.");
		try {
			rowsUpd = s.executeUpdate(update.toString());
			contents.put(i.getID(), ing);	//Add the new item to the mapped list of repository items.
			javaLogger.logp(Level.INFO, THISCLASSNAME, "doUpdate()", "Successfully updated record.");
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new SQLException("An error occurred while updating ingredient with ID " + i.getID());
		} finally {
			conn.close();
		}
		return rowsUpd;
	}

	/**
	 * Persists the connection string of the default ingredient repository table to the plug-in preference store.
	 */
	private void persistDefault() {
		try {
			IPreferenceStore store = Activator.getDefault().getPreferenceStore();
			store.setValue(PreferenceConstants.PRE_REPO_CONN_STR, repo.getConnectionString());
		} catch (Exception e) {
			String msg = "An error occured while storing the default repository " +
					"connection. The error message is: " + e.getMessage();
			javaLogger.log(Level.SEVERE, THISCLASSNAME, msg);
		}
	}

	/**
	 * Retrieves the connection string of the default repository from the system and 
	 * updates the provided database with the connection details. 
	 * 
	 * @param db An IDatabase object to update with the default repository's info
	 * @return An IDatabase object that contains the information needed to operate the repository
	 *            or null if the default repository is not yet defined.
	 */
	public IDatabase getDefaultRepository() throws Exception {
		String thisMethodName = "getDefaultRepository()";
		RepositoryHelper helper = null;
		try {
			IPreferenceStore prefStore = Activator.getDefault().getPreferenceStore();
			repo.setConnectionString(prefStore.getString(PreferenceConstants.PRE_REPO_CONN_STR));
			if (repo.getConnectionString().isEmpty() || repo.getConnectionString().equals("Repository JDBC connection string")) {
				String message = "There is no repository defined. Open Windows then prefernces to define a default repository.";
				javaLogger.logp(Level.SEVERE, THIS_CLASS_NAME, thisMethodName, message);
				IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				MessageBox mb = new MessageBox(window.getShell(), SWT.ERROR);
				mb.setText("Ingredient Repository Error! "); 
				mb.setMessage(message);
				mb.open();
				return null; 
			}
			helper = new RepositoryHelper(repo);
			helper.declareDbType(helper.parseForDBMS(repo.getConnectionString()), repo.getConnectionString());
			if (repo.getCredentials() == null) {
				repo.setCredentials(new Credentials());
			}
			repo.getCredentials().setUserID(prefStore.getString(PreferenceConstants.PRE_REPO_USER_ID).toCharArray());
			repo.getCredentials().setPassword(prefStore.getString(PreferenceConstants.PRE_REPO_USER_PWD).toCharArray());
		} catch (ClassNotFoundException | SQLException e) {
			javaLogger.logp(Level.SEVERE, THIS_CLASS_NAME, thisMethodName,
					"An exception occurred while attempting to get the default repository info from the preference store " + 
			         e.getMessage());
		} catch (Exception e) {
			javaLogger.logp(Level.SEVERE, THIS_CLASS_NAME, thisMethodName, 
					"An unidentifed exception occurred while retrieving the default ingredient repo" 
					+ e.getMessage());
			throw new Exception("getDefaultRepository failed. " + e.getMessage());
		}
		
		//TODO: This is a temporary hack to append the user ID and password to the connection string.
		//      To fix this, refactor this to use the dataconnector connection string factory.
		// 		Currently this is specific to a SQL Server DB. Must be generic to any supported DBMS.
		String connHack = repo.getConnectionString();
		connHack = connHack + ";user=" + new String(repo.getCredentials().getUserID());
		connHack = connHack + ";password=" + new String(repo.getCredentials().getPassword(helper.fetchCert()));
		repo.setConnectionString(connHack);
		return repo;
	}

}
