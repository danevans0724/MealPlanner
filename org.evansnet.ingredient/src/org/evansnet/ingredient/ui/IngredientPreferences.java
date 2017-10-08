package org.evansnet.ingredient.ui;

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
import org.evansnet.ingredient.app.Activator;
import org.evansnet.ingredient.persistence.preferences.PreferenceConstants;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;

public class IngredientPreferences extends PreferencePage implements IWorkbenchPreferencePage {
	
	private Text txtRepo;
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
		container.setLayout(new GridLayout(1, false));
		
		Label lblIngredientRepositoryConnection = new Label(container, SWT.NONE);
		lblIngredientRepositoryConnection.setText("Ingredient repository connection string;");
		
		txtRepo = new Text(container, SWT.BORDER);
		GridData gd_txtRepo = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_txtRepo.widthHint = 340;
		txtRepo.setLayoutData(gd_txtRepo);
		
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
				} 
				return;
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
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
	}
	
	private void restoreDefaults() {
		//Set the value in the control to the default. Warning dialog??
		txtRepo.setText(prefStore.getDefaultString(PreferenceConstants.PRE_REPO_CONN_STR));
	}
	
	private void savePreferences() {
		prefStore.setValue(PreferenceConstants.PRE_REPO_CONN_STR, txtRepo.getText());
	}

	private boolean doTestRepositoryConn() {
		// TODO: Temporary use of Java JDBC. Set up to use dataconnector plugin for long term
		MessageBox info = new MessageBox(this.getShell(), SWT.ICON_INFORMATION);
		info.setMessage("Not yet implemented. Always returns false!");
		info.open();
		return false;
	}
}
