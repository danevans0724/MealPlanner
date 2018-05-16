package org.evansnet.ingredient.persistence;

import org.eclipse.swt.widgets.Shell;
import org.evansnet.ingredient.model.Ingredient;
import org.evansnet.ingredient.persistence.PersistenceProvider;

/**
 * Provides methods for fetching, saving, updating, and deleting 
 * ingredients on the database. 
 * 
 * @author pmidce0
 *
 */
public class IngredientTypePersister extends PersistenceProvider{
	
	IngredientPersistenceAction action;
	
	public IngredientTypePersister(Shell shell, Ingredient i, IngredientPersistenceAction a) throws Exception {
		super(i, a);
		// TODO Add code for getting the database and table to use. 
		//      Then, call the methods on the parent class to establish the
		//      connection to the database.
		
	}

}
