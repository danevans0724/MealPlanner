package org.evansnet.ingredient.persistence.repository;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.security.cert.CertificateException;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.evansnet.dataconnector.internal.core.Credentials;
import org.evansnet.dataconnector.internal.core.DBType;
import org.evansnet.dataconnector.internal.core.IDatabase;
import org.evansnet.dataconnector.internal.dbms.MySQLConnection;
import org.evansnet.dataconnector.internal.dbms.SQLSrvConnection;
import org.evansnet.dataconnector.ui.ConnectionDialog;
import org.evansnet.ingredient.app.Activator;
import org.evansnet.ingredient.persistence.preferences.PreferenceConstants;

/**
 * Provides methods for working with ingredient repository objects.
 * @author pmidce0
 *
 */
public class RepositoryHelper {
	
	public static final String THIS_CLASS_NAME = RepositoryHelper.class.getName();
	Logger javaLogger = Logger.getLogger(THIS_CLASS_NAME);
	
	private IDatabase database;
	String connStr;
	
	public RepositoryHelper() {
		connStr = "";		
	}

	public RepositoryHelper(IDatabase db) {
		this();
		database = db;
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
		try {
			IPreferenceStore prefStore = Activator.getDefault().getPreferenceStore();
			connStr = prefStore.getString(PreferenceConstants.PRE_REPO_CONN_STR);
			if (connStr.isEmpty() || connStr.equals("Repository JDBC connection string")) {
				String message = "There is no repository defined. Open Windows then prefernces to define a default repository.";
				javaLogger.logp(Level.SEVERE, THIS_CLASS_NAME, thisMethodName, message);
				IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				MessageBox mb = new MessageBox(window.getShell(), SWT.ERROR);
				mb.setText("Ingredient Repository Error! "); 
				mb.setMessage(message);
				mb.open();
				return null; 
			}
			declareDbType(parseForDBMS(connStr), connStr);
			database.getCredentials().setUserID(prefStore.getString(PreferenceConstants.PRE_REPO_USER_ID).toCharArray());
			database.getCredentials().setPassword(prefStore.getString(PreferenceConstants.PRE_REPO_USER_PWD).toCharArray());
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
		String connHack = database.getConnectionString();
		connHack = connHack + ";user=" + new String(database.getCredentials().getUserID());
		connHack = connHack + ";password=" + new String(database.getCredentials().getPassword(fetchCert()));
		database.setConnectionString(connHack);
		return database;
	}
	
	private Certificate fetchCert() throws FileNotFoundException, CertificateException {
		// TODO Refactor this into the common security plugin.
		String certFile = "C:\\Users\\pmidce0\\git\\dataconnector\\org.evansnet.dataconnector\\security\\credentials.cer";
		FileInputStream fis = new FileInputStream(certFile);
		return CertificateFactory.getInstance("X.509").generateCertificate(fis);
	}

	/**
	 * Used when the builder is supplied a connection string. After the string is parsed,
	 * and the type of DBMS is determined, then the database is constructed based on the 
	 * DBMS types supported by the org.evansnet.dataconnector plug-in.
	 * 
	 * @param dbType
	 * @throws Exception 
	 */
	public IDatabase declareDbType(DBType dbType, String c) throws Exception {
		int h;
		int endDB;
		String connectStr = c;
		switch(dbType) {
		case MS_SQLSrv :
			IDatabase sqldb = new SQLSrvConnection();
			//TODO: Update parser to handle inclusion of an instance name.
			//We know the format of the connection string so set the host based on the connection string content
			//jdbc:sqlserver://host:portNumber;database=theDatabase;property=value;property=value
			h = connectStr.indexOf("://") + 3;
			endDB = connectStr.indexOf(':', h);
			sqldb.getHost().setHostName(connectStr.substring(h, endDB));
			h = connectStr.indexOf("database=") + 9;						// Find the database name.
			endDB = connectStr.indexOf(';', h); 
			if (endDB < 0) {
				sqldb.setDatabaseName(connectStr.substring(h));
			} else {
				sqldb.setDatabaseName(connectStr.substring(h, endDB));
			}
			sqldb.setConnectionString(connectStr);
			if (extractCredentials(connectStr));
			sqldb.setSchema("dbo");     // Set default to dbo.
			return sqldb;
			
		case MySQL :
			//jdbc:mysql://<<Host>>:3306/<<database>>
			IDatabase mydb = new MySQLConnection();
			h = connectStr.indexOf("://") + 3;
			mydb.getHost().setHostName(connectStr.substring(h, connectStr.indexOf(":", h)));
			h = connectStr.indexOf('/', h) + 1;						// Find the database name.
			endDB = connectStr.indexOf('?', h); 
			if (endDB < 0) {
				mydb.setDatabaseName(connectStr.substring(h));
			} else {
				mydb.setDatabaseName(connectStr.substring(h, endDB));
			}
			mydb.setConnectionString(connectStr);
			return mydb;
		default:
			return null;
		}
	}

	
	/**
	 * Parses the JDBC connection string in order to get the DBMS type. The 
	 * method assumes a JDBC string format of:
	 * jdbc:<DBMS type>:/host:port or a variant consistent with the DBMS systems 
	 * that are supported.
	 * 
	 * @param c  A valid JDBC connection string for a supported DBMS.
	 * @return A DBMS manufacturer/type like DB2, MS SQL Server etc. 
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	public DBType parseForDBMS(String c) {
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
	public void showErrMessageBox(String title, String msg) {
		try {
			MessageBox theMsg = new MessageBox(
						PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.NONE);
			theMsg.setText(title);
			theMsg.setMessage(msg);
			theMsg.open();
		} catch (Exception e) {
			javaLogger.log(Level.SEVERE, "Unable to get workbench: " + e.getMessage(), THIS_CLASS_NAME);
		}		
	}
	
	/**
	 * Called by declareDbType which passes a string that may consist of
	 * a user id and a password. Extract the user id and password and set them
	 * into this object's credential object and then into the database object's
	 * credentials object.
	 * @param s
	 * @throws Exception 
	 */
	public boolean extractCredentials(String s) throws Exception {
		if (database == null) {
			return false;
		}
		if (database.getCredentials() == null) {
			database.setCredentials(new Credentials());
		}
		ArrayList<String> cStr = new ArrayList<>();
		cStr.add("user");
		cStr.add("password");
		try {
			for (String c : cStr) {
				if (s.contains(c)) {
					int start = s.indexOf(c);
					int end = s.indexOf(';', start) > 0 ? s.indexOf(';', start) : s.length();
					if (c.equals("user")) {
						start = start + 5;
						database.getCredentials().setUserID(s.substring(start, end).toCharArray());
						javaLogger.log(Level.INFO, "Setting user ID  %s", s.substring(start, end));
						continue;
					} else {
						start = start + 9;
						database.getCredentials().setPassword(s.substring(start, end).toCharArray());
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
					+ " from the string provided. \n" + e.getMessage());
		}
		return true;
	}
	
	/**
	 * Used to create a connection definition and to return the connection to the database. 
	 * In the process, this method sets the IHost type, and connection string for the repository database.
	 * 
	 * @return A JDBC connection to the database. 
	 */
	public Connection buildConnection() {
		Connection conn = null;
		try {
			ConnectionDialog connectionDialog = new ConnectionDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.NONE);
			database = (IDatabase)connectionDialog.open();
			setConnectionString(database.getConnectionString());
			conn = database.getConnection();
		} catch (Exception e) {
			String message = "An exception occurred while creating a connection to the repository";
			javaLogger.logp(Level.SEVERE, RepositoryHelper.class.getName(), 
					"buildConnection()", message + "\n" + e.getMessage());
			conn = null;
		}
		return conn;
	}
	
	public void setConnectionString(String s) {
		connStr = s;
	}
}
