package org.evansnet.ingredient.ui.providers;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.evansnet.ingredient.model.Ingredient;
import org.evansnet.ingredient.persistence.repository.IngredientRepository;

public class IngredientTreeContentProvider implements ITreeContentProvider {
	
	public static final String THIS_CLASS_NAME = IngredientTreeContentProvider.class.getName();
	public Logger javaLogger = Logger.getLogger(THIS_CLASS_NAME);
	
	private IngredientRepository repo;
	
	@Override
	public void dispose() {		
	}
		
	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof IngredientRepository) {
			repo = ((IngredientRepository)inputElement);
			Object[] ingList = new Object[] {"Ingredients"};
			return ingList;
		} else 
			return new Object[0];
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof IngredientRepository) {
			return getChildren("Ingredients");
		} else if (parentElement instanceof Object[]) {
			return (Object[])parentElement;
		} else if (parentElement instanceof String && ((String)parentElement) == "Ingredients") {
			Object[] names = fetchNames(repo);
			return names;
		}	else 		
			return new Object[] {"getChildred", "failed"};
	}

	@Override
	public Object getParent(Object element) {
		// This tree has only one level under the root.
		if (element instanceof String) {
			return "Ingredients";
		}
		return new String("getParent");
	} 
	
	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof IngredientRepository) {
			return true;
		} else if (element instanceof String && ((String)element)== "Ingredients") { 
			// The repository hash map has an entry set of ID's and ingredients
			return true;
		}
		return false; 	// The entry set of ingredients is the lowest item in the hierarchy.
	}

	public Object[] fetchNames(IngredientRepository r) {
		javaLogger.log(Level.INFO, "Fetching ingredients from the repository");
		HashMap<Integer, Ingredient> repoIngredients = r.getTreeIngredients();
		Object[] names = new Object[repoIngredients.size()];
		int count = 0;
		for (Map.Entry<Integer, Ingredient> i : repoIngredients.entrySet()) {
			String n = ((Ingredient)i.getValue()).getIngredientName();
			names[count] = (Object) n;
			++count;
		}
		return names;
	}


	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub		
	}

}
