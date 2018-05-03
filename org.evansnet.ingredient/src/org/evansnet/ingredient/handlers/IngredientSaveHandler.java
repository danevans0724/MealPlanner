package org.evansnet.ingredient.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.progress.IProgressService;

/**
 * Handles requests to save the ingredient to the database.
 * 
 * @author pmidce0
 *
 */
public class IngredientSaveHandler extends AbstractHandler {
	
	IProgressMonitor monitor;
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		System.out.println("Saving an ingredient!");
		// TODO Activate the progress monitor in the GUI, (this), thread. Ref: eclipse JOB framework.
		
		monitor = SubMonitor.convert(monitor);
		// 1. Insure the editor is open. Else error message & clear state
		HandlerUtil.getActiveEditor(event).doSave(monitor);;
		// 2. Create an ingredient persister and pass the ingredient to it. 
		// 3. The ingredient persister writes the ingredient to disk and reports success / fail.
		// 4. 
		return null;
	}

}
