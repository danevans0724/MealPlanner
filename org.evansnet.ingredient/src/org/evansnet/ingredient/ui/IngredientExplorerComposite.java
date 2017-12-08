package org.evansnet.ingredient.ui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Tree;
import org.evansnet.ingredient.ui.providers.IngredientTreeContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Label;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeNodeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;


public class IngredientExplorerComposite extends Composite {
	
	TreeViewer treeviewer;
	ITreeContentProvider treeContent;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public IngredientExplorerComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));
		
		Label lblThisIsTo = new Label(this, SWT.NONE);
		lblThisIsTo.setText("This is to test the composite");
		
		treeviewer = new TreeViewer(this);
		treeviewer.setContentProvider(treeContent = new IngredientTreeContentProvider());

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
