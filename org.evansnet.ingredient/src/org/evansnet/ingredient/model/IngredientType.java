package org.evansnet.ingredient.model;


/**
 * Describes the different types of ingredients that a user may want to 
 * define. "Fruits", "Vegetable", "Fresh vegetable", "Dry goods", etc.
 * Each type is identified with a unique ID value. 
 * 
 * @author pmidce0
 *
 */
public class IngredientType {
	
	public static int lastID;	//TODO: How do we keep track of the last ID from run to run?
	int typeID;
	String typeName;
	String typeDesc;
	
	public IngredientType() {
		typeID = -1;
		typeName = new String();
		typeDesc = new String();
	}
	
	public IngredientType (int id, String name, String d) {
		typeID = id;
		typeName = new String(name);
		typeDesc = new String(d);
	}
	
	
	// Getters and setters.
	
	public int getTypeID() {
		return typeID;
	}

	public void setTypeID(int typeID) {
		this.typeID = typeID;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getTypeDesc() {
		return typeDesc;
	}

	public void setTypeDesc(String typeDesc) {
		this.typeDesc = typeDesc;
	}
	
	// Methods
	
	private int generateID() {
		//TODO: implement the method to generate a unique ingredient ID. 
		return -1;
	}

	/**
	 * After check of the id by an ingredient type list, warns the user
	 * that the ID is already in use or is not an integer value.
	 * 
	 */
	public void illegalID() {
		// TODO: implement illegalID() in class IngredientType
	}
}
