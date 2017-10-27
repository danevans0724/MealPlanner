package org.evansnet.ingredient.persistence.repository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.PlatformUI;
import org.evansnet.dataconnector.internal.core.Credentials;
import org.evansnet.dataconnector.internal.core.DBType;
import org.evansnet.dataconnector.internal.core.IDatabase;
import org.evansnet.dataconnector.internal.dbms.MySQLConnection;
import org.evansnet.dataconnector.internal.dbms.SQLSrvConnection;

/**
 * Provides methods for working with ingredient repository objects.
 * @author pmidce0
 *
 */
public class RepositoryHelper {
	
	public static String THIS_CLASS_NAME = "org.evansnet.ingredient.persistence.repository.RepositoryHelper";
	Logger javaLogger = Logger.getLogger(THIS_CLASS_NAME);
	
	private IDatabase database;
	String connStr;

	public RepositoryHelper(IDatabase db) {
		connStr = new String();
		database = db;
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
	public void declareDbType(DBType dbType, IDatabase database) throws ClassNotFoundException, SQLException {
		int h, endDB;
		String connStr = new String(database.getConnectionString());
		switch(dbType) {
		case MS_SQLSrv :
			database = new SQLSrvConnection();
			
			//We know the format of the connection string so set the host based on the connection string content
			h = connStr.indexOf("://") + 3;
			endDB = connStr.indexOf(":", h);
			database.getHost().setHostName(connStr.substring(h, endDB));
			h = connStr.indexOf("database=") + 9;						// Find the database name.
			endDB = connStr.indexOf(";", h); 
			if (endDB < 0) {
				database.setDatabaseName(connStr.substring(h));
			} else {
				database.setDatabaseName(connStr.substring(h, endDB));
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
	 * Parses the JDBC connection string in order to get the DBMS type. The 
	 * method assumes a JDBC string format of:
	 * jdbc:<DBMS type>:/host:port or a variant consistent with the DBMS systems 
	 * that are supported.
	 * 
	 * @param c  A valid JDBC connection string for a supported DBMS.
	 * @return A DBMS manufacturer/type like DB2, MS SQL Server etc. 
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
	public void showErrMessageBox(String t, String msg) {
		try {
			MessageBox theMsg = new MessageBox(
						PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.NONE);
			theMsg.setText(t);
			theMsg.setMessage(msg);
			theMsg.open();
		} catch (Exception e) {
			javaLogger.log(Level.SEVERE, "Unable to get workbench: ", THIS_CLASS_NAME);
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
	public boolean extractCredentials(String s) {
		if (database == null) {
			return false;
		}
		if (database.getCredentials() == null) {
			database.setCredentials(new Credentials(new String(), new String()));
		}
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
		}
		return true;
	}
	

}
