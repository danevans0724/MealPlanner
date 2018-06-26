package org.evansnet.ingredient.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.part.ViewPart;
import org.evansnet.ingredient.handlers.IngredientEditHandler;
import org.evansnet.ingredient.model.Ingredient;
import org.evansnet.ingredient.persistence.repository.IRepository;
import org.evansnet.ingredient.persistence.repository.IngredientRepository;
import org.evansnet.ingredient.ui.providers.IngredientTreeContentProvider;
import org.evansnet.ingredient.ui.providers.IngredientTreeLabelProvider;

/**
 * Provides a tree view for navigating Ingredient types and ingredients in 
 * the system. 
 * @author pmidce0
 *
 */
public class IngredientExplorerView extends ViewPart {
	public static final String ID = "org.evansnet.ingredient.ui.ingredientexplorerview";
	
	public static final String THIS_CLASS_NAME = "org.evansnet.ingredient.ui.IngredientExplorerView";	
	Logger javaLogger = Logger.getLogger(THIS_CLASS_NAME);
	
	TreeViewer treeviewer;
	IRepository repo;
	ISelectionService selectionService;
	
	
	public IngredientExplorerView() {
		try {
			repo = new IngredientRepository();
			repo.setConnectStr(repo.fetchDefaultRepo());	// TODO: Allow pointing to a different repo through the interface
			javaLogger.logp(Level.INFO, THIS_CLASS_NAME, "IngredientRepositoryView()", repo.getRepoName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		javaLogger.logp(Level.INFO, THIS_CLASS_NAME, "createPartControl", 
				"Creating ingredient explorer view.");
		treeviewer = new TreeViewer(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		treeviewer.setLabelProvider(new IngredientTreeLabelProvider());
		treeviewer.setContentProvider(new IngredientTreeContentProvider());
		treeviewer.setInput(repo);
		
		// Register a context menu for ingredient edit and delete from the tree viewer.
		MenuManager menuMgr = new MenuManager();
		Menu popupMnu = menuMgr.createContextMenu(treeviewer.getTree());
		treeviewer.getTree().setMenu(popupMnu);
		getSite().registerContextMenu(menuMgr, treeviewer);

		/**
		 * This listener allows for the selection of an ingredient and its display in 
		 * the editor. Once in the editor, it can be edited or deleted. 
		 */
		treeviewer.addDoubleClickListener(event -> {
			Viewer viewer = event.getViewer();
			IStructuredSelection sel = (IStructuredSelection) viewer.getSelection();
			if (!(sel.isEmpty()) && sel instanceof IStructuredSelection) {
				Object ing = ((IStructuredSelection)sel).getFirstElement();
				if (ing instanceof String) {
					List<Object> selectedIng = repo.fetchByName((String)ing);
					Map<String, String> parms = new HashMap<String, String>();
						IngredientInput input = new IngredientInput((Ingredient)selectedIng.get(0)); 

						parms.put("input", input.getName());
						if(!(ing == null)) {
							ICommandService cmdSrv = this.getViewSite().getWorkbenchWindow()
									.getService(ICommandService.class);
							Command cmd = cmdSrv.getCommand("org.evansnet.ingredient.command.ingredient.edit");
							ExecutionEvent editEvent = new ExecutionEvent(cmd, parms, this, null);
							try {
								cmd.setHandler(new IngredientEditHandler(input));
								cmd.executeWithChecks(editEvent);
							} catch (Exception e) {
								javaLogger.logp(Level.SEVERE, THIS_CLASS_NAME, "treeviewer.addDoubleClickListener()", 
										"An exceptionn occured while trying to open the ingredient editor " + e.getMessage());
								e.printStackTrace();
							}
					}
				}
			}
		});
	}

	@Override
	public void setFocus() {
	}

	/**
	 * Sets the ingredient repository to the one specified in the input parameter.
	 * @param r The ingredient repository to use.
	 */
	public void setRepository(IRepository r) {
		repo = r;
		refresh(true);
	}

	/**
	 * Update the nodes in the tree for add & remove operations.
	 * @param updateLabels 
	 */
	public void refresh(boolean updateLabels) {
		treeviewer.refresh(updateLabels);
	}
	
	public IRepository getRepo() {
		return repo;
	}

	public void setRepo(IRepository repo) {
		this.repo = repo;
	}
	
	public void removeRepoItem(int i) throws Exception {
		String nodeToRemove = ((Ingredient)(repo.fetchById(i))).getIngredientName();
		treeviewer.remove(nodeToRemove);
		repo.removeMappedItem(i);
		refresh(true);
	}

	/**
	 * Adds an item to the repository map and refreshes the tree to add the item. 
	 * Note that at this point the item has already been added to the repository table. 
	 * @param ingredient
	 * @throws Exception 
	 */
	public void addRepoItem(Ingredient ingredient) throws Exception {
		@SuppressWarnings("unused")
		Map<Integer, Object> dummy = repo.fetchAll();	// Update the repository. The variable is a dummy. We just want to refresh the map.
		dummy = null;
		refresh(true);
	}
	
	public IStructuredSelection getTreeSelection() {
		return treeviewer.getStructuredSelection();
	}
}
