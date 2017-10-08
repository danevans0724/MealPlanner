package org.evansnet.ingredient.persistence.repository;

import java.sql.Connection;
import java.sql.SQLException;

import org.evansnet.dataconnector.internal.core.IHost;
import org.evansnet.dataconnector.internal.dbms.MySQLConnection;
import org.evansnet.dataconnector.internal.dbms.SQLSrvConnection;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.PlatformUI;
import org.evansnet.dataconnector.internal.core.DBType;
import org.evansnet.dataconnector.internal.core.IDatabase;
import org.evansnet.ingredient.app.Activator;
import org.evansnet.ingredient.persistence.PersistenceProvider;
import org.evansnet.ingredient.persistence.preferences.PreferenceConstants;

/**
 * A class that allows for the creation of a repository in a database and 
 * the management of existing repositories.
 * 
 * @author Dan Evans
 *
 */
public class RepositoryBuilder {
	
	private IHost	 	host;			//The database host machine.
	private IDatabase 	database;
	private String 		sqlCreate;		//Create table statement
	private String 		connStr;		//The connection string provided by the data connector.
	private Connection 	conn;			//The database connection provided by the data connector.
	
	/*
	 * The repository builder 
	 * Allows the user to define the connection to a database for the 
	 * purpose of creating a repository.
	 * 
	 * Creates the repository table in the database selected.
	 * 
	 * Persists the connection information (connection string) to the 
	 * application preferences.
	 *  
	 */
	
	private RepositoryBuilder() {
		// This constructor gets a connection dialog and subsequently the connection.
	}
	
	private RepositoryBuilder(String strConn) {
		//The constructor builds the repository if given a valid connection string as a parameter.
		DBType dbType = parseForDBMS(strConn);
		try {
			declareDbType(dbType);
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void declareDbType(DBType dbType) throws ClassNotFoundException, SQLException {
		switch(dbType) {
		case MS_SQLSrv :
			database = new SQLSrvConnection();
			break;
		case MySQL :
			database = new MySQLConnection();
			break;
		default:
			database = null;
			break;
		}
	}

	public String createRepository() {
		//TODO: Implement public call to the repository builder.
		return connStr;
	}
	
	public String createRepository(String strConn) {
		new RepositoryBuilder(strConn);
		return connStr;
	}
	
	/**
	 * Uses the ingredient persistence provider to create a connection definition and to 
	 * return the connection to the database. 
	 * @return A JDBC connection to the database. 
	 */
	public Connection buildConnection() {
		PersistenceProvider persistor = new PersistenceProvider(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		persistor.showConnDialog();
		database = persistor.getDb();
		host = persistor.getHost();
		connStr = database.getConnectionString();
		conn = persistor.getDb().getConnection();
		return conn;
	}
	
	private void buildTable(Connection c) {
		//TODO: Implement code to run the script to create the ingredient table. 
	}
	
	/**
	 * Persists the connection string to the plug-in preference store.
	 */
	private void persistConn() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setValue(PreferenceConstants.PRE_REPO_CONN_STR, connStr);
	}
	
	/**
	 * Parses the JDBC connection string in order to get the DBMS type. The 
	 * method assumes a JDBC string format of:
	 * jdbc:<DBMS type>:/host:port or a variant consistent with the DBMS systems 
	 * that are supported.
	 * @param c A valid JDBC connection string
	 * @return A DBMS manufacturer/type like DB2, MS SQL Server etc. 
	 */
	private DBType parseForDBMS(String c) {
		DBType type; 
		
		return null;
	}

	public IHost getHost() {
		return host;
	}

	public void setHost(IHost host) {
		this.host = host;
	}

	public String getConnStr() {
		return connStr;
	}

	public void setConnStr(String connStr) {
		this.connStr = connStr;
	}

	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}

	public IDatabase getDatabase() {
		return database;
	}
	
}
