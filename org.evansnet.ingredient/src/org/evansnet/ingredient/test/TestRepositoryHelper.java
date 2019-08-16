package org.evansnet.ingredient.test;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import static org.easymock.EasyMock.*;
import org.easymock.Mock;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.evansnet.dataconnector.internal.core.DBType;
import org.evansnet.dataconnector.internal.core.Host;
import org.evansnet.dataconnector.internal.core.IDatabase;
import org.evansnet.dataconnector.internal.core.IHost;
import org.evansnet.ingredient.persistence.repository.RepositoryHelper;
import org.evansnet.dataconnector.internal.core.Credentials;


public class TestRepositoryHelper {
	
	String sqlserverConn = "jdbc:sqlserver://TestHost:1433;database=theDatabase;user=TheUserName;password=ThePwd";
	String mysqlConn = "jdbc:mysql://MySqlHost:3306/MySqlDatabase?user=TheUserName?password=pwd";
	HashMap<DBType, String> connStrings;
	ArrayList<String> credentialTestStrings;
	Credentials credentials = new Credentials();
	
	// Mocks
	@Mock IDatabase databaseMock = mock(IDatabase.class);
	@Mock IDatabase mySqlMock = mock(IDatabase.class);
	@Mock IHost hostMock = createMock(Host.class);

	//jdbc:sqlserver://host\instanceName:portNumber;property=value;property=value
	//jdbc:sqlserver://host:portNumber;database=theDatabase;property=value;property=value
	//jdbc:jtds:sybase://host:port/database/
	//jdbc:mysql://Host:3306/database?user=name?password=pwd
	
	@Before
	public void setUp() throws Exception {
		
		connStrings = new HashMap<>();
		connStrings.put(DBType.MS_SQLSrv, sqlserverConn);
		connStrings.put(DBType.MySQL, mysqlConn);
		
		credentials.setUserID("dan".toCharArray());
		credentials.setPassword("123".toCharArray());
		
		// Mock expects
		// This mock to test for MS Sql Server
		expect(databaseMock.getCredentials()).andReturn(credentials).anyTimes();
		expect(databaseMock.getConnectionString()).andReturn(connStrings.get(DBType.MS_SQLSrv));
		expect(databaseMock.getHost()).andReturn(hostMock);
		databaseMock.setConnectionString(anyString());
		expectLastCall();
		databaseMock.setDatabaseName(anyString());
		expectLastCall();
		replay(databaseMock);
		// This mock to test for MySQL
		expect(mySqlMock.getCredentials()).andReturn(credentials).anyTimes();
		expect(mySqlMock.getConnectionString()).andReturn(mysqlConn);
		expect(mySqlMock.getHost()).andReturn(hostMock);
		mySqlMock.setConnectionString(anyString());
		expectLastCall();
		mySqlMock.setDatabaseName(anyString());
		expectLastCall();
		replay(mySqlMock);
		hostMock.setHostName(anyString());
		expectLastCall();
		replay(hostMock);
	}

	@After
	public void tearDown() {
		databaseMock = null;
		credentials = null;
	}

	@Test
	public void testCredentialExtract() throws Exception {
		RepositoryHelper helper = new RepositoryHelper(databaseMock);
		boolean strOk = helper.extractCredentials(sqlserverConn);
		if (!strOk) {	// We expect to fail on an empty string.
			fail("Failed handling string: " + sqlserverConn);
		}
	}
	
	@Test
	public void testParseForDBMS() {
		RepositoryHelper rh = new RepositoryHelper(databaseMock);
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
	public void testDeclareDBSqlServer() throws Exception {
		RepositoryHelper helper = new RepositoryHelper(databaseMock);
		DBType t = DBType.MS_SQLSrv;
			try {
				databaseMock = helper.declareDbType(t, databaseMock.getConnectionString());
				assertTrue(databaseMock.getHost().getHostName().equals("TestHost"));
				assertTrue(databaseMock.getDatabaseName().equals("theDatabase"));
				assertTrue(databaseMock.getHost().getPort() == 1433);
				assertTrue(databaseMock.getSchema().equals("dbo"));
			} catch (ClassNotFoundException | SQLException e) {
				fail("Did not get correct databaseMock name, host name or port! Returned " 
				+ databaseMock.getHost().getHostName() + ", " 
				+ databaseMock.getDatabaseName() + ", " 
				+ databaseMock.getHost().getPort());
			}
	}		

	@Test
		public void testDeclareDBMySql() throws Exception {
			RepositoryHelper helper = new RepositoryHelper(mySqlMock);
			DBType t = DBType.MySQL;
				try {
					mySqlMock = helper.declareDbType(t, mySqlMock.getConnectionString());
					assertTrue(mySqlMock.getHost().getHostName().equals("MySqlHost"));
					assertTrue(mySqlMock.getDatabaseName().equals("MySqlDatabase"));
					assertTrue(mySqlMock.getHost().getPort() == 3306);
				} catch (ClassNotFoundException | SQLException e) {
					fail("Did not get correct databaseMock name, host name or port! Returned " 
							+ databaseMock.getHost().getHostName() + ", " 
							+ databaseMock.getDatabaseName() + ", " 
							+ databaseMock.getHost().getPort());
				}
		}
	}
