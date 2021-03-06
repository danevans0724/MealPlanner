package org.evansnet.ingredient.persistence.repository.test;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	Ingredient i;
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
		
		//Create an ingredient for insert.
		i = new Ingredient();
		i.setID(1);
		i.setIngredientName("Flour");
		i.setIngredientDescription("All purpose flour");
		i.setPkgPrice(new BigDecimal("2.65"));
		i.setUnitPrice(new BigDecimal("0.26"));
		i.setPkgUom("1");
		i.setStrUom("1");
		i.setIsRecipe(false);
	}

	@After
	public void tearDown() throws Exception {
		db = null;
		credentials = null;
		repo = null;
		map1 = null;
	}
	
	@Test
	public void testDoInsertNew() {
		try {
		 	int result = repo.doInsertNew(i);
		 	assertEquals(1, result);
		} catch (Exception e) {			
			fail("An Exception was thrown during insert attempt! " + e.getMessage());
		}
	}
	
	@Test
	public void testDoUpdate() {
		i.setIngredientName("All Purpose Flour");
		try {
			int result = repo.doUpdate(i);
			assertEquals(1, result);
		} catch (Exception e) {
			fail("Exception thrown on update! " + e.getMessage());
		}
	}
	
	@Test
	public void testDoFetchAll() {
		//Test executing a select statement from the IngredientRepository class.
		try {
			Map<Integer, Ingredient> ingredients = repo.fetchAll();
			assertNotNull(ingredients);
			for (Integer i : ingredients.keySet()) {
				Ingredient theIng = ingredients.get(i);
				System.out.println(theIng.getID() + " " + theIng.getIngredientName());
			}
		} catch (Exception e) {
			fail("Exception thrown on fetchAll()! " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	@Test
	public void testFetchID() {
		Ingredient i = new Ingredient();
		try {
			i = repo.fetchById(1);
			assertNotNull(i);
			assertEquals("All Purpose Flour", i.getIngredientName());
			System.out.println(i.getID() + ", " + i.getIngredientName());
		} catch (Exception e) {
			fail("Exception thrown from testFetchID() " + e.getMessage());
		}
	}

	@Test
	public void testFetchName() {
		String nameToFetch = "All Purpose Flour";
		List<Ingredient> returned = new ArrayList<Ingredient>();
		try {
			returned = repo.fetchByName(nameToFetch);
			assertEquals(nameToFetch, returned.get(0).getIngredientName());
			assertEquals(returned.size(), 1);
			System.out.println(returned.get(0).getID() + ", " + returned.get(0).getIngredientName());
		} catch(Exception e) {
			fail("Exception thrown on fetchName()! " + e.getMessage());
		}
	}

//	@Test
//	public void testFetchMultiName() {
//		fail("Not yet implemented");
//	}

	@Test
	public void testDoDelete() {
		int toDelete = 1;
		try {
			int rowsDeleted = repo.doDelete(toDelete);
			assertEquals(1, rowsDeleted);
		} catch (Exception e) {
			fail("Exception thrown on delete! " + e.getMessage());
		}
	}
	
}
