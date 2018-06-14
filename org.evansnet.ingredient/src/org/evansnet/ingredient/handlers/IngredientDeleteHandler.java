package org.evansnet.ingredient.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.ui.handlers.HandlerUtil;
import org.evansnet.ingredient.ui.IngredientEditor;

public class IngredientDeleteHandler extends AbstractHandler {

	IProgressMonitor monitor;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// Remove the ingredient from the repository and close the editor.
		monitor = SubMonitor.convert(monitor);
		IngredientEditor editor = (IngredientEditor)HandlerUtil.getActiveEditor(event);
		editor.doDelete();
		// Close the editor to prevent further action on the deleted ingredient. 
		editor.getEditorSite().getPage().closeEditor(editor, false);
		return null;
	}

}
