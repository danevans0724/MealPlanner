package org.evansnet.ingredient.persistence.repository;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.ui.PlatformUI;
import org.evansnet.dataconnector.internal.core.DBType;
import org.evansnet.dataconnector.internal.core.IDatabase;
import org.evansnet.ingredient.persistence.PersistenceProvider;

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
//		repo = new IngredientRepository();
	}
	
	public RepositoryBuilder(String strConn) {
		//The constructor builds the repository if given a valid connection string as a parameter.
		this();
		connStr = strConn;
	}
	

	/**
	 * Creates an ingredient repository for a supported DBMS system. If the 
	 * builder's constructor is given a valid JDBC connection string, the 
	 * method creates the repository table in that DBMS. If no connection 
	 * string is given, the method uses the data connector to define the 
	 * host, and DBMS in which to build the table.
	 * 
	 * @return An Ingredient repository object
	 * @throws ClassNotFoundException 
	 */	
	public IngredientRepository createRepository() throws SQLException, ClassNotFoundException {
		RepositoryHelper rhlp = new RepositoryHelper(database);
		if (connStr == null) {
			conn = buildConnection();
			} else {
			DBType dbType = rhlp.parseForDBMS(connStr);
			try {
				database = rhlp.declareDbType(dbType, connStr);
			} catch (ClassNotFoundException | SQLException e) {
				rhlp.showErrMessageBox("Create Repository Error!", 
						"An error occurred while creating the repository table: " + e.getMessage() +
						"\n See the log for more information.");
				e.printStackTrace();
			}			
		}
		repo = new IngredientRepository();
		repo.setConnectStr(database.getConnectionString()); 	
		repo.setRepo(database);
		buildTable(database.getConnection());
		try {
			conn.close();
		} catch (SQLException e) {
			javaLogger.log(Level.FINEST, "Failed to close the repository database!");
			rhlp.showErrMessageBox("Database Close Error!", 
					"An error occurred while closing the repository database: " + e.getMessage() +
					"\n See the log for more information.");
			e.printStackTrace();
		}
//		persistConn();		// Store the connection string in the plug-in preferences.
		return repo;
	}
	
	public IngredientRepository createRepository(String strConn) throws SQLException, ClassNotFoundException {
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
		if (database.getSchema() == null || database.getSchema().isEmpty()) {
			database.setSchema("dbo"); //TODO: Pop a dialog and get the schema from the user.
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
		javaLogger.logp(Level.INFO, class_name, "buildTable()", "Created create table DDL \n" + sb.toString());
		
		if (conn == null || conn.isClosed()) {		
			if (database.getCredentials().getUserID().isEmpty()) {
				javaLogger.log(Level.SEVERE, "No credentials are available!", class_name);
				throw new SQLException("No credentials available for repository build.");
				//TODO: Get credentials for the connection if not already defined.
			}
			javaLogger.logp(Level.INFO, class_name, "buildTable()", "Connecting to repository database..." );
			conn = database.connect(connStr);
		}
		
		Statement st = conn.createStatement();
		try {
			javaLogger.logp(Level.INFO, class_name, "buildTable()", "Executing DDL statement...");
			st.executeUpdate(sqlCreate);
			javaLogger.logp(Level.INFO, class_name, "buildTable()", "Successfully created repository table.");
		} catch (SQLException e) {
//			RepositoryHelper helper = new RepositoryHelper(database);
			javaLogger.log(Level.SEVERE, "An error occurred while creating the repository table. \n"
					+ e.getMessage());
//			helper.showErrMessageBox("Create Repository Table Error",
//					"The create table statement failed to create the ingredient repository table!");
		} finally {
			if (!conn.isClosed()) {
				conn.close();
			}
		}
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
