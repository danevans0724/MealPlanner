package org.evansnet.ingredient.persistence;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.evansnet.dataconnector.internal.core.IDatabase;
import org.evansnet.dataconnector.internal.core.DBMS;
import org.evansnet.dataconnector.internal.core.Host;
import org.evansnet.dataconnector.internal.core.IHost;
import org.evansnet.dataconnector.ui.ConnectionDialog;
import org.evansnet.ingredient.model.Ingredient;
import org.evansnet.ingredient.persistence.IngredientPersistenceAction;

/**
 * Base class for the ingredient persistence mechanism.
 * Provides connectivity to a database and to an ingredient table through
 * a data connector. 
 * 
 * @author pmidce0
 */
public class PersistenceProvider {
	
	/* Algorithm
	 * ------------------
	 * 1. get a connection to the database
	 * 2. Set the persistence type: Fetch, save, update, delete.
	 * 3. Set up a basic query for the type.
	 * 4. Allow the sub-class of the persistence type to execute the query.
	 * 
	 */		
	private static Logger javaLogger = Logger.getLogger("IngredientPersistanceProvider");
	IHost host;
	IDatabase  db;
	ConnectionDialog connectDialog;
	Connection conn;
	Ingredient ingredient;
	ProgressBar progress;		//TODO: implement the progress bar for connection operation.
	
	public PersistenceProvider(Shell s) {
		connectDialog = new ConnectionDialog(s, SWT.NONE);	
		host = new Host();
		db = new DBMS();	
	}
	
	public PersistenceProvider(Shell shell, Ingredient i, IngredientPersistenceAction a) {
		this(shell);
		ingredient = i;
	}
	
	public IHost getHost() {
		return host;
	}

	public IDatabase getDb() {
		return db;
	}

	public Connection getConn() {
		return conn;
	}

	/**
	 * Provides a way to get the connection to the database. Shows the 
	 * connector dialog and persists the connection info to the repository.
	 */
	public void showConnDialog() {
		db = (IDatabase)connectDialog.open();
		conn = db.getConnection();	
	}
	
	public void doSave() throws SQLException {
		Statement sqlStatement = db.getConnection().createStatement();
		String query = buildInsertQuery(ingredient);
		int rows = sqlStatement.executeUpdate(query);
		javaLogger.log(Level.INFO, "IngredientPersistenceProvider.doSave() \n " + 
				rows + " Rows affected.");
	}
	
	/**
	 * Fetch the ingredient(s) with the name provided
	 * @param name The name of the ingredient
	 * @return A list of ingredients with the name provided.
	 */
	public Map<Integer, Ingredient> doFetch(String name) {
		//TODO: Ingredient persistence: Fetch the ingredient(s) with the name provided 
		return null;
	}
	
	/**
	 * Return the ingredient with the ID = int
	 * @param id An integer value representing the ingrediennt ID.
	 * @return The ingredient with that ID, or null if it doesn't exist.
	 */
	public Ingredient doFetch(int id) throws SQLException {
		return null;
	}
	
	public void doUpdate(int id) throws SQLException {
		//TODO: Update the ingredient (update query).
	}
	
	public void doDelete(int id) throws SQLException {
		//TODO: Delete the ingredient (delete query).
	}
	
	public void closeConnection() throws SQLException {
		this.closeConnection(conn);
		javaLogger.log(Level.INFO, "Database connection closed.");
	}
	
	private void closeConnection(Connection c) throws SQLException {
		c.close();
		javaLogger.log(Level.INFO, "Database connection closed.");
	}
	
	/**
	 * Called when a new ingredient is to be added to the ingredient table
	 * @param i; The ingredient object to save
	 * @return The SQL INSERT query necessary to add the ingredient.
	 */
	private String buildInsertQuery(Ingredient i) {
		StringBuilder sb = new StringBuilder("INSERT INTO ");
		sb.append(db.getSchema() + "." + "INGREDIENT");
		sb.append(" VALUES( ");
		sb.append(getNextID() + ","); 
		sb.append("\'" + i.getIngredientName() + "\',");
		sb.append("\'" + i.getIngredientDescription() + "\',");
		sb.append("\'" + "1" + "\',");	//TODO: Translate from value selected in combo.
		sb.append(i.getPkgPrice() + ", ");
		sb.append("\'" + "1" + "\',");	//TODO: Translate from value selected in combo.
		sb.append( i.getPkgPrice() + ",");
		
		if (i.isRecipe()) {
			sb.append(1 + ",");
		}
		sb.append(0 + " );");
		javaLogger.log(Level.INFO, "Insert query; " + sb.toString());
		return sb.toString();
	}
	
	/** 
	 * Gets the next ingredient ID to be used from the database. 
	 * @return The number of ingredients in the table + 1
	 */
	private int getNextID() {
		int lastID = 0;
		String query = "SELECT COUNT(ID) FROM " + db.getSchema()+ "." + "INGREDIENT" + ";";
		try {
			Statement sqlStatement = conn.createStatement();
			ResultSet rs = sqlStatement.executeQuery(query);
			rs.next();
			lastID = rs.getInt(1); 
		} catch (SQLException e) {
			javaLogger.log(Level.SEVERE, "Not able to get the next ingredient ID \n" + 
		       "Encountered an exception when accessing the database " + 
					db.getDatabaseName()+"." + db.getDatabaseName() + "\n" + 
		            e.getErrorCode() + " " + e.getMessage());
		}
		return ++lastID;
	}
}