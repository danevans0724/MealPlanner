package org.evansnet.ingredient.persistence;

import java.sql.Connection;
import java.sql.SQLException;

import org.evansnet.dataconnector.internal.core.IDatabase;
import org.evansnet.ingredient.model.IngredientType;
import org.evansnet.ingredient.persistence.repository.IRepository;

/**
 * Provides methods for fetching, saving, updating, and deleting 
 * ingredients on the database. 
 * 
 * @author Dan Evans
 *
 */
public class IngredientTypeProvider implements IPersistenceProvider {
	
	IngredientType ingredientType;
	IngredientPersistenceAction action;
	
	public IngredientTypeProvider(IngredientType i, IngredientPersistenceAction a) throws Exception {
		// TODO Add code for getting the database and table to use. 
		//      Then, call the methods on the parent class to establish the
		//      connection to the database.
		
	}

	@Override
	public IDatabase getDb() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Connection getConn() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int doSave(Object typeToSave) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void doUpdate(Object i) throws SQLException, Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int doDelete(Object i) throws SQLException, Exception {
		return 0;
		}

	@Override
	public void closeConnection() throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setRepsitory(IRepository repo) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IRepository getRepository() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}




}
