/**
 * 
 */
package org.evansnet.ingredient.persistence.repository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.evansnet.ingredient.model.IngredientType;

/**
 * @author pmidce0
 *
 */
public class IngredientTypeRepository extends RepositoryImpl {

	public static final String THIS_CLASS_NAME = IngredientTypeRepository.class.getName();
	public Logger javaLogger = Logger.getLogger(THIS_CLASS_NAME);
	
	IngredientType type;

	public IngredientTypeRepository () {
		super();
		setTableName("INGREDIENT_TYPE");
	}
	
	/* (non-Javadoc)
	 * @see org.evansnet.ingredient.persistence.repository.IRepository#getRepoVersion()
	 */
	@Override
	public String getRepoVersion() {
		return "1.0";
	}
	
	@Override
	public Map<Integer, Object> fetchAll() throws Exception {
		Connection conn = connect();
		String select = "SELECT * FROM " + getTableName() + ";";
		Statement stmt = conn.createStatement();
		ResultSet resultset = stmt.executeQuery(select);
		
		while (resultset.next()) {
			IngredientType type = new IngredientType();
			type.setTypeID(resultset.getInt("ID"));
			type.setTypeName(resultset.getString("ING_TYPE_NAME"));
			type.setTypeDesc(resultset.getString("TYPE_DESCR"));
			contents.put(resultset.getInt("ID"), type);
		}
		conn.close();
		javaLogger.logp(Level.INFO, THIS_CLASS_NAME, "fetchAll()", "Connection to database has been closed.");
		return contents;
	}

	@Override
	public List<Object> fetchByName(String n) {
		List<Object> result = new ArrayList<>();
		if (contents.isEmpty()) {
			try {
				fetchAll();
			} catch (Exception e) {
				String message = "An exception occurred while fetching " + getTableName() + " " + n + ".\n";
				javaLogger.logp(Level.SEVERE, THIS_CLASS_NAME, " fetchByName() ", message + e.getMessage());
			}
		} 
		for (Object o : contents.values()) {
			if (((IngredientType) o).getTypeName().equals(n)) {
				result.add(o);
			}
		}
		return result;
	}

	@Override
	public int doInsertNew(Object ing) throws SQLException {
		int rowsInserted = -1;
		StringBuilder query = new StringBuilder("INSERT INTO " + getTableName() + " VALUES (");
		type = (IngredientType)ing;
		query.append(type.getTypeID() + ", ");
		query.append("'" + type.getTypeName() + "', ");
		query.append("'" + type.getTypeDesc() + "');");
		
		Connection conn = connect();
		Statement stmt = conn.createStatement();
		rowsInserted = stmt.executeUpdate(query.toString());
		javaLogger.logp(Level.INFO, THIS_CLASS_NAME, "doInsertNew()", 
				"Inserted " + rowsInserted + " rows into " + getTableName());
		return rowsInserted;
	}

	@Override
	public int doUpdate(Object ing) throws SQLException {
		int rowsUpdated = -1;
		type = (IngredientType)ing;
		StringBuilder query = new StringBuilder("UPDATE " + getTableName() + " SET ");
		query.append("ID=" + type.getTypeID() + ", ");
		query.append("ING_TYPE_NAME='" + type.getTypeName() + "',");
		query.append("TYPE_DESCR='" + type.getTypeDesc() + "' ");
		query.append(" WHERE ID=" + type.getTypeID() + ";");
		
		javaLogger.logp(Level.INFO, null, "doUpdate()", "Updating " + getTableName() + " with ID " + type.getTypeID());
		Connection conn = connect();
		Statement stmt = conn.createStatement();
		rowsUpdated = stmt.executeUpdate(query.toString());
		
		return rowsUpdated;
	}

}
