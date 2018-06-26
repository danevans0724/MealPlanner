package org.evansnet.ingredient.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.evansnet.ingredient.ui.IngredientExplorerView;

public class RefreshHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchPart part = HandlerUtil.getActivePart(event);
		
		if (part instanceof IngredientExplorerView) {
			//Refresh the tree.
			((IngredientExplorerView)part).refresh(true);
		} 
		return null;
	}


}
