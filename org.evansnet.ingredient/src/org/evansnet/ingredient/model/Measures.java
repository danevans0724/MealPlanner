package org.evansnet.ingredient.model;

import java.util.ArrayList;

/**
 * Provides a list of the kitchen units of measure used in 
 * the description of ingredients and ultimately recipes.
 * 
 * Note: This is a temporary class that will be used until the "measure" 
 *       plug-in has been fully developed.
 * 
 * @author Dan Evans
 *
 */
public class Measures implements Measure {
	
	ArrayList<String> measures;
	
	public Measures() {
		measures = new ArrayList<String>();
		measures.add("");				//For self unitized ingredients. Ex. 1 egg. The egg is its own unit.
		measures.add("teaspoon");
		measures.add("tablespoon");
		measures.add("Cup");
		measures.add("ounce");
		measures.add("Pound");
		measures.add("each");
	}
	
	public ArrayList<String> getMeasures() {
		return measures;
	}
	
	/**
	 * Gets the index of the measure with the name provided if it exists.
	 * @param m A string representing the measure to search for.
	 * @return An integer representing the index of the measure in the list or -1 if the
	 * measure doesn't exist.
	 */
	public int getMeasure(String m) {
		
		if (measures.contains(m)) {
			return measures.indexOf(m);
		}
		return -1;		// returned if no measure with name "m" exists.
	}
}
