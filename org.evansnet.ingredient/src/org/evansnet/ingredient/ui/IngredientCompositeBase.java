package org.evansnet.ingredient.ui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Button;

import org.evansnet.ingredient.model.Ingredient;

import java.math.BigDecimal;

import java.util.List;
import java.util.ArrayList;

public class IngredientCompositeBase extends Composite {
	private Text txtIngredientName;
	private Text txtIngredientDescription;
	private Text txtUnitPrice;
	private Text txtPackagePrice;
	private Combo cmbUom;
	private Combo cmbPkgUom;
	private Button btnHasARecipe;
	private boolean dirty = false;
	

	Ingredient ingredient = new Ingredient();
	
	// Temporary objects remove and re-code when UOM is available.
	List<String> uom = new ArrayList<String>();
	

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public IngredientCompositeBase(Composite parent, int style) {
		super(parent, style);
		
		setLayout(new GridLayout(4, false));
		uom.add("ounce");
		uom.add("Pound");
		uom.add("each");
		uom.add("package");
		uom.add("fl ounce");
		
		Label lblIngredientName = new Label(this, SWT.NONE);
		lblIngredientName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblIngredientName.setText("Ingredient Name:");
		
		txtIngredientName = new Text(this, SWT.BORDER);
		txtIngredientName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		new Label(this, SWT.NONE);
		
		Label lblDescription = new Label(this, SWT.NONE);
		lblDescription.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblDescription.setText("Description: ");
		
		txtIngredientDescription = new Text(this, SWT.BORDER);
		txtIngredientDescription.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		
		Label lblType = new Label(this, SWT.NONE);
		lblType.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblType.setText("Type: ");
		
		Combo cmbType = new Combo(this, SWT.NONE);
		cmbType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		new Label(this, SWT.NONE);
		
		Label lblRecipeUnitOf = new Label(this, SWT.NONE);
		lblRecipeUnitOf.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblRecipeUnitOf.setText("Recipe Unit of Measure: ");
		
		cmbUom = new Combo(this, SWT.NONE);
		cmbUom.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		// TODO: Add the fill code when the UOM class becomes available
		
		Label lblPackageUnitOf = new Label(this, SWT.NONE);
		lblPackageUnitOf.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPackageUnitOf.setText("Package Unit of Measure");
		
		cmbPkgUom = new Combo(this, SWT.NONE);
		cmbPkgUom.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		//Add call to populate list here.
		
		Label lblUnitPrice = new Label(this, SWT.NONE);
		lblUnitPrice.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblUnitPrice.setText("Unit Price: ");
		
		txtUnitPrice = new Text(this, SWT.BORDER);
		txtUnitPrice.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblPackagePrice = new Label(this, SWT.NONE);
		lblPackagePrice.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPackagePrice.setText("Package Price: ");
		
		txtPackagePrice = new Text(this, SWT.BORDER);
		txtPackagePrice.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Button btnHasARecipe = new Button(this, SWT.CHECK);
		btnHasARecipe.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		btnHasARecipe.setText("Has a recipe");
		new Label(this, SWT.NONE);
		
		btnHasARecipe.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (ingredient.isRecipe()) {
					ingredient.setRecipe(false);
				} else {
					ingredient.setRecipe(true);
				}
			}
		});
		
		Button btnIngredientOK = new Button(this, SWT.NONE);
		GridData gd_btnIngredientOK = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_btnIngredientOK.widthHint = 56;
		btnIngredientOK.setLayoutData(gd_btnIngredientOK);
		btnIngredientOK.setText("OK");
		
		btnIngredientOK.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateModel();
				dirtyFlag(true);
			}
		});
		
		Button btnNewButton = new Button(this, SWT.NONE);
		btnNewButton.setText("Cancel");
		
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				clearControls();
				dirtyFlag(false);
				btnHasARecipe.setSelection(false);
			}
		});
		
		//Temporary call to populate combo box
		populateLists();		
	}

	private void dirtyFlag(boolean b) {
		dirty = b;
	}

	private void updateModel() {
		Double up;
		Double pp;
		
		// Test for non-numeric values
		try {
			up = Double.parseDouble(txtUnitPrice.getText());
			pp = Double.parseDouble(txtPackagePrice.getText());
		} catch (NumberFormatException e) {
			// if a non-numeric value is encountered return without updating anything.
			MessageBox numErr = new MessageBox(this.getParent().getShell(), SWT.ICON_ERROR|SWT.OK);
			numErr.setMessage("Numeric error has been detected. Please verify price content: " + 
					e.getMessage());
			numErr.open();
			txtUnitPrice.setText("");
			txtPackagePrice.setText("");
			return;
		}
		
		// All is well, update the model.
		ingredient.setIngredientName(txtIngredientName.getText());
		ingredient.setIngredientDescription(txtIngredientDescription.getText());
		ingredient.setStrUom(cmbUom.getText());
		ingredient.setPkgUom(cmbPkgUom.getText());
		ingredient.setUnitPrice(BigDecimal.valueOf(up));
		ingredient.setPkgPrice(BigDecimal.valueOf(pp));
		ingredient.setRecipe(btnHasARecipe.getSelection());
		dirtyFlag(false);
	}

	private void clearControls() {
		txtIngredientName.setText("");
		txtIngredientDescription.setText("");
		txtUnitPrice.setText("");
		txtPackagePrice.setText("");
		cmbUom.setText("");
		cmbPkgUom.setText("");
	}
	
	public boolean isDirty() {
		return dirty;
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	
	/**
	 * Temporary method that will populate the list boxes while the 
	 * measurement units plug-in is under development.
	 */
	private void populateLists() {
		List<String> units = new ArrayList<String>();
		units.add("ounces");
		units.add("pounds");
		units.add("grams");
		units.add("fl ounces");
		units.add("each");
		units.add("other");
		
		for(String s : units) {
			cmbUom.add(s);
			cmbPkgUom.add(s);
		}
	}
	
	
}
