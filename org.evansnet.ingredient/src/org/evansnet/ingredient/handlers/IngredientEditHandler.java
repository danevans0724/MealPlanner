package org.evansnet.ingredient.handlers;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
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
	public static final String ID = THIS_CLASS_NAME;
	public static Logger javaLogger = Logger.getLogger(THIS_CLASS_NAME);
	
	IngredientInput input;
	
	public IngredientEditHandler(IngredientInput i) {
		input = i;
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage activePage = window.getActivePage();
		//We have the window, now open and fill it.
		try {
	 		if (!(input == null)) {
				IEditorPart editor = activePage.getActiveEditor();
				if (editor == null) {
					Ingredient i = input.getIngredient();
					editor = new IngredientEditor(i);	
				}
				activePage.openEditor(input, IngredientEditor.ID);
			} else {
				String message = new String("ERROR! Input for the editor is empty!");
				javaLogger.logp(Level.SEVERE, THIS_CLASS_NAME, "execute()", 
						message);
				throw new Exception(message);
			}
		} catch (Exception e) { 
			javaLogger.logp(Level.SEVERE, THIS_CLASS_NAME, "execute()", e.toString());
		}
		return null;
	}
}
