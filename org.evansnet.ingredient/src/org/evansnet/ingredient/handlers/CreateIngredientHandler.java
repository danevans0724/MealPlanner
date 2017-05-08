package org.evansnet.ingredient.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.evansnet.ingredient.app.Perspective;
import org.evansnet.ingredient.ui.IngredientEditor;
import org.evansnet.ingredient.ui.IngredientInput;

/**
 * Handler for creation of an ingredient.
 * 
 * @author pmidce0
 *
 */
public class CreateIngredientHandler extends AbstractHandler {
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		System.out.println("Creating an ingredient!");
		// TODO Open the editor with the editing composite within it.
		IWorkbenchWindow ingEditorWindow = HandlerUtil.getActiveWorkbenchWindow(event);
		IWorkbenchPage thisPage = ingEditorWindow.getActivePage();
		IngredientInput input = new IngredientInput();
		
		try {
			thisPage.openEditor(input, IngredientEditor.ID);
		} catch (WorkbenchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}


}
