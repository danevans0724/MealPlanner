package org.evansnet.ingredient.persistence;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;

import org.evansnet.ingredient.model.Ingredient;
import org.evansnet.ingredient.persistence.IngredientPersistenceAction;
import org.evansnet.ingredient.persistence.repository.IRepository;
import org.evansnet.ingredient.persistence.repository.IngredientRepository;
import org.evansnet.ingredient.persistence.repository.RepositoryHelper;

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
	@Override
	public int doSave(Object ingToSave) throws SQLException {
		try {
			int id = ((Ingredient)ingToSave).getID();	//A new ingredient will id = 0.
			if	(!repository.checkExists(id)) {
				id = repository.doInsertNew(ingToSave);
				return id;
			} else {
				repository.doUpdate(ingToSave);
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
	public void doUpdate(Object i) throws SQLException, Exception {
		repository.doUpdate((Ingredient)i);
	}
	
	/* (non-Javadoc)
	 * @see org.evansnet.ingredient.persistence.IPersistenceProvider#doDelete(org.evansnet.ingredient.model.Ingredient)
	 */
	@Override
	public int doDelete(Object i) throws SQLException, Exception {
		int id = ((Ingredient)i).getID();
		if (id == 0) {
			// The object in the editor doesn't have an id assigned. See if it exists in the database
		List<Object> ing = repository.fetchByName(((Ingredient)i).getIngredientName());
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
		id = repository.doDelete(id);	
		return id;
	}
	
	/* (non-Javadoc)
	 * @see org.evansnet.ingredient.persistence.IPersistenceProvider#setRepsitory(org.evansnet.ingredient.persistence.repository.IngredientRepository)
	 */
	@Override
	public void setRepsitory(IRepository repo) {
		repository = repo;
	}
	
	/* (non-Javadoc)
	 * @see org.evansnet.ingredient.persistence.IPersistenceProvider#getRepository()
	 */
	@Override
	public IRepository getRepository() throws Exception {
		// If repository hasn't been set yet, get the default.
		if (repository == null) {
			repository = new IngredientRepository();
			RepositoryHelper helper = new RepositoryHelper();
			repository.setRepo(helper.getDefaultRepository());
		} 
		return repository;
	}
	
}
