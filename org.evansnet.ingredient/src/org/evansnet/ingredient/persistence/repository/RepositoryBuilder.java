package org.evansnet.ingredient.persistence.repository;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.evansnet.dataconnector.internal.dbms.MySQLConnection;
import org.evansnet.dataconnector.internal.dbms.SQLSrvConnection;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
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
	
	private IDatabase 	database;
	private String 		sqlCreate;		//Create table statement
	private String 		connStr;		//The connection string provided by the data connector.
	private Connection 	conn;			//The database connection provided by the data connector.
	private IngredientRepository repo;	//The newly created ingredient repository.
	
	public RepositoryBuilder() {
		// This constructor gets a connection dialog and subsequently the connection.
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
		int h, endDB;
		switch(dbType) {
		case MS_SQLSrv :
			database = new SQLSrvConnection();
			
			//We know the format of the connection string so set the host based on the connection string content
			h = connStr.indexOf("://") + 3;
			endDB = connStr.indexOf(":", h);
			database.getHost().setHostName(connStr.substring(h, endDB));
			h = connStr.indexOf("/", h) + 1;						// Find the database name.
			endDB = connStr.indexOf(";", h); 
			if (endDB < 0) {
				database.setDatabaseName(connStr.substring(h));
			} else {
				database.setDatabaseName(connStr.substring(h, endDB));
				extractCredentials(connStr.substring(h,endDB));
			}
			break;
			
		case MySQL :
			database = new MySQLConnection();
			//jdbc:mysql://<<Host>>:3306/<<database>>
			h = connStr.indexOf("://") + 3;
			database.getHost().setHostName(connStr.substring(h, connStr.indexOf(":", h)));
			h = connStr.indexOf("/", h) + 1;						// Find the database name.
			endDB = connStr.indexOf(";", h); 
			if (endDB < 0) {
				database.setDatabaseName(connStr.substring(h));
			} else {
				database.setDatabaseName(connStr.substring(h, endDB));
			}
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
	public IngredientRepository createRepository() throws SQLException {
		if (connStr == null) {
			conn = buildConnection();
			} else {
			DBType dbType = parseForDBMS(connStr);
			try {
				declareDbType(dbType);
			} catch (ClassNotFoundException | SQLException e) {
				showErrMessageBox("Create Repository Error!", 
						"An error occurred while creating the repository table: " + e.getMessage() +
						"\n See the log for more information.");
				e.printStackTrace();
			}			
		}
		repo.setConnectStr(database.getConnectionString()); 	
		buildTable(conn);
		try {
			conn.close();
		} catch (SQLException e) {
			javaLogger.log(Level.FINEST, "Failed to close the repository database!");
			showErrMessageBox("Database Close Error!", 
					"An error occurred while closing the repository database: " + e.getMessage() +
					"\n See the log for more information.");
			e.printStackTrace();
		}
		persistConn();		// Store the connection string in the plug-in preferences.
		return repo;
	}
	
	public IngredientRepository createRepository(String strConn) throws SQLException {
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
		connStr = database.getConnectionString();
		conn = persistor.getDb().getConnection();
		return conn;
	}
	
	private void buildTable(Connection c) throws SQLException {
		if (database.getSchema() == null) {
			conn.setSchema("dbo"); //TODO: Pop a dialog and get the schema from the user.
		}
		
		//First build the create table statement.
		StringBuilder sb = new StringBuilder("CREATE TABLE ");
		sb.append("\"" + database.getSchema() + "\".\"INGREDIENT\" (");		//Repository table name is always INGREDIENT
		sb.append("   ID BIGINT PRIMARY KEY, ");
		sb.append("   ING_NAME VARCHAR(40) NOT NULL,");
		sb.append("   ING_DESC VARCHAR(80) NULL,");
		sb.append("   UNIT_OF_MEASURE BIGINT NULL,");
		sb.append("   PKG_UOM BIGINT NULL,");
		sb.append("   UNIT_PRICE DECIMAL(6,2) NULL,");
		sb.append("	  PKG_PRICE DECIMAL (6,2) NULL,");
		sb.append("	  IS_RECIPE ");
		
		if (database.getDBMS() == DBType.MS_SQLSrv) {
			sb.append("BIT DEFAULT 0 );" );
		} else if (database.getDBMS() == DBType.MySQL) {
			sb.append("BOOLEAN DEFAULT FALSE);");
		}
		
		sqlCreate = sb.toString();
		
		if (conn == null || conn.isClosed()) {
			//TODO: Get credentials for the connection if not already defined.
			conn = database.connect(connStr);
		}
		
		Statement st = conn.createStatement();
		try {
			st.executeUpdate(sqlCreate);
		} catch (SQLException e) {
			javaLogger.log(Level.SEVERE, "An error occurred while creating the repository table. ");
			showErrMessageBox("Create Repository Table Error",
					"The create table statement failed to create the ingredient repository table!");
		}
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
	
	/**
	 * Shows an error message box given the message provided.
	 * @return
	 */
	private void showErrMessageBox(String t, String msg) {
		try {
			MessageBox theMsg = new MessageBox(
						PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.NONE);
			theMsg.setText(t);
			theMsg.setMessage(msg);
			theMsg.open();
		} catch (Exception e) {
			javaLogger.log(Level.SEVERE, "Unable to get workbench: ", class_name);
			e.printStackTrace();
		}		
	}
	
	/**
	 * Called by declareDbType which passes a string that may consist of
	 * a user id and a password. Extract the user id and password and set them
	 * into this object's credential object and then into the database object's
	 * credentials object.
	 * @param s
	 */
	private boolean extractCredentials(String s) throws SQLException {
//		if (database == null) {
//			return false;
//		}
		ArrayList<String> cStr = new ArrayList<String>();
		cStr.add("user");
		cStr.add("password");
		try {
			for (String c : cStr) {
				if (s.contains(c)) {
					int start = s.indexOf(c);
					int end = s.indexOf(";", start) > 0 ? s.indexOf(";", start) : s.length();
					if (c.equals("user")) {
						start = start + 5;
						database.getCredentials().setUserID(s.substring(start, end));
						javaLogger.log(Level.INFO, "Setting user ID " + s.substring(start, end));
						continue;
					} else {
						start = start + 9;
						database.getCredentials().setPassword(s.substring(start, end));
//						javaLogger.log(Level.INFO, "Setting password " + s.substring(start, end));
						continue;
					}
				} else {
					return false;
				}
			}		
		} catch (StringIndexOutOfBoundsException ob) {
			return false;
		} catch(Exception e) {
			javaLogger.log(Level.WARNING, "An exception occurred when trying to get credentials" 
					+ " from the string provided. \n");
			e.printStackTrace(); 
			throw new SQLException(e);
		}
		return true;
	}
	
	// public method to facilitate JUnit test.
	public String credentialExtractTest(String s) throws ClassNotFoundException, SQLException {
		try {
			if (extractCredentials(s)) {
				return "passed";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "failed";
	}

	// Getters and Setters 
	
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
