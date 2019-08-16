package org.evansnet.ingredient.test;

import static org.junit.Assert.*;

import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.Connection;
import java.sql.SQLException;

import org.evansnet.dataconnector.internal.core.DBType;
import org.evansnet.dataconnector.internal.dbms.SQLSrvConnection;
import org.evansnet.ingredient.persistence.repository.IRepository;
import org.evansnet.ingredient.persistence.repository.IngredientRepository;
import org.evansnet.ingredient.persistence.repository.RepositoryBuilder;
import org.evansnet.ingredient.persistence.repository.RepositoryHelper;

import static org.easymock.EasyMock.*;

import org.easymock.EasyMock;
import org.easymock.Mock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestRepositoryBuilder {
	
	public static final Logger testLogger = Logger.getLogger("RepositoryBuilderLogger");
	String conn1 = "jdbc:sqlserver://localhost:1433;database=TESTDB;user=AUser;password=ThePwd";	//SQL Server test
	String testRepoName = "testRepoName";
	DBType typeSQLSrv = DBType.MS_SQLSrv;
	DBType typeMySql  = DBType.MySQL;
	RepositoryBuilder builder;
	
	//Mocks
	@Mock private DatabaseMock dbMock = createMock(DatabaseMock.class);
	@Mock public RepositoryHelper helperMock = createMock(RepositoryHelper.class);
	@Mock private Connection connMock = createNiceMock(Connection.class);
	@Mock private IRepository repoMock = createNiceMock(IngredientRepository.class);
	@Mock private Statement stmtMock = createNiceMock(Statement.class);
	

	@Before
	public void setUp() throws Exception {
		
		//Mocks
		expect(dbMock.getSchema()).andReturn("dbo").anyTimes();
		expect(dbMock.getDBMS()).andReturn(typeSQLSrv);
		expect(dbMock.getConnectionString()).andReturn(conn1).anyTimes();
		expect(dbMock.getConnection()).andReturn(connMock);
		EasyMock.checkOrder(dbMock, false);
		replay(dbMock);
		expect(connMock.isClosed()).andReturn(false);
		expect(connMock.createStatement()).andReturn(stmtMock);
		EasyMock.checkOrder(connMock, false);
		replay(connMock);
		expect(helperMock.declareDbType(EasyMock.anyObject(), EasyMock.anyString())).andReturn(dbMock);
		expect(helperMock.buildConnection()).andReturn(connMock);
		expect(helperMock.parseForDBMS(EasyMock.anyString())).andReturn(typeSQLSrv);
		EasyMock.checkOrder(helperMock, false);
		replay(helperMock);
		expect(stmtMock.executeUpdate(EasyMock.anyString())).andReturn(1); //1 row updated.		
		replay(stmtMock);
	}

	@After
	public void tearDown()  {
		dbMock = null;
		connMock = null;
		helperMock = null;
		stmtMock = null;
	}

	@Test
	public void testCreateRepositoryWithString() {
		DBType type = null;
		builder = new RepositoryBuilder(testRepoName);
		try {
			IRepository r = builder.createRepository(helperMock, conn1);		
			type = builder.getDatabase().getDBMS();
			assertTrue(type == DBType.MS_SQLSrv && r.getRepoName().equals("testRepoName"));		//Change to a more meaningful result.
		} catch (Exception e) {
			fail("Could not create Ingredient repository table!\n" );
		}
	}
	
	@Test
	public void testCreateRepository() {
		builder = new RepositoryBuilder(testRepoName); 
		builder.setConnStr(conn1);
		try {
			IRepository r = builder.createRepository(helperMock);
			assertTrue(r.getRepoName().equals(testRepoName));
		} catch (Exception e) {
			testLogger.log(Level.SEVERE, "Test: testCreateRepository() failed! " + e.getMessage());
			fail("An exception occurred causing a test failure: ");
		}
	}	
	
	private class DatabaseMock extends SQLSrvConnection {
		
	
		public DatabaseMock() throws ClassNotFoundException, SQLException {
			super();
		}

		@Override
		public Connection getConnection() {
			return connMock;
		}

		@Override
		public String getConnectionString() {
			return conn1;
		}
		
		@Override
		public void setConnectionString(String s) {
			//Mock method.
		}
		
		@Override
		public Connection connect(String s) {
			return connMock;
		}
		
		@Override
		public String getSchema() {
			return "dbo";
		}
		
		@Override
		public DBType getDBMS() {
			return typeSQLSrv;
		}
	}
	
}
