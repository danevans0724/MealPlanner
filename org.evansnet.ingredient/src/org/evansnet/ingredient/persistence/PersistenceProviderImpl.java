package org.evansnet.ingredient.persistence;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.ui.PlatformUI;

import org.evansnet.repository.core.RepositoryImpl;

/**
 * Implementation of the IPersistenceProvider interface.
 * 
 * @author Dan Evans (c) 2020 All rights reserved.
 *
 */
public abstract class PersistenceProviderImpl extends RepositoryImpl implements IPersistenceProvider {

	public static final String THIS_CLASS_NAME = IngredientProvider.class.getName();
	protected static Logger javaLogger = Logger.getLogger(THIS_CLASS_NAME);
	protected Connection conn;
	public ProgressBar progress;

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