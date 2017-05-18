package org.evansnet.ingredient.handlers;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.evansnet.ingredient.ui.IngredientEditor;
import org.evansnet.ingredient.ui.IngredientInput;

/**
 * Handler for creation of an ingredient.
 * 
 * @author pmidce0
 *
 */
public class CreateIngredientHandler extends AbstractHandler {
	
	public final Logger javaLogger = Logger.getLogger("CreateIngredientLogger");
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		javaLogger.log(Level.INFO, "Opening ingredient editor");
		IWorkbenchWindow ingEditorWindow = HandlerUtil.getActiveWorkbenchWindow(event);
		IWorkbenchPage thisPage = ingEditorWindow.getActivePage();
		IngredientInput input = new IngredientInput();
		
		try {
			thisPage.openEditor(input, IngredientEditor.ID);
		} catch (WorkbenchException e) {
			javaLogger.log(Level.SEVERE, "Unable to open ingredient creation editor." 
					+ e.getMessage());
			e.printStackTrace();
		}
		
		return null;
	}


}
