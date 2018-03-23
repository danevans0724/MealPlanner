package org.evansnet.ingredient.ui.providers;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.evansnet.ingredient.model.Ingredient;
import org.evansnet.ingredient.persistence.repository.IngredientRepository;

public class IngredientTreeContentProvider implements ITreeContentProvider {
	
	public static final String THIS_CLASS_NAME = IngredientTreeContentProvider.class.getName();
	public Logger javaLogger = Logger.getLogger(THIS_CLASS_NAME);
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public Object[] getElements(Object inputElement) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Object[] getChildren(Object parentElement) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Object getParent(Object element) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean hasChildren(Object element) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
