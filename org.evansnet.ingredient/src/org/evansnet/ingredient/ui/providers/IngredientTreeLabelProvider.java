package org.evansnet.ingredient.ui.providers;


import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jface.viewers.LabelProvider;
import org.evansnet.ingredient.model.Ingredient;
import org.evansnet.ingredient.persistence.repository.IngredientRepository;

public class IngredientTreeLabelProvider extends LabelProvider {
	
	public static final String THIS_CLASS_NAME = IngredientTreeLabelProvider.class.getName();
	public Logger javaLogger = Logger.getLogger(THIS_CLASS_NAME);
	
	/**
	 * Get the ingredient names for the ingredient explorer tree.
	 * @param element An object that provides the tree node string.
	 * @return The name of the ingredient as a string.
	 */
	@SuppressWarnings("rawtypes")
	public String getText(Object element) {
		if (element instanceof IngredientRepository) {
			return "Ingredients";
		} else if (element instanceof Object[]) {
			String i = ((Ingredient)element).getIngredientName();
			return i;
		} else if (element instanceof String) {
			return (String)element;
		} else
			javaLogger.logp(Level.FINE, THIS_CLASS_NAME, "getText()", 
					"Invalid value, returning null");
		return "Sorry didn't work " + element.getClass();			
	}
}
