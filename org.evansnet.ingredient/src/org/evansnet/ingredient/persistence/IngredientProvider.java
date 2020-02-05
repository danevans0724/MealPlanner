package org.evansnet.ingredient.persistence;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;

import org.evansnet.dataconnector.internal.core.IDatabase;
import org.evansnet.ingredient.model.Ingredient;

/**
 * Provides persistence for ingredients. 
 * 
 * @author Dan Evans.
 */
public class IngredientProvider extends PersistenceProviderImpl {
	
	Ingredient ingredient;

	
	public IngredientProvider() throws Exception {
		super();
		ingredient = new Ingredient();
	}
		
	public IngredientProvider(Ingredient i, IngredientPersistenceAction a) throws Exception {
		this();
		ingredient = i;
	}
	
	/* (non-Javadoc)
	 * @see org.evansnet.ingredient.persistence.IPersistenceProvider#doSave()
	 */

	public int doSave(Object ingToSave) throws SQLException {
		try {
			int id = ((Ingredient)ingToSave).getID();	//A new ingredient will id = 0.
			if	(!super.checkExists(id)) {
				id = super.doInsertNew(ingToSave);
				return id;
			} else {
				super.doUpdate(ingToSave);
			}
		} catch (Exception e) {
			StringBuilder message = new StringBuilder("An exception occurred while storing an ingreding in the repository. ");
			message.append("\n The error message is: " + e.getCause() + " " + e.getMessage());
			showErrMessageBox(message.toString());
			javaLogger.logp(Level.SEVERE, THIS_CLASS_NAME, "doSave()", message.toString());
			e.printStackTrace();
		}
		return 0;
	}
	
	/* (non-Javadoc)
	 * @see org.evansnet.ingredient.persistence.IPersistenceProvider#doUpdate(org.evansnet.ingredient.model.Ingredient)
	 */
	@Override
	public int doUpdate(Object i) throws SQLException  {
		return super.doUpdate((Ingredient)i);
	}
	
	/* (non-Javadoc)
	 * @see org.evansnet.ingredient.persistence.IPersistenceProvider#doDelete(org.evansnet.ingredient.model.Ingredient)
	 */
	
	public int doDelete(Object i) throws SQLException, Exception {
		int id = ((Ingredient)i).getID();
		if (id == 0) {
			// The object in the editor doesn't have an id assigned. See if it exists in the database
		List<Object> ing = super.fetchByName(((Ingredient)i).getIngredientName());
			if (ing.isEmpty() ||ing == null) {
				return 0;		// It isn't in the repository, so return.
			} else {
				// Get the id
				id = ((Ingredient)ing.get(0)).getID();
			}
		} 
		// This deletes the first ingredient with the name shown.
		//TODO: Add a routine that gives the user a choice of which one to delete.
		//Always delete by ingredient id because id is unique.
		id = super.doDelete(id);	
		return id;
	}
	
	/* (non-Javadoc)
	 * @see org.evansnet.ingredient.persistence.IPersistenceProvider#getRepository()
	 */
	@Override
	public IDatabase getRepository() {
		// If repository hasn't been set yet, get the default.
		return repo;
	}

	
}
