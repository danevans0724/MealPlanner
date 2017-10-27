package org.evansnet.ingredient.test;

import static org.junit.Assert.*;

import java.sql.SQLException;

import org.evansnet.dataconnector.internal.core.DBType;
import org.evansnet.ingredient.persistence.repository.IngredientRepository;
import org.evansnet.ingredient.persistence.repository.RepositoryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestRepositoryBuilder {
	
	String conn1 = "jdbc:sqlserver://localhost:1433;database=DCEDB01;user=devans;password=3xnhlcup";	//SQL Server test
	String conn2 = "jdbc:mysql://localhost:3306/INGREDIENT";	//MySQL test
	DBType typeSQLSrv = DBType.MS_SQLSrv;
	DBType typeMySql  = DBType.MySQL;
	IngredientRepository repo;
	RepositoryBuilder builder;
	
	//TODO: Refer to compuware tests to see how to make a mock DBMS instance.

	@Before
	public void setUp() throws Exception {
		repo = new IngredientRepository();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testMySQLCreateRepositoryWithString() {
		DBType type = null;
		builder = new RepositoryBuilder(conn2);
		try {
			builder.createRepository(conn2);		//Test build SQL Server
			type = builder.getDatabase().getDBMS();
			assertTrue(type == DBType.MS_SQLSrv);
		} catch (SQLException e) {
			fail("Could not create Ingredient repository table!\n" );
			e.printStackTrace();
		}
	}
	
	@Test
	public void testSQLServerCreateRepositoryWithString() {
		DBType type = null;
		builder = new RepositoryBuilder(conn1);
		try {
			builder.createRepository(conn1);		//Test build SQL Server
			type = builder.getDatabase().getDBMS();
			assertTrue(type == DBType.MS_SQLSrv);
		} catch (SQLException e) {
			fail("Could not create Ingredient repository table!\n" );
			e.printStackTrace();
		}
		
	}
}
