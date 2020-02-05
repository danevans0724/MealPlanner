package org.evansnet.ingredient.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.evansnet.ingredient.model.Ingredient;
import org.evansnet.ingredient.ui.IngredientEditor;
import org.evansnet.ingredient.ui.IngredientExplorerView;
import org.evansnet.ingredient.persistence.IPersistenceProvider;
import org.evansnet.ingredient.persistence.IngredientPersistenceAction;
import org.evansnet.ingredient.persistence.IngredientProvider;

public class IngredientDeleteHandler extends AbstractHandler {

	IProgressMonitor monitor;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// Remove the ingredient from the repository and close the editor.
		IStructuredSelection treeSelection;
		monitor = SubMonitor.convert(monitor);		//TODO: Implement the job framework to free the UI thread.
		IngredientEditor editor = (IngredientEditor)HandlerUtil.getActiveEditor(event);
		if (editor == null) {
			// The event likely comes from the treeviewer context menu.
			IWorkbenchPart part = HandlerUtil.getActivePart(event);
			if (part instanceof IngredientExplorerView) {
				treeSelection = ((IngredientExplorerView) part).getTreeSelection();
				String ingName = treeSelection.getFirstElement().toString();
				Ingredient ingredient = (Ingredient) ((IngredientExplorerView) part).getRepo().fetchByName(ingName).get(0);
				try {
					IPersistenceProvider provider = new IngredientProvider(ingredient, IngredientPersistenceAction.Ingredient_Delete);
					provider.doDelete(ingredient.getID());
					((IngredientExplorerView)part).refresh(true);
				} catch (Exception e) {
					String message = "An error occured while attempting to delete an ingredient.";
					throw new ExecutionException(message);
				}
			} else {
				String message = "An unexpected source was received during a request to delete an ingredient.";
				throw new ExecutionException(message);
			}
		} else {
			editor.doDelete();
			// Close the editor to prevent further action on the deleted ingredient. 
			editor.getEditorSite().getPage().closeEditor(editor, false);
		}
		return null;
	}

}
