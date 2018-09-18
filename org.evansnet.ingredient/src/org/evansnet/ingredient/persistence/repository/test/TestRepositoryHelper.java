package org.evansnet.ingredient.persistence.repository.test;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

//import org.jmock.Mockery;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.evansnet.dataconnector.internal.core.DBType;
import org.evansnet.dataconnector.internal.core.IDatabase;
import org.evansnet.dataconnector.internal.dbms.SQLSrvConnection;
import org.evansnet.ingredient.persistence.repository.RepositoryHelper;


public class TestRepositoryHelper {

//	Mockery mockery = new Mockery();
	HashMap<DBType, String> connStrings;
	ArrayList<String> credentialTestStrings;
	IDatabase database;

		//jdbc:sqlserver://host\instanceName:portNumber;property=value;property=value
		//jdbc:sqlserver://host:portNumber;database=theDatabase;property=value;property=value
		//jdbc:jtds:sybase://host:port/database/
		//jdbc:mysql://Host:3306/database?user=name?password=pwd
	
	@Before
	public void setUp() throws Exception {
		database = new SQLSrvConnection();
		connStrings = new HashMap<DBType, String>();
		connStrings.put(DBType.MS_SQLSrv, "jdbc:sqlserver://host:1433;database=theDatabase;user=name;password=pwd");
		connStrings.put(DBType.MySQL, "jdbc:mysql://Host:3306/database?user=name?password=pwd");
//		connStrings.add("");  //TODO: configure for Sybase and others.
		
		credentialTestStrings = new ArrayList<String>();
		credentialTestStrings.add("user=dan;password=123");
		credentialTestStrings.add("password=321;user=dan");
		credentialTestStrings.add("user=dan");
		credentialTestStrings.add("password=123");
		credentialTestStrings.add("");
		credentialTestStrings.add("user=dan;password=123;parm=something");
	}

	@After
	public void tearDown() throws Exception {
		database = null;
		credentialTestStrings = null;
//		mockery = null;
	}

	@Test
	public void testCredentialExtractTest() {
		RepositoryHelper helper = new RepositoryHelper(database);
		for (String s : credentialTestStrings) {
			boolean strOk = false;
				strOk = helper.extractCredentials(s);
			if (!strOk) {
				if (s.isEmpty()) {
					assertTrue(true);	// We expect to fail on an empty string.
					continue;
				} else if (!(s.contains("user")) || !(s.contains("password"))) {
					assertTrue(true); 	// probably only has user or password but not both
					continue;
				}
				fail("Failed handling string: " + s);
			}
		}
		assertTrue(true);	// Test passed.
	}
	
	@Test
	public void testParseForDBMS() {
		RepositoryHelper rh = new RepositoryHelper(database);
		DBType theType;
		try {
			for (DBType t : connStrings.keySet()) {
				theType = rh.parseForDBMS(connStrings.get(t));
				switch(t) {
				case MS_SQLSrv :
					assertTrue(theType.equals(DBType.MS_SQLSrv));
					break;
				case MySQL :
					assertTrue(theType.equals(DBType.MySQL));
					break;
					default :
						fail("Incorrect DBMS chosen!");
				}
			}
		} catch (Exception e) {
			fail("An exception was thrown.");
		}
	}
	
	@Test
	public void testDeclareDB() throws Exception {
		RepositoryHelper helper = new RepositoryHelper(database);
		for (DBType t : connStrings.keySet()) {
			database.setConnectionString(connStrings.get(t));
			try {
				database = helper.declareDbType(t, database.getConnectionString());
			} catch (ClassNotFoundException | SQLException e) {
				fail("Exception thrown: " + e.toString());
				e.printStackTrace();
			}
			switch (t) {
			case MS_SQLSrv :
				assertTrue(database.getHost().getHostName().equals("host"));
				assertTrue(database.getDatabaseName().equals("theDatabase"));
				break;
			case MySQL :
				assertTrue(database.getHost().getHostName().equals("Host"));
				assertTrue(database.getDatabaseName().equals("database"));
				break;
				default :
					fail("Did not get correct database name!");
			}
		}
	}

}
