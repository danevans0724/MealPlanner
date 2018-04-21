package org.evansnet.ingredient.handlers;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.evansnet.ingredient.model.Ingredient;
import org.evansnet.ingredient.ui.IngredientEditor;
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
	public static Logger javaLogger = Logger.getLogger(THIS_CLASS_NAME);
	
	IngredientInput input;
	
	public IngredientEditHandler(IngredientInput i) {
		input = i;
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		IWorkbenchPage thisPage = window.getActivePage();
		
		try {
			if (input == null) {
				String message = new String("ERROR! Input for the editor is empty!");
				javaLogger.logp(Level.SEVERE, THIS_CLASS_NAME, "execute()", 
						message);
				throw new Exception(message);
			} else {
				thisPage.openEditor(input, IngredientEditor.ID);
			}
		} catch (Exception e) {
			//Already threw it. 
			javaLogger.logp(Level.SEVERE, THIS_CLASS_NAME, "execute()", e.toString());
		}
		return null;
	}


}
