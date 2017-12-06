package org.evansnet.ingredient.ui;

import java.util.TreeMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import org.evansnet.ingredient.model.Ingredient;
import org.evansnet.ingredient.persistence.repository.IngredientRepository;


/**
 * Provides a tree view for navigating Ingredient types and ingredients in 
 * the system. 
 * @author pmidce0
 *
 */
public class IngredientExplorerView extends ViewPart {
	public static final String ID = "org.evansnet.ingredient.ui.ingredientexplorerview";
	
	Composite treeComposite;
	TreeMap<Integer, Ingredient> ingredients;
	IngredientRepository repo;

	@Override
	public void createPartControl(Composite parent) {
		treeComposite = new IngredientExplorerComposite(parent, SWT.NONE);
		ingredients = new TreeMap<Integer, Ingredient>();
		populate(repo);
	}

	@Override
	public void setFocus() {
		treeComposite.setFocus();
	}
	
	/**
	 * Reads the ingredients from the repository table and populates the tree with the contents
	 * of the table. The ingredients are stored in the hash map with the ID as the key and the 
	 * ingredient name as the value.
	 */
	private void populate(IngredientRepository r) {
		/*
		 * Algorithm:
		 * Ask the repository to connect the database
		 * Run a select query against the repository.
		 * Use the result set to populate the hash map from the result set.
		 * spin through the hash map and populate the tree.
		 */
		
	}
	
	public void setRepository(IngredientRepository r) {
		repo = r;
	}
}
