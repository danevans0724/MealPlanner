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
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;
import org.evansnet.ingredient.model.Ingredient;
import org.evansnet.ingredient.persistence.IPersistenceProvider;
import org.evansnet.ingredient.persistence.IngredientPersistenceAction;
import org.evansnet.ingredient.persistence.IngredientProvider;


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
		IPersistenceProvider provider;
		try {
			provider = new IngredientProvider( ingEditorComposite.getIngredient(),
					IngredientPersistenceAction.Ingredient_Save);
			int id = provider.doSave(ingredient);
			ingredient.setID(id);
			IngredientExplorerView explorerView = getIngredientExplorerView();
			explorerView.addRepoItem(ingredient);
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
	
	public void doDelete() {
		IPersistenceProvider provider;
		try {
			provider = new IngredientProvider(ingEditorComposite.getIngredient(), 
					IngredientPersistenceAction.Ingredient_Delete);
			int id = provider.doDelete(ingredient);
			ingredient.setID(id);      //This is necessary because if a user deletes a new ingredient from the editor it will have an id=0;
			//Refresh the tree view to remove the deleted ingredient.
			IngredientExplorerView explorerView = getIngredientExplorerView();
			explorerView.removeRepoItem(ingredient.getID());		//Remove the item from the mappend list of ingredients
			explorerView.getRepo().removeMappedItem(ingredient.getID());	//Remove the item from the tree.
			makeDirty(false);
		} catch (Exception e) {
			javaLogger.logp(Level.SEVERE, ID, "doDelete()", "An Exception occurred while trying to delete an ingredient. \n" +
		       e.getMessage());
		}
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

	public IngredientCompositeBase getIngEditorComposite() {
		return ingEditorComposite;
	}

	public void setIngEditorComposite(IngredientCompositeBase ingEditorComposite) {
		this.ingEditorComposite = ingEditorComposite;
	}
	
	private IngredientExplorerView getIngredientExplorerView() {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IViewReference viewReference = page.findViewReference("org.evansnet.ingredient.ui.ingredientexplorerview");
		IngredientExplorerView explorerView = (IngredientExplorerView) (viewReference.getView(true));
		return explorerView;
	}
}
