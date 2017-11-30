package org.evansnet.ingredient.persistence.repository.test;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.evansnet.dataconnector.internal.core.Credentials;
import org.evansnet.dataconnector.internal.core.DBType;
import org.evansnet.dataconnector.internal.core.IDatabase;
import org.evansnet.dataconnector.internal.dbms.SQLSrvConnection;
import org.evansnet.ingredient.model.Ingredient;
import org.evansnet.ingredient.persistence.repository.IngredientRepository;
import org.evansnet.ingredient.persistence.repository.RepositoryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestRepository {
	
	private IDatabase db;
	private Credentials credentials;
	private IngredientRepository repo;
	Map<Integer, Ingredient> map1 = new HashMap<Integer, Ingredient>();

	@Before
	public void setUp() throws Exception {
		//"jdbc:sqlserver://localhost:1433;database=DCEDB01;user=devans;password=3xnhlcup"
		db = new SQLSrvConnection();
		db.getHost().setHostName("localhost");
		db.getHost().setPort(1433);
		credentials = new Credentials("devans", "3xnhlcup");
		db.setInstanceName(db.getHost().getHostName());
		db.setDatabaseName("DCEDB01");
		db.setCredentials(credentials);
		db.setSchema("dbo");
		db.addParms("user", credentials.getUserID());
		db.addParms("password", credentials.getPassword());
		db.buildConnectionString(DBType.MS_SQLSrv);

		// Build the repository table.
		RepositoryBuilder builder = new RepositoryBuilder(db.getConnectionString());
		repo = builder.createRepository(db.getConnectionString());
	}

	@After
	public void tearDown() throws Exception {
		db = null;
		credentials = null;
		repo = null;
		map1 = null;
	}
	
	@Test
	public void testDoFetchAll() {
		//Test executing a select statement from the IngredientRepository class.
		try {
			Map<Integer, Ingredient> ingredients = repo.fetchAll();
			assertNotNull(ingredients);
			for (Integer i : ingredients.keySet()) {
				Ingredient theIng = ingredients.get(i);
				System.out.println(theIng.getID() + theIng.getIngredientName());
			}
		} catch (Exception e) {
			fail("Exception thrown! " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	@Test
	public void testFetchID() {
		Ingredient i = new Ingredient();
		try {
			i = repo.fetch(1);
			assertNotNull(i);
		} catch (Exception e) {
			fail("Exception thrown from testFetchID() " + e.getMessage());
			e.printStackTrace();
		}
	}

	@Test
	public void testFetchOneName() {
		fail("Not yet implemented");
	}

	@Test
	public void testFetchMultiName() {
		fail("Not yet implemented");
	}
}
