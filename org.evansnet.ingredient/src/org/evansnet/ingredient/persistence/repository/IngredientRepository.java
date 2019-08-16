package org.evansnet.ingredient.persistence.repository;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.evansnet.dataconnector.internal.core.DBType;
import org.evansnet.ingredient.model.Ingredient;


/**
 * Implements the usage of the ingredient repository. 
 * The repository is a database table that stores the ingredients 
 * created by the user. 
 * 
 * @author Dan Evans
 *
 */
public class IngredientRepository extends RepositoryImpl implements IRepository {	
	
	public final String INGREDIENT = "INGREDIENT";
	
	public IngredientRepository() {
		super();
		setTableName(INGREDIENT);
	}
	
	/**
	 * Retrieve a hash map containing the ingredients stored in the repository table. This
	 * is used, among other things, to populate the tree in the ingredient explorer
	 * view and fill the list of ingredient types in the ingredient editor.
	 * 
	 * @return A list of the ingredients contained in the repository tree.
	 */
	public HashMap<Integer, Object> getTreeIngredients() {
		contents.clear();
			try {
				fetchAll();
				return  contents;
			} catch (Exception e) {
				javaLogger.logp(Level.SEVERE, THISCLASSNAME, "getTreeIngredients()" ,
						"An exception was thrown when retrieving ingredients from the repository " +
						e.getMessage());
				e.printStackTrace();
			}
		return null;
	}
	
	@Override
	public Map<Integer, Object> fetchAll() throws Exception {
		contents.clear();
		Connection conn = connect();
		String s = "SELECT * FROM " + repo.getSchema() + "." + INGREDIENT;
		Statement stmt = null;
		stmt = conn.createStatement();
		resultSet = stmt.executeQuery(s);
	
		while (resultSet.next()) {
			Ingredient i = new Ingredient();
			i.setID(resultSet.getInt("ID"));
			i.setIngredientName(resultSet.getString("ING_NAME"));
			i.setIngredientDescription(resultSet.getString("ING_DESC"));
			i.setStrUom(resultSet.getString("UNIT_OF_MEASURE"));
			i.setPkgUom(resultSet.getString("PKG_UOM"));
			i.setUnitPrice(resultSet.getBigDecimal("UNIT_PRICE"));
			i.setPkgPrice(resultSet.getBigDecimal("PKG_PRICE"));
			i.setIsRecipe(resultSet.getBoolean("IS_RECIPE"));
			i.setIngredientType(resultSet.getInt("ING_TYPE"));
		contents.put(i.getID(), i);
		}
		conn.close();
		return contents;
	}

	@Override
	public List<Object> fetchByName(String n) {
		List<Object> result = new ArrayList<>();
			try {
				fetchAll();
			} catch (Exception e) {
				javaLogger.logp(Level.INFO, THISCLASSNAME, "fetchName()", 
						"Fetching " + getTableName() + " list from the repository.");
			}
		for (Object i : contents.values()) {
			if (((Ingredient)i).getIngredientName().equals(n)) {
				result.add(i);
			}
		}
		return result;
	}
	
