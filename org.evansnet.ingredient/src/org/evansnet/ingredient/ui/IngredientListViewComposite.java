package org.evansnet.ingredient.ui;

import org.eclipse.swt.widgets.Composite;

public class IngredientListViewComposite extends Composite {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public IngredientListViewComposite(Composite parent, int style) {
		super(parent, style);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
