package org.evansnet.ingredient.ui;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
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
	
	TreeViewer treeviewer;
	IngredientRepository repo;
	
	
	public IngredientExplorerView() {
		try {
			repo = new IngredientRepository();
			repo.setConnectStr(repo.fetchDefaultRepo());	// TODO: Allow pointing to a different repo through the interface
			javaLogger.logp(Level.INFO, THIS_CLASS_NAME, "IngredientRepositoryView()", repo.getRepoName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		javaLogger.logp(Level.INFO, THIS_CLASS_NAME, "createPartControl", 
				"Creating ingredient explorer view.");
		treeviewer = new TreeViewer(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		treeviewer.setLabelProvider(new IngredientTreeLabelProvider());
		treeviewer.setContentProvider(new IngredientTreeContentProvider());
		treeviewer.setInput(repo);
		
		treeviewer.addDoubleClickListener(event -> {
			Viewer viewer = event.getViewer();
			ISelection sel = viewer.getSelection();
			if (!(sel.isEmpty()) && sel instanceof IStructuredSelection) {
				//TODO: Put code to call the ingredient into the editor here.
				Object ing = ((IStructuredSelection)sel).getFirstElement();
				if (ing instanceof String) {
					List<Ingredient> selectedIng = repo.fetchByName((String)ing);
					// We have the ingredient, now open the editor and put it in.
					
				}
			}
		});
	}

	@Override
	public void setFocus() {
	}

	/**
	 * Sets the ingredient repository to the one specified in the input parameter.
	 * @param r The ingredient repository to use.
	 */
	public void setRepository(IngredientRepository r) {
		// TODO: If the repo is reset, then the tree needs to be repopulated.
		repo = r;
		//TODO: Fire treeviewer change.
	}
}
