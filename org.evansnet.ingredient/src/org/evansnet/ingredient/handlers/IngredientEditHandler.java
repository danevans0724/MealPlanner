package org.evansnet.ingredient.handlers;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.evansnet.ingredient.model.Ingredient;
import org.evansnet.ingredient.ui.IngredientEditor;
import org.evansnet.ingredient.ui.IngredientExplorerView;
import org.evansnet.ingredient.ui.IngredientInput;


/**
 * Allows for changes to an ingredient. Primarily intended to handle 
 * ingredients that are double clicked from the ingredient explorer tree. 
 * 
 *  @author pmidce0
 *
 */
public class IngredientEditHandler extends AbstractHandler {
	
	public static final String THIS_CLASS_NAME = IngredientEditHandler.class.getName();
	public static final String ID = THIS_CLASS_NAME;
	public static Logger javaLogger = Logger.getLogger(THIS_CLASS_NAME);
	
	IngredientInput input;
	
	public IngredientEditHandler() {}
	
	public IngredientEditHandler(IngredientInput i) {
		input = i;
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection treeSelection;
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPart part = window.getActivePage().getActivePart();	//HandlerUtil.getActivePart(event);
		
		//We have the window, now open and fill the editor.
		try {
				if (part instanceof IngredientExplorerView) {
					// The request has come from the tree. 
					treeSelection = ((IngredientExplorerView) part).getTreeSelection();
					String ingName = (String) treeSelection.getFirstElement();	//Gives the ingredient name.
					Ingredient ingredient = (Ingredient) ((IngredientExplorerView) part).getRepo().fetchByName(ingName).get(0);
					input.setIngredient(ingredient);
				} else {
					String message = "An unknown source was received for editing!";
					javaLogger.logp(Level.SEVERE, THIS_CLASS_NAME, "execute()", message);
					throw new ExecutionException(message);
				}
			window.getActivePage().openEditor(input, IngredientEditor.ID, true);
		} catch (Exception e) { 
			javaLogger.logp(Level.SEVERE, THIS_CLASS_NAME, "execute()", e.toString());
		}
		return null;
	}
}
