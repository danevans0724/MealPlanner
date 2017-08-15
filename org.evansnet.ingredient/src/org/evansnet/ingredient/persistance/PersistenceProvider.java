package org.evansnet.ingredient.persistance;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
import org.evansnet.ingredient.persistance.IngredientPersistenceAction;

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
	ProgressBar progress;
	
	public PersistenceProvider(Shell shell, Ingredient i, IngredientPersistenceAction a) {
		connectDialog = new ConnectionDialog(shell, SWT.NONE);	
		host = new Host();
		db = new DBMS();
		ingredient = i;
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
	
	public void doUpdate() throws SQLException {
		//TODO: Update the ingredient (update query).
	}
	
	public void doDelete() throws SQLException {
		//TODO: Delete the ingredient (delete query).
	}
	
	public void closeConnection() throws SQLException {
		this.closeConnection(conn);
	}
	
	private void closeConnection(Connection c) throws SQLException {
		c.close();
	}
	
	/**
	 * Called when a new ingredient is to be added to the ingredient table
	 * @param i; The ingredient object to save
	 * @return The SQL INSERT query necessary to add the ingredient.
	 */
	private String buildInsertQuery(Ingredient i) {
		StringBuilder sb = new StringBuilder("INSERT INTO ");
		sb.append(db.getSchema() + "." + db.getDatabaseName());
		sb.append(" VALUES( ");
		sb.append(getNextID() + ","); 
		sb.append("\"" + i.getIngredientName() + "\",");
		sb.append("\"" + i.getIngredientDescription() + "\",");
		sb.append("\"" + i.getStrUom() + "\",");
		sb.append(i.getPkgPrice() + ", ");
		sb.append("\"" + i.getPkgUom() + "\",");
		sb.append( i.getPkgPrice() + ",");
		
		if (i.isRecipe()) {
			sb.append(1 + ",");
		}
		sb.append(0 + " );");
		javaLogger.log(Level.FINEST, "Insert query; " + sb.toString());
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
