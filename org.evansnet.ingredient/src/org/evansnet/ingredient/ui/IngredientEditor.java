package org.evansnet.ingredient.ui;

import java.sql.SQLException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.evansnet.ingredient.model.Ingredient;
import org.evansnet.ingredient.persistance.IngredientPersistenceAction;
import org.evansnet.ingredient.persistance.PersistenceProvider;


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
		PersistenceProvider provider = new PersistenceProvider(this.ingEditorComposite.getShell(),
				ingEditorComposite.getIngredient(),
				IngredientPersistenceAction.Ingredient_Save);

		//TODO: Later we should already have connections defined and just use them. 
		//      For now, we'll get the connection each time. 
		provider.showConnDialog();
		// 2. If new ingredient, create insert statement,
		// 3. otherwise, create update statement.
		// 4. Write data to the table.
		try {
			provider.doSave();
			// 5. Close the connection.
			provider.closeConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
