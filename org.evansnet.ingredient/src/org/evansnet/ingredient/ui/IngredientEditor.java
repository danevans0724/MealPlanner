package org.evansnet.ingredient.ui;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
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
	public static Logger javaLogger = Logger.getLogger("Ingredient editor logger");
	IngredientCompositeBase ingEditorComposite;
	boolean dirty = false;

	public IngredientEditor() {
		super();
	}
	
	@Override
	public void doSave(IProgressMonitor monitor) {
		// 1. Get / open a database connection
		PersistenceProvider provider = new PersistenceProvider(this.ingEditorComposite.getShell(),
				ingEditorComposite.getIngredient(),
				IngredientPersistenceAction.Ingredient_Save);

		//TODO: Later we should already have connections defined and just use them. 
		//      For now, we'll get the connection each time. 
		provider.showConnDialog();
		// 2. If new ingredient, create insert statement, // TODO: Write routine to check for existing
		// 3. otherwise, create update statement.
		// 4. Write data to the table.
		try {
			provider.doSave();
			// 5. Close the connection.
			provider.closeConnection();
			//TODO: Add new ingredient to the tree list: IngredientExplorerView
			makeDirty(false);
		} catch (SQLException e) {
			javaLogger.log(Level.SEVERE, "An error occurred while trying to save an ingredient. \n " +
				e.getErrorCode() + " " + e.getMessage() );
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
	
	public void makeDirty(boolean b) {
		dirty = true;
		super.firePropertyChange(PROP_DIRTY);
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
