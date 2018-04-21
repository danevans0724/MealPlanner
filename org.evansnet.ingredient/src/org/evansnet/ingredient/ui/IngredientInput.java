package org.evansnet.ingredient.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.evansnet.ingredient.model.Ingredient;

import org.evansnet.ingredient.model.Ingredient;

/**
 * This input object connects the editorPart with the Ingredient model.
 * 
 * @author pmidce0
 *
 */
public class IngredientInput implements IEditorInput {
	
	private Ingredient ingredient;
	
	//constructors
	/**
	 * Default constructor. Creates a new instance of the ingredient class for 
	 * the purpose of creation. 
	 */
	public IngredientInput() { ingredient = new Ingredient();}
	
	/**
	 * This constructor takes an existing ingredient as a parameter. It is
	 * used primarily for editing or displaying ingredients that have already
	 * been defined. 
	 * @param i : A previously created ingredient object from the repository or 
	 * other source.
	 */
	public IngredientInput(Ingredient i) {
		ingredient = i;
	}
	
	public boolean equals(Ingredient i1, Ingredient i2) {
		if (i1.equals(i2)) {
			return true;
		}
		return false;
	}

	@Override
	public Object getAdapter(Class adapter) {
		// TODO Auto-generated method stub
		return null;
	}
	

	@Override
	public boolean exists() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		if (ingredient != null) {
			return ingredient.getIngredientName();
		} else {
			return "New Ingredient";
		}
	}

	@Override
	public IPersistableElement getPersistable() {
		// TODO Determine if IngredientInput.getPersistable() should be implemented.
		return null;
	}

	@Override
	public String getToolTipText() {
		return "The ingredient to create or edit.";
	}


}