	@Override
	public int doInsertNew(Object ing) throws SQLException {
		int rowsInserted = 0;
		int id = getNextID();
		Ingredient i = (Ingredient) ing;
		StringBuilder insert = new StringBuilder("INSERT INTO ");
		insert.append(repo.getSchema() + ".");
		insert.append(getTableName() + " VALUES(");
		insert.append(id + ", ");
		insert.append("'" + i.getIngredientName() + "', ");
		insert.append("'" + i.getIngredientDescription() + "', ");
		insert.append("'" + i.getStrUom() + "', " );
		insert.append("'" + i.getPkgUom() + "', ");
		insert.append("'" + i.getUnitPrice() + "', ");
		insert.append("'" + i.getPkgPrice() + "', ");
		if (i.isRecipe()) {
			if (repo.getDBMS().equals(DBType.MS_SQLSrv)) {
				insert.append("1"); 
			} else if (repo.getDBMS().equals(DBType.MySQL)) {
				insert.append("true");
			}
		} else {
			if (repo.getDBMS().equals(DBType.MS_SQLSrv)) {
				insert.append("0"); 
			} else if (repo.getDBMS().equals(DBType.MySQL)) {
				insert.append("false");
			}			
		}
		insert.append(", " + i.getIngredientType() + ")");
		javaLogger.logp(Level.INFO, THISCLASSNAME, "doInsertNew()",
				"Insert statement completed: \n" + insert.toString());
		javaLogger.logp(Level.INFO, THISCLASSNAME, "doInsertNew()",
				"Connecting to the repository database.");
		Connection con = connect();
		Statement stmt = con.createStatement();
		try {
			javaLogger.logp(Level.INFO, THISCLASSNAME, "doInsertNew()",
					"Executing insert query");
			rowsInserted = stmt.executeUpdate(insert.toString());
			if (rowsInserted > 0) {
				//The insert was successful so add the ingredient to the map.
				contents.put(i.getID(), i);
			javaLogger.logp(Level.INFO, THISCLASSNAME, "doInsertNew()",
					"Inserted " + rowsInserted + " rows successfully.");
			}
		} catch (Exception e) {
			javaLogger.logp(Level.SEVERE, THISCLASSNAME, "doInsertNew()",
					"Failed to insert ingredient record. Rows inserted:  " + rowsInserted +
					"\n" + e.getMessage());
			e.printStackTrace();
			throw new SQLException(e.getMessage());
		} finally {
			con.close();
		}
		return id;
	}

	@Override
	public int doUpdate(Object ing) throws SQLException {
		int rowsUpd;
		Ingredient i = (Ingredient) ing;
		StringBuilder update = new StringBuilder();
		update.append("UPDATE " + repo.getSchema() + ".INGREDIENT SET ");
		update.append("ID=" + i.getID() + ", ");
		update.append("ING_NAME=\'" + i.getIngredientName() + "\', ");
		update.append("ING_DESC=\'" + i.getIngredientDescription() + "\', ");
		update.append("UNIT_OF_MEASURE=" + i.getStrUom() + ", ");
		update.append("PKG_UOM=" + i.getPkgUom() + ", ");
		update.append("UNIT_PRICE=" + i.getUnitPrice() + ", ");
		update.append("PKG_PRICE=" + i.getPkgPrice() + ", ");
		if(i.isRecipe()) {
			if (repo.getDBMS().equals(DBType.MS_SQLSrv)) { 
				update.append("IS_RECIPE=1");				
			} else if (repo.getDBMS().equals(DBType.MySQL)) {
				update.append("IS_RECIPE=true");
			}
		} else {
			if (repo.getDBMS().equals(DBType.MS_SQLSrv)) {
				update.append("IS_RECIPE=0");				
			} else if (repo.getDBMS().equals(DBType.MySQL)) {
				update.append("IS_RECIPE=false");
			}
		}
		update.append(", ING_TYPE=" + i.getIngredientType());
		update.append(" WHERE ID=" + i.getID());
		
		javaLogger.logp(Level.INFO, THISCLASSNAME, "doUpdate()",
				"Update query constructed: \n" + update.toString());
		
		javaLogger.logp(Level.INFO, THISCLASSNAME, "doUpdate()", "Connecting to repository table. ");
		Connection conn = connect();
		Statement s = conn.createStatement();
		javaLogger.logp(Level.INFO, THISCLASSNAME, "doUpdate()", 
				"Executing update query.");
		try {
			rowsUpd = s.executeUpdate(update.toString());
			contents.put(i.getID(), ing);	//Add the new item to the mapped list of repository items.
			javaLogger.logp(Level.INFO, THISCLASSNAME, "doUpdate()", "Successfully updated record.");
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new SQLException("An error occurred while updating ingredient with ID " + i.getID());
		} finally {
			conn.close();
		}
		return rowsUpd;
	}

}
