package org.evansnet.ingredient.persistence;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.ui.PlatformUI;
import org.evansnet.dataconnector.internal.core.IDatabase;
import org.evansnet.dataconnector.internal.core.DBMS;
import org.evansnet.ingredient.model.Ingredient;
import org.evansnet.ingredient.persistence.IngredientPersistenceAction;
import org.evansnet.ingredient.persistence.repository.IngredientRepository;
import org.evansnet.ingredient.persistence.repository.RepositoryHelper;

/**
 * Base class for the ingredient persistence mechanism.
 * Provides connectivity to a database containing an ingredient 
 * repository table. 
 *  
 * @author pmidce0
 */
public class PersistenceProvider {
	
	/* Algorithm
	 * ------------------
	 * 1. get a connection to the repository database
	 * 2. Set the persistence type: Fetch, save, update, delete.
	 * 3. Set up a basic query for the type.
	 * 4. Allow the sub-class of the persistence type to execute the query.
	 * 
	 */
	public static final String THIS_CLASS_NAME = PersistenceProvider.class.getName();
	private static Logger javaLogger = Logger.getLogger(THIS_CLASS_NAME);
	
	public static IngredientRepository repository;
	IDatabase  db;
	Connection conn;
	Ingredient ingredient;
	ProgressBar progress;		//TODO: implement the progress bar for connection operation. Use a job.
	
	public PersistenceProvider() throws Exception {
		repository = getRepository(); //Set the repository to the default 
		db = new DBMS();
		ingredient = new Ingredient();
	}
		
	public PersistenceProvider(Ingredient i, IngredientPersistenceAction a) throws Exception {
		this();
		ingredient = i;
	}
	
	public IDatabase getDb() {
		return db;
	}

	public Connection getConn() {
		return db.getConnection();
	}

	
	public void doSave() throws SQLException {
		try {
			if	(!repository.checkExists(ingredient.getID())) {
				repository.doInsertNew(ingredient);
			} else {
				repository.doUpdate(ingredient);
			}
		} catch (Exception e) {
			StringBuilder message = new StringBuilder("An exception occurred while storing an ingreding in the repository. ");
			message.append("\n" + e.getCause() + e.getMessage());
			showErrMessageBox(message.toString());
			javaLogger.logp(Level.SEVERE, THIS_CLASS_NAME, "doSave()", message.toString());
			e.printStackTrace();
		}
	}
	
	public void doUpdate(Ingredient i) throws SQLException, Exception {
		repository.doUpdate(i);
	}
	
	public void doDelete(Ingredient i) throws SQLException, Exception {
		repository.doDelete(i.getID());
	}
	
	public void closeConnection() throws SQLException {
		this.closeConnection(conn);
		javaLogger.log(Level.INFO, "Database connection closed.");
	}
	
	public void setRepsitory(IngredientRepository repo) {
		repository = repo;
	}
	
	public IngredientRepository getRepository() throws Exception {
		// If repository hasn't been set yet, get the default.
		if (repository == null) {
			repository = new IngredientRepository();
			RepositoryHelper helper = new RepositoryHelper();
			repository.setRepo(helper.getDefaultRepository());
		} 
		return repository;
	}
	
	public void SetRepository(IngredientRepository repo) {
		if (!(repo == null)) {
			repository = repo;
		} else {
			MessageBox err = new MessageBox(PlatformUI.getWorkbench().
					getActiveWorkbenchWindow().getShell(),
					SWT.ICON_ERROR | SWT.OK);
			err.open();
		}
	} 
	
	private void closeConnection(Connection c) throws SQLException {
		c.close();
		javaLogger.log(Level.INFO, "Database connection closed.");
	}
	
	private void showErrMessageBox(String msg) {
		try {
			MessageBox theMsg = new MessageBox(
						PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.NONE);
			theMsg.setText("Persistence error!");
			theMsg.setMessage(msg);
			theMsg.open();
		} catch (Exception e) {
			javaLogger.log(Level.SEVERE, "Unable to get workbench: ", THIS_CLASS_NAME);
			e.printStackTrace();
		}	
	}
}
