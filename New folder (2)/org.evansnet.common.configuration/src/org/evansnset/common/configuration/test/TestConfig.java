package org.evansnset.common.configuration.test;

import static org.junit.Assert.*;

import org.evansnet.common.configuration.Globals;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestConfig {
	
	Globals globals;

	@Before
	public void setUp() throws Exception {
		globals = new Globals();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testFetchConfig() {
		try {
			
		} catch (Exception e) {
			fail("Exception thrown: " + e.getMessage());
			e.printStackTrace();
		}
	}

	@Test
	public void testSaveConfig() {
		try {
			globals.saveConfig();
		} catch (Exception e) {
			fail("Exception thrown: " + e.getMessage());
			e.printStackTrace();
		}
	}

}
