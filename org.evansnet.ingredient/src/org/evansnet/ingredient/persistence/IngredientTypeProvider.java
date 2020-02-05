package org.evansnet.ingredient.persistence;

import org.evansnet.ingredient.model.IngredientType;

/**
 * Provides methods for fetching, saving, updating, and deleting 
 * ingredients on the database. 
 * 
 * @author Dan Evans
 *
 */
public class IngredientTypeProvider extends PersistenceProviderImpl {
	
	IngredientType ingredientType;
	IngredientPersistenceAction action;
	
	public IngredientTypeProvider(IngredientType i, IngredientPersistenceAction a) throws Exception {
		// TODO Add code for getting the database and table to use. 
		//      Then, call the methods on the parent class to establish the
		//      connection to the database.
		
	}

}
