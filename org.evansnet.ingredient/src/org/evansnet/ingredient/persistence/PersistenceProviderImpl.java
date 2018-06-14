package org.evansnet.ingredient.persistence;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.ui.PlatformUI;
import org.evansnet.dataconnector.internal.core.DBMS;
import org.evansnet.dataconnector.internal.core.IDatabase;
import org.evansnet.ingredient.persistence.repository.IRepository;

public abstract class PersistenceProviderImpl implements IPersistenceProvider {

	public static final String THIS_CLASS_NAME = IngredientProvider.class.getName();
	protected static Logger javaLogger = Logger.getLogger(THIS_CLASS_NAME);
	public static IRepository repository;
	protected IDatabase db;
	protected Connection conn;
	public ProgressBar progress;

	public PersistenceProviderImpl() throws Exception {
		repository = getRepository(); //Set the repository to the default 
		db = new DBMS();
	}

	@Override
	public IDatabase getDb() {
		return db;
	}

	@Override
	public Connection getConn() {
		return db.getConnection();
	}

	@Override
	public int doSave(Object ingToSave) throws SQLException {
		return 0;
	}

	@Override
	public void doUpdate(Object i) throws SQLException, Exception {
		repository.doUpdate(i);
	}

	@Override
	public int doDelete(Object i) throws SQLException, Exception {
		return 0;
	}

	@Override
	public void closeConnection() throws SQLException {
		this.closeConnection(conn);
		javaLogger.log(Level.INFO, "Database connection closed.");
	}

	@Override
	public void setRepsitory(IRepository repo) {
		repository = repo;
	}

	@Override
	public IRepository getRepository() throws Exception {
		return repository;
	}

	public void SetRepository(IRepository repo) {
		if (!(repo == null)) {
			repository = repo;
		} else {
			MessageBox err = new MessageBox(PlatformUI.getWorkbench().
					getActiveWorkbenchWindow().getShell(),
					SWT.ICON_ERROR | SWT.OK);
			err.open();
		}
	}

	protected void closeConnection(Connection c) throws SQLException {
		c.close();
		javaLogger.log(Level.INFO, "Database connection closed.");
	}

	protected void showErrMessageBox(String msg) {
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