package org.evansnet.ingredient.ui;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.evansnet.dataconnector.internal.core.IDatabase;
import org.evansnet.ingredient.app.Activator;
import org.evansnet.ingredient.persistence.preferences.PreferenceConstants;
import org.evansnet.ingredient.persistence.repository.IRepository;
import org.evansnet.ingredient.persistence.repository.IngredientRepository;
import org.evansnet.ingredient.persistence.repository.RepositoryHelper;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;

public class IngredientPreferences extends PreferencePage implements IWorkbenchPreferencePage {
	
	private Text txtRepo;
	private Text txtUserId;
	private Text txtPassword;
	Button btnRepoConn;
	IPreferenceStore prefStore;

	/**
	 * Create the preference page.
	 */
	public IngredientPreferences() {
		super();
		prefStore = doGetPreferenceStore();
		setPreferenceStore(prefStore);
		setDescription("Configuration values for the ingredient system.");
	}

	/**
	 * Create the controls of the preference page.
	 * @param parent
	 */
	@Override
	public Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(4, false));
		
		Label lblIngredientRepositoryConnection = new Label(container, SWT.NONE);
		lblIngredientRepositoryConnection.setText("Ingredient repository connection string;");
		lblIngredientRepositoryConnection.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 3, 1));
		new Label(container, SWT.NONE);
		
		txtRepo = new Text(container, SWT.BORDER);
		GridData gd_txtRepo = new GridData(SWT.LEFT, SWT.CENTER, true, false, 4, 1);
		gd_txtRepo.widthHint = 340;
		txtRepo.setLayoutData(gd_txtRepo);
		
		Label lblcHeader = new Label(container, SWT.NONE);
		lblcHeader.setText("Repository Credentials:");
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		Label lblUserId = new Label(container, SWT.NONE);
		lblUserId.setText("User ID: ");
		
		txtUserId = new Text(container, SWT.BORDER);
		txtUserId.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		Label lblPassword = new Label(container, SWT.NONE);
		lblPassword.setText("Password: ");
		
		txtPassword = new Text(container, SWT.BORDER | SWT.PASSWORD);
		txtPassword.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		
		
		btnRepoConn = new Button(container, SWT.NONE);
		btnRepoConn.setToolTipText("Create a connect definition");
		btnRepoConn.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnRepoConn.setText("&Connect");
		
		btnRepoConn.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!doTestRepositoryConn()) {
					String msg = new String( "Could not connect to the ingredient repository!");
					MessageBox errBox = new MessageBox(parent.getShell(), SWT.ICON_ERROR);
					errBox.setMessage(msg);
					errBox.open();
				} else {
					//Successful connection
					String msg = "Connection successful!";
					MessageBox success = new MessageBox(parent.getShell(), SWT.ICON_INFORMATION);
					success.setMessage(msg);
					success.open();
				}
				return;
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}			
		});
		new Label(container, SWT.NONE);
		
		initializeValues();
		
		return container;
	}
	
	/**
	 * Overridden to implement OK processing.
	 */
	@Override
	public boolean performOk() {
		savePreferences();
		return true;
	}
	
	@Override
	public void performDefaults() {
		restoreDefaults();
	}
	
	/** 
	 * Overridden to handle apply processing.
	 */
	@Override
	public void performApply() {
		//Apply the value to the repository model and then call performOK() to save it.
		//TODO: Update when repository logic is developed.
		savePreferences();
	}

	@Override
	protected IPreferenceStore doGetPreferenceStore() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		return store;
	}

	@Override
	public void init(IWorkbench workbench) {}
	
	private void initializeValues() {
		txtRepo.setText(prefStore.getString(PreferenceConstants.PRE_REPO_CONN_STR));
		txtUserId.setText(prefStore.getString(PreferenceConstants.PRE_REPO_USER_ID));
		txtPassword.setText(prefStore.getString(PreferenceConstants.PRE_REPO_USER_PWD));
	}
	
	private void restoreDefaults() {
		//Set the value in the control to the default. Warning dialog??
		txtRepo.setText(prefStore.getDefaultString(PreferenceConstants.PRE_REPO_CONN_STR));
		txtUserId.setText(prefStore.getDefaultString(PreferenceConstants.PRE_REPO_USER_ID));
		txtPassword.setText(prefStore.getDefaultString(PreferenceConstants.PRE_REPO_USER_PWD));
	}
	
	private void savePreferences() {
		prefStore.setValue(PreferenceConstants.PRE_REPO_CONN_STR, txtRepo.getText());
		prefStore.setValue(PreferenceConstants.PRE_REPO_USER_ID, txtUserId.getText());
		prefStore.setValue(PreferenceConstants.PRE_REPO_USER_PWD, txtPassword.getText());
	}

	private boolean doTestRepositoryConn() {
		MessageBox info; 
		boolean result = false;
		RepositoryHelper helper = new RepositoryHelper();
		String message = "Failed to connect to the repository shown.";
		info = new MessageBox(this.getShell(), SWT.ICON_INFORMATION);
		try {
			IDatabase dbTest = helper.getDefaultRepository();
			if (dbTest == null) {
				throw new Exception(message);
			}
			info.setMessage("Connection successful!");
			info.open();
			result = true;
		} catch (Exception e) {
			info.setMessage(message);
			info.open();
			e.printStackTrace();
		}
		
		return result;
	}
}
