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
import org.evansnet.ingredient.persistence.preferences.PreferenceConstants;

public class RepositoryImpl implements IRepository {

	public static String THIS_CLASS_NAME = RepositoryImpl.class.getName();
	public static Logger javaLogger = Logger.getLogger(THIS_CLASS_NAME);
	protected String connStr;
	IDatabase repo;
	protected String repoName;
	protected String repoVersion;
	protected boolean isDefault;
	protected HashMap<Integer, Object> contents;
	protected ResultSet resultSet;
	private   String tableName;

	public RepositoryImpl() {
		connStr = new String();
		repoName = new String("");
		isDefault = false;
		contents = new HashMap<Integer, Object>();
	}

	@Override
	public String fetchDefaultRepo() throws Exception {
		RepositoryHelper helper = new RepositoryHelper();
		try {
			repo = helper.getDefaultRepository();
			if (repo == null) {
				//Null means no default is defined. Create a default repo.
			}
		} catch (Exception e) {
			String message = new String(""); 
			if (repo == null) {
				message = "The repository is null. Has the default been established?";
			}
			javaLogger.log(Level.SEVERE,"The repository is null. Has the default been established? \n");
			e.printStackTrace();
			throw new Exception("fetchDefaultRepo() failed! \n" + message);
		}
		isDefault = true;
		setRepoName("Default Repository");
		connStr = repo.getConnectionString();
		return connStr;
	}

	@Override
	public String getRepoVersion() {
		//TODO: Get repository version from the repository table and set the value.s
		return repoVersion;
	}

	@Override
	public void setConnectStr(String c) {
		connStr = c;
	}

	@Override
	public String getConnectStr() {
		return connStr;
	}

	@Override
	public void setRepoName(String name) {
		repoName = name;
	}

	@Override
	public String getRepoName() {
		return repoName;
	}

	@Override
	public void setDefault() {
		isDefault = true;
		persistDefault();	// Store the default repo connection string in the plug-in properties.
	}

	@Override
	public boolean isDefault() {
		return isDefault;
	}

	@Override
	public void setRepo(IDatabase database) {
		repo = database;
	}

	@Override
	public Map<Integer, Object> fetchAll() throws Exception {
		return null;
	}

	@Override
	public Object fetchById(int id) throws Exception {
		if (contents.isEmpty()) {
			fetchAll();
		}
		if (contents.containsKey(id)) {
			return  contents.get(id);
		} 
		return null;
	}

	@Override
	public List<Object> fetchByName(String n) {
		List<Object> result = new ArrayList<Object>();
		return result;
	}

	@Override
	public int doInsertNew(Object ing) throws Exception {
		return 0;
	}

	@Override
	public int doUpdate(Object ing) throws SQLException, Exception {
		return 0;
	}

	@Override
	public int doDelete(int i) throws SQLException, Exception {
		int rowsDel = -1;
		StringBuilder delete = new StringBuilder("DELETE FROM " + repo.getSchema() + "." +
				tableName + " WHERE ID = " + i);
		Connection conn = connect();
		try {
			Statement s = conn.createStatement();
			rowsDel = s.executeUpdate(delete.toString());
			contents.remove(i);		// Remove the object from the map.
			javaLogger.logp(Level.INFO, THIS_CLASS_NAME, "doDelete()", 
					"Successfully deleted ingredient with ID = " + i + " Rows deleted = " + rowsDel);
			return i;
		} catch (Exception e) {
			javaLogger.logp(Level.SEVERE, THIS_CLASS_NAME, "doDelete()", 
					"An error occurred while deleting ingredient with ID " + i +
					" \n" + e.getMessage());
		} finally {
			javaLogger.logp(Level.INFO, THIS_CLASS_NAME, "doDelete()", "Closing database connection.");
			conn.close();
		}
		return 0;
	}

	@Override
	public boolean checkExists(int i) throws SQLException, Exception {
		Connection conn = connect();
		String s = "SELECT * FROM " + repo.getSchema() + "." + tableName + " WHERE ID=" + i;
		Statement stmt = conn.createStatement();
		try {
			javaLogger.logp(Level.INFO, THIS_CLASS_NAME, "checkExist()", 
					"Checking for existence of " + tableName + " . Executing query.");
			ResultSet rs = stmt.executeQuery(s);
				if (rs.next()) {
					conn.close();
					return true;
				}	
		} catch (Exception e) {
			javaLogger.logp(Level.SEVERE, THIS_CLASS_NAME, "checkExist()", 
					"An exception was thrown while checking for " + tableName + "  with ID = " + i +
					"\n" + e.getMessage());
			e.printStackTrace();
		} finally {
			conn.close();
		}
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

	@Override
	public boolean isEmpty() throws Exception {
		if (contents.isEmpty()) {			//Check the map first if empty than run the query.
			if (fetchAll().isEmpty()) {
				return true;
			}
		} 
		return false;
	}

	/** 
	 * Gets the next ingredient ID to be used from the database. 
	 * @return The number of ingredients in the table + 1
	 * @throws SQLException 
	 */
	protected int getNextID() throws Exception {
		int lastID = 0;
		Connection conn = connect();		
		try {
			String query = "SELECT COUNT(ID) FROM " + repo.getSchema()+ "." + tableName + ";";
			Statement sqlStatement = conn.createStatement();
			ResultSet rs = sqlStatement.executeQuery(query);
			rs.next();
			lastID = rs.getInt(1); 
		} catch (SQLException e) {
			String message = "Not able to get the next " + tableName + " ID \n" + 
				       "Encountered an exception when accessing the database " + 
						repo.getDatabaseName()+"." + repo.getDatabaseName() + "\n" + 
			            e.getErrorCode() + " " + e.getMessage();
			javaLogger.logp(Level.SEVERE, THIS_CLASS_NAME, "getNextID()", message);
			throw new SQLException(message);
		} finally {
			conn.close();
		}
		return ++lastID;
	}

	protected Connection connect() throws Exception {
			Connection conn = repo.getConnection();
			if (conn == null || conn.isClosed()) {
				try {
					conn = repo.connect(repo.getConnectionString());
				} catch (SQLException e) {
					throw new SQLException(e.getMessage());
				} 
			}
			return conn;
		}
	
	public String getTableName() {
		return tableName;
	}
	
	public void setTableName(String name) {
		tableName = name;
	}
	
	/**
	 * Removes an object from the internal Map of objects. 
	 * @param i The ID of the object to remove.
	 */
	public void removeMappedItem(int i) {
		contents.remove(i);
	}
}