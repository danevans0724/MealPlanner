package org.evansnet.ingredient.persistence.repository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

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
 * The repository builder 
 * Allows the user to define the connection to a database for the 
 * purpose of creating a repository.
 * 
 * Creates the repository table in the database selected.
 * 
 * Persists the connection information (connection string) to the 
 * application preferences.
 *  
 * @author Dan Evans
 *
 */
public class RepositoryBuilder {
	
	public static final String class_name = "org.evansnset.ingredient.persistence.repository.RepositoryBuilder";
	public static Logger javaLogger = Logger.getLogger(class_name);
	
	private IHost	 	host;			//The database host machine.
	private IDatabase 	database;
	private String 		sqlCreate;		//Create table statement
	private String 		connStr;		//The connection string provided by the data connector.
	private Connection 	conn;			//The database connection provided by the data connector.
	private IngredientRepository repo;	//The newly created ingredient repository.
	
	public RepositoryBuilder() {
		// This constructor gets a connection dialog and subsequently the connection.
		host = null;
		database = null;
		sqlCreate = null;
		connStr = null;
		conn = null;
		repo = new IngredientRepository();
	}
	
	public RepositoryBuilder(String strConn) {
		//The constructor builds the repository if given a valid connection string as a parameter.
		this();
		connStr = strConn;
	}
	
	/**
	 * Used when the builder is supplied a connection string. After the string is parsed,
	 * and the type of DBMS is determined, then the database is constructed based on the 
	 * DBMS types supported by the org.evansnet.dataconnector plug-in.
	 * 
	 * @param dbType
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	private void declareDbType(DBType dbType) throws ClassNotFoundException, SQLException {
		switch(dbType) {
		case MS_SQLSrv :
			database = new SQLSrvConnection();
			//TODO: Set the host based on the connection string content
			break;
		case MySQL :
			database = new MySQLConnection();
			//TODO: Set the host based on the connection string content
			break;
		default:
			database = null;
			break;
		}
	}


	/**
	 * Creates an ingredient repository for a supported DBMS system. If the 
	 * builder's constructor is given a valid JDBC connection string, the 
	 * method creates the repository table in that DBMS. If no connection 
	 * string is given, the method uses the data connector to define the 
	 * host, and DBMS in which to build the table.
	 * 
	 * @return An Ingredient repository object
	 */	
	public IngredientRepository createRepository() {
		if (connStr == null) {
			conn = buildConnection();
			} else {
			DBType dbType = parseForDBMS(connStr);
			try {
				declareDbType(dbType);
			} catch (ClassNotFoundException | SQLException e) {
				// TODO Enhance this exception handler and fail gracefully.
				e.printStackTrace();
			}			
		}
		repo.setConnectStr(database.getConnectionString()); 	
		buildTable(conn);
		try {
			conn.close();
		} catch (SQLException e) {
			javaLogger.log(Level.FINEST, "Failed to close the repository database!");
			//TODO: Pop a messagebox.
			e.printStackTrace();
		}
		persistConn();		// Store the connection string in the plug-in preferences.
		return repo;
	}
	
	public IngredientRepository createRepository(String strConn) {
		connStr = strConn;
		repo = createRepository();
		return repo;
	}
	
	/**
	 * Uses the ingredient persistence provider to create a connection definition and to 
	 * return the connection to the database. This method also sets the IHost type, 
	 * IDatabase type and connection string for the repository database.
	 * 
	 * @return A JDBC connection to the database. 
	 */
	private Connection buildConnection() {
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
		StringBuilder sb = new StringBuilder("CREATE TABLE ");
		String schema = database.getSchema();
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
	 * 
	 * @param c  A valid JDBC connection string for a supported DBMS.
	 * @return A DBMS manufacturer/type like DB2, MS SQL Server etc. 
	 */
	private DBType parseForDBMS(String c) {
		DBType type = null; 
		if (c.contains("jdbc:mysql")) {					//Based on MySQL connector driver.
			type = DBType.MySQL;
		} else if (c.contains("jdbc:sqlserver")) {		//Based on Microsoft driver.
			type = DBType.MS_SQLSrv;
		}
		return type;
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
