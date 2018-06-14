package org.evansnet.ingredient.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Handles requests to save the ingredient to the database.
 * 
 * @author Dan Evans
 *
 */
public class IngredientSaveHandler extends AbstractHandler {
	
	IProgressMonitor monitor;
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// TODO Activate the progress monitor in the GUI, (this), thread. Ref: eclipse JOB framework.
		
		monitor = SubMonitor.convert(monitor);
		HandlerUtil.getActiveEditor(event).doSave(monitor);
		return null;
	}

}
