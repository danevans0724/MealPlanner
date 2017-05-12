package org.evansnet.ingredient.ui;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.evansnet.ingredient.ui.IngredientInput;


/**
 * The editor for Ingredients.
 * 
 * @author pmidce0
 *
 */
public class IngredientEditor extends EditorPart {
	
	public static final String ID = "org.evansnet.ingredient.ingredienteditor";	
	IngredientCompositeBase ingEditorComposite;
	boolean dirty = false;

	public IngredientEditor() {
		super();
	}
	
	@Override
	public void doSave(IProgressMonitor monitor) {
		// TODO Create ingredient save routine.
		// 1. Get / open a database connection
		// 2. If new ingredient, create insert statement,
		// 3. otherwise, create update statement.
		// 4. Write data to the table.
		// 5. Close the connection.
	}

	@Override
	public void doSaveAs() {
		// Do nothing for now.
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setSite(site);
		setInput(input);
	}

	@Override
	public boolean isDirty() {
		return dirty;
	}

	@Override
	public boolean isSaveAsAllowed() {
		// Disable save-as for now. Each ingredient should be saved as itself.
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {
		ingEditorComposite = new IngredientCompositeBase(parent, SWT.NONE);		
	}

	@Override
	public void setFocus() {
		ingEditorComposite.setFocus();
	}
	
}
