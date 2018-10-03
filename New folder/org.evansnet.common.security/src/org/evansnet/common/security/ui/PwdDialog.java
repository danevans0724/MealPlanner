package org.evansnet.common.security.ui;

import java.awt.Button;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;


/**
 * Simple dialog to get user credentials
 * @author pmidce0
 *
 */
public class PwdDialog extends Dialog  implements SelectionListener {
	private String BTN_PWD_OK = "okButton";
	private String BTN_PWD_CANCEL = "cancelButton";
	private Text txtPwd;
	private char[] pwd;
	private Text txtPwdAgain;
	
	public PwdDialog(Shell parent) {
		super(parent);
	}

	public Object open () {
		Shell parent = getParent();
		Shell shlSafeStorePassword = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		shlSafeStorePassword.setText("Safe store password");
		shlSafeStorePassword.setSize(333, 211);
		shlSafeStorePassword.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Composite composite = new Composite(shlSafeStorePassword, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		Label lblSafeStorePassword = new Label(composite, SWT.NONE);
		lblSafeStorePassword.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblSafeStorePassword.setText("Safe store password: ");
		
		txtPwd = new Text(composite, SWT.BORDER | SWT.PASSWORD);
		txtPwd.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblReenterPassword = new Label(composite, SWT.NONE);
		lblReenterPassword.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblReenterPassword.setText("Re-enter password: ");
		
		txtPwdAgain = new Text(composite, SWT.BORDER | SWT.PASSWORD);
		txtPwdAgain.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		org.eclipse.swt.widgets.Button btnOk = new org.eclipse.swt.widgets.Button(composite, SWT.NONE);
		btnOk.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnOk.setText("OK");
		btnOk.setData(BTN_PWD_OK);
		
				
				org.eclipse.swt.widgets.Button btnCancel = new org.eclipse.swt.widgets.Button(composite, SWT.NONE);
				btnCancel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
				btnCancel.setText("Cancel");
				btnCancel.setData(BTN_PWD_CANCEL);
		
		shlSafeStorePassword.open();
		Display display = parent.getDisplay();
		while (!shlSafeStorePassword.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep();
		}
		
		return pwd;
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		if (e.getSource() == BTN_PWD_OK) {
			try {
				pwd = new char[txtPwd.getText().length()];
				pwd = txtPwd.getTextChars();
				this.getParent().close();
			} catch (NullPointerException npe) {
				npe.printStackTrace();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else if (e.getSource() == BTN_PWD_CANCEL) {
			try {
				pwd = "".toCharArray();
			} catch (NullPointerException npe) {
				npe.printStackTrace();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} else {
			throw new NullPointerException("Password cannot be null!");
		}
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
		widgetSelected(e);	
	}
	
}
