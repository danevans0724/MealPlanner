package org.evansnet.ingredient.ui;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.evansnet.ingredient.model.Ingredient;
import org.evansnet.ingredient.persistence.IngredientPersistenceAction;
import org.evansnet.ingredient.persistence.PersistenceProvider;


/**
 * The editor for Ingredients. 
 * 
 * @author pmidce0
 *
 */
public class IngredientEditor extends EditorPart {
	
	public static final String ID = "org.evansnet.ingredient.ingredienteditor";	
	public static Logger javaLogger = Logger.getLogger("Ingredient editor logger");
	Ingredient ingredient;
	IngredientCompositeBase ingEditorComposite;
	boolean dirty = false;

	public IngredientEditor() {
		super();
		ingredient = new Ingredient();
	}
	
	public IngredientEditor(Ingredient i) {
		this();
		setIngredient(i);
	}
	
	@Override
	public void doSave(IProgressMonitor monitor)  {
		PersistenceProvider provider;
		try {
			provider = new PersistenceProvider( ingEditorComposite.getIngredient(),
					IngredientPersistenceAction.Ingredient_Save);
			provider.doSave();
			//TODO: Add new ingredient to the tree list: IngredientExplorerView
			// since we have a repository, we should be able to fire an event to update the tree.
			makeDirty(false);
		} catch (SQLException  e) {
			javaLogger.log(Level.SEVERE, "An SQL Exception occurred while trying to save an ingredient. \n " +
				e.getErrorCode() + " " + e.getMessage() );
			e.printStackTrace();
		} catch (Exception e) {
			javaLogger.logp(Level.SEVERE, ID, "doSave()", 
					"An unexpected exception occurred during save. " + e.getMessage());
			e.printStackTrace(); 
		}
	}

	@Override
	public void doSaveAs() {
		// Do nothing for now.
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		if (!(input instanceof IngredientInput) || input == null) {
			throw new RuntimeException("IngredientEditor; Wrong input!!");
		}
		this.ingredient = ((IngredientInput) input).getIngredient();
		setSite(site);
		setInput(input);
	}

	@Override
	public boolean isDirty() {
		return dirty;
	}
	
	public void setIngredient(Ingredient i) {
		ingredient = i;
	}
	
	public void makeDirty(boolean b) {
		dirty = b;
		firePropertyChange(IEditorPart.PROP_DIRTY);
	}

	@Override
	public boolean isSaveAsAllowed() {
		// Disable save-as for now. Each ingredient should be saved as itself.
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {
		ingEditorComposite = new IngredientCompositeBase(parent, SWT.NONE, ingredient);		
	}

	@Override
	public void setFocus() {
		ingEditorComposite.setFocus();
	}
	
}
