package org.evansnet.ingredient.model;

import java.beans.*;
import java.math.BigDecimal;

/**
 * Ingredient defines a base foodstuff that is used as a component for a
 * recipe. An ingredient can be a base, like flour, or can be a recipe in
 * itself, like pie dough. 
 * @author Dan Evans
 *
 */
public class Ingredient {
	
	int id;
	String ingredientName;
	String ingredientDescription;
	String strUom;			// A temporary class for test. TODO: Replace with UOM when written.
	String pkgUom;
	BigDecimal unitPrice;
	BigDecimal pkgPrice;
	boolean isRecipe;		// TODO: Add the recipe class reference when Recipe is written.
	
	
	
	private PropertyChangeSupport pcs = 
			new PropertyChangeSupport(this);
	
	// Getters & setters
	public int getID() {
		return id;
	}
	
	public void setID(int i) {
		id = i;
	}
	
	public String getIngredientName() {
		return ingredientName;
	}
	public void setIngredientName(String name) {
		String oldIngName = ingredientName;
		ingredientName = name;
		pcs.firePropertyChange("ingredientName", oldIngName, ingredientName);
	}
	public String getIngredientDescription() {
		return ingredientDescription;
	}
	public void setIngredientDescription(String desc) {
		String oldDesc = ingredientDescription;
		ingredientDescription = desc;
		pcs.firePropertyChange("ingredientDescription", oldDesc, ingredientDescription);
	}
	public String getStrUom() {
		return strUom;
	}
	public void setStrUom(String newUom) {
		String oldUom = strUom;
		this.strUom = newUom;
		pcs.firePropertyChange("strUom", oldUom, strUom);
	}
	public String getPkgUom() {
		return pkgUom;
	}
	public void setPkgUom(String newUom) {
		String oldPkgUom = pkgUom;
		this.pkgUom = newUom;
		pcs.firePropertyChange("pkgUom", oldPkgUom, pkgUom);
	}
	public BigDecimal getUnitPrice() {
		return unitPrice;
	}
	public void setUnitPrice(BigDecimal p) {
		BigDecimal oldUnitPrice = unitPrice;
		this.unitPrice = p;
		pcs.firePropertyChange("unitPrice", oldUnitPrice, unitPrice);
	}
	public BigDecimal getPkgPrice() {
		return pkgPrice;
	}
	public void setPkgPrice(BigDecimal p) {
		BigDecimal oldPkgPrice = pkgPrice;
		this.pkgPrice = p;
		pcs.firePropertyChange("pkgPrice", oldPkgPrice, pkgPrice);
	}
	public boolean isRecipe() {
		return isRecipe;
	}
	public void setRecipe(boolean b) {
		boolean oldIsRecipe = isRecipe;
		this.isRecipe = b;
		pcs.firePropertyChange("isRecipe", oldIsRecipe, isRecipe);
	}


	public void addProertyChangeListener(PropertyChangeListener l) {
		pcs.addPropertyChangeListener(l);
	}
	
	public void removePropertyChangeListener(PropertyChangeListener l) {
		pcs.removePropertyChangeListener(l);
	}
	
}
