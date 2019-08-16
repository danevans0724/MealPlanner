package org.evansnet.ingredient.test;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import static org.easymock.EasyMock.*;

import org.easymock.EasyMock;
import org.easymock.Mock;
import org.evansnet.dataconnector.internal.core.Credentials;
import org.evansnet.dataconnector.internal.core.DBType;
import org.evansnet.dataconnector.internal.core.IDatabase;
import org.evansnet.dataconnector.internal.dbms.SQLSrvConnection;
import org.evansnet.ingredient.model.Ingredient;
import org.evansnet.ingredient.persistence.repository.IRepository;
import org.evansnet.ingredient.persistence.repository.IngredientRepository;
import org.evansnet.ingredient.persistence.repository.RepositoryBuilder;
import org.evansnet.ingredient.persistence.repository.RepositoryHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestRepository {
	
	private static Logger testLogger = Logger.getLogger("TestRepositoryLogger");
	private IRepository repo;
	Map<Integer, Ingredient> map1 = new HashMap<>();
	String c = "jdbc.jtds.sqlserver:/TheHost:1433/TheDatabase;user=Dan;password=password";

	//Mocks
	@Mock private IDatabase dbMock = createNiceMock(SQLSrvConnection.class);
	@Mock private Credentials credentialsMock;
	@Mock private RepositoryBuilder repoBuilderMock = createNiceMock(RepositoryBuilder.class);
	@Mock private RepositoryHelper  repoHelperMock = createNiceMock(RepositoryHelper.class);
	@Mock private Connection connMock = createNiceMock(Connection.class);
	@Mock private Statement stmtMock = createNiceMock(Statement.class);
	@Mock private ResultSet rsMock = createNiceMock(ResultSet.class);
	@Mock private Ingredient i;
	
	@Before
	public void setUp() throws Exception {
		
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
		
		//Setup mocks
		expect(dbMock.getConnectionString()).andReturn(c).anyTimes();
		expect(dbMock.getConnection()).andReturn(connMock).anyTimes();
		expect(dbMock.connect(EasyMock.anyString())).andReturn(connMock).anyTimes();
		expect(dbMock.getSchema()).andReturn("dbo");
		expect(dbMock.getDBMS()).andReturn(DBType.MS_SQLSrv);
		
		replay(dbMock);

		expect(repoHelperMock.parseForDBMS(c)).andReturn(DBType.MS_SQLSrv);
		expect(repoHelperMock.declareDbType(DBType.MS_SQLSrv, c)).andReturn(dbMock);
		replay(repoHelperMock);

		expect(connMock.isClosed()).andReturn(false);
		expect(connMock.createStatement()).andReturn(stmtMock).anyTimes();
		replay(connMock);
		
		expect(stmtMock.executeQuery(EasyMock.anyString())).andReturn(rsMock);
		expect(stmtMock.executeUpdate(EasyMock.anyString())).andReturn(1);
		replay(stmtMock);

		expect(rsMock.next()).andReturn(true);
		expect(rsMock.getInt(1)).andReturn(5);
		replay(rsMock);

		repo = new IngredientRepository();
		repo.setRepo(dbMock);

	}

//	private Certificate fetchCert() throws Exception {
//		String certFile = "C:\\Users\\pmidce0\\git\\dataconnector\\org.evansnet.dataconnector\\security\\credentials.cer";
//		FileInputStream fis = new FileInputStream(certFile);
//		Certificate cert = CertificateFactory.getInstance("X.509").generateCertificate(fis);
//		return cert;
//	}

	@After
	public void tearDown() throws Exception {
		dbMock = null;
		credentialsMock = null;
		repo = null;
		map1 = null;
	}
	
	@Test
	public void testDoInsertNew() throws Exception {
		try {
		 	int result = repo.doInsertNew(i);
		 	assertEquals(6, result);
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
			Map<Integer, Object> ingredients = repo.fetchAll();
			assertNotNull(ingredients);
			for (Integer i : ingredients.keySet()) {
				Ingredient theIng = (Ingredient)ingredients.get(i);
				System.out.println(theIng.getID() + " " + theIng.getIngredientName());
			}
		} catch (Exception e) {
			fail("Exception thrown on fetchAll()! " + e.getMessage());
			e.printStackTrace();
		}
	}
	

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
