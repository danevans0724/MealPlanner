package org.evansnet.ingredient.ui;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import org.evansnet.ingredient.model.Ingredient;
import org.evansnet.ingredient.persistence.repository.IngredientRepository;
import org.evansnet.ingredient.ui.providers.IngredientTreeContentProvider;
import org.evansnet.ingredient.ui.providers.IngredientTreeLabelProvider;


/**
 * Provides a tree view for navigating Ingredient types and ingredients in 
 * the system. 
 * @author pmidce0
 *
 */
public class IngredientExplorerView extends ViewPart {
	public static final String ID = "org.evansnet.ingredient.ui.ingredientexplorerview";
	
	public static final String THIS_CLASS_NAME = "org.evansnet.ingredient.ui.IngredientExplorerView";
	
	Logger javaLogger = Logger.getLogger(THIS_CLASS_NAME);
	
	HashMap<Integer, Ingredient> ingredients;
	TreeViewer treeviewer;
	IngredientRepository repo;
	
	
	public IngredientExplorerView() {
		try {
			repo = new IngredientRepository();
			repo.setConnectStr(repo.fetchDefaultRepo());
			javaLogger.logp(Level.INFO, THIS_CLASS_NAME, "IngredientRepositoryView()", repo.getRepoName());
			ingredients = (HashMap<Integer, Ingredient>) repo.fetchAll(); 	//ingredients now holds the repository contents.
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		javaLogger.logp(Level.INFO, THIS_CLASS_NAME, "createPartControl", 
				"Creating ingredient explorer view.");
		treeviewer = new TreeViewer(parent, SWT.H_SCROLL | SWT.V_SCROLL);
	}

	@Override
	public void setFocus() {
//		treeComposite.setFocus();
	}

	/**
	 * Sets the ingredient repository to the one specified in the input parameter.
	 * @param r The ingredient repository to use.
	 */
	public void setRepository(IngredientRepository r) {
		// TODO: If the repo is reset, then the tree needs to be repopulated.
		repo = r;
	}
}
