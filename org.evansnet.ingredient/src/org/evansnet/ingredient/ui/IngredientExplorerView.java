package org.evansnet.ingredient.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;


/**
 * Provides a tree view for navigating Ingredient types and ingredients in 
 * the system. 
 * @author pmidce0
 *
 */
public class IngredientExplorerView extends ViewPart {
	public static final String ID = "org.evansnet.ingredient.ui.ingredientexplorerview";
	
	Composite treeComposite;

	@Override
	public void createPartControl(Composite parent) {
		treeComposite = new IngredientExplorerComposite(parent, SWT.NONE);
	}

	@Override
	public void setFocus() {
		treeComposite.setFocus();
	}

}
