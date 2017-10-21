package org.evansnet.ingredient.test;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.evansnet.ingredient.persistence.repository.RepositoryBuilder;

public class testExtractCredentialsFromString {

	ArrayList<String> testStrings;

	@Before
	public void setUp() throws Exception {
		testStrings = new ArrayList<String>();
		testStrings.add("user=dan;password=123");
		testStrings.add("password=321;user=dan");
		testStrings.add("user=dan");
		testStrings.add("password=123");
		testStrings.add("");
		testStrings.add("user=dan;password=123;parm=something");
	}

	@After
	public void tearDown() throws Exception {
		
	}

	@Test
	public void testCredentialExtractTest() {
		String ret = new String("");
		RepositoryBuilder builder = new RepositoryBuilder();
		for(String s:testStrings) {
			try {
				ret = builder.credentialExtractTest(s);
			} catch (ClassNotFoundException | SQLException e) {
				e.printStackTrace();
				fail("Failed handling string: " + s);
			}
			if (ret.equals("failed")) {
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

}
