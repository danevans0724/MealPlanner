package org.evansnet.ingredient.persistence.repository.test;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.evansnet.ingredient.model.Ingredient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestRepository {
	
	Map<Integer, Ingredient> map1 = new HashMap<Integer, Ingredient>();

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testFetchAll() {
		fail("Not yet implemented");	//TODO: Set up to mock a repository connection and query.
	}

	@Test
	public void testFetchID() {
		fail("Not yet implemented");
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
