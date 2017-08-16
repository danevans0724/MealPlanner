package org.evansnet.ingredient.ui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Button;

import org.evansnet.ingredient.model.Ingredient;
import org.evansnet.ingredient.model.IngredientType;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.core.databinding.beans.PojoProperties;

public class IngredientCompositeBase extends Composite {
	
	public Logger logger = Logger.getLogger("IngredientCompositeLogger");
	
	public static final String strIngredientName = "INGREDIENT_NAME";
	public static final String strIngredientDesc = "INGREDIENT_DESC";
	public static final String strUnitPrice = "UNIT_PRICE";
	public static final String strPackagePrice = "PACKAGE_PRICE";
	public static final String strIngredientType = "INGREDIENT_TYPE";
	public static final String strUnitOfMeasure = "UNIT_OF_MEASURE";
	public static final String strPkgUnitOfMeasure = "PKG_UNIT_OF_MEASURE";
	
	
	@SuppressWarnings("unused")
	private DataBindingContext m_bindingContext;
	private Text txtIngredientName;
	private Text txtIngredientDescription;
	private Text txtUnitPrice; 
	private Text txtPackagePrice;
	private Combo cmbType;
	private Combo cmbUom;
	private Combo cmbPkgUom;
	private Button btnHasARecipe;
	private boolean dirty = false;
	private IngredientType ingredientTypes;
	public static ModifyListener modifyListener;
	
	Ingredient ingredient = new Ingredient();
	
	// Temporary objects remove and re-code when UOM is available.
	List<String> uom = new ArrayList<String>();
	private Label lblIngredientName;
	private Button btnHasARecipe_1;
	

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public IngredientCompositeBase(Composite parent, int style) {
		super(parent, style);
		
		modifyListener = new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				switch((String)e.widget.getData()) {
				case strIngredientName:
				case strIngredientDesc:
				case strUnitPrice:
				case strPackagePrice:
				case strIngredientType:
				case strUnitOfMeasure:
				case strPkgUnitOfMeasure:
					setDirty(true);
					break;
				}
			}
		};
		
		setLayout(new GridLayout(4, false));
		uom.add("ounce");
		uom.add("Pound");
		uom.add("each");
		uom.add("package");
		uom.add("fl ounce");
		
		lblIngredientName = new Label(this, SWT.NONE);
		lblIngredientName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblIngredientName.setText("Ingredient Name:");
		
		txtIngredientName = new Text(this, SWT.BORDER);
		txtIngredientName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		new Label(this, SWT.NONE);
		txtIngredientName.setData(strIngredientName);
		txtIngredientName.addModifyListener(modifyListener);
		
		Label lblDescription = new Label(this, SWT.NONE);
		lblDescription.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblDescription.setText("Description: ");
		
		txtIngredientDescription = new Text(this, SWT.BORDER);
		txtIngredientDescription.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		txtIngredientDescription.setData(strIngredientDesc);
		txtIngredientDescription.addModifyListener(modifyListener);
		
		Label lblType = new Label(this, SWT.NONE);
		lblType.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblType.setText("Type: ");
		
		cmbType = new Combo(this, SWT.NONE);
		cmbType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		new Label(this, SWT.NONE);
		cmbType.setData(strIngredientType);
		cmbType.addModifyListener(modifyListener);
		
		Label lblRecipeUnitOf = new Label(this, SWT.NONE);
		lblRecipeUnitOf.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblRecipeUnitOf.setText("Recipe Unit of Measure: ");
		
		cmbUom = new Combo(this, SWT.NONE);
		cmbUom.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		cmbUom.setData(strUnitOfMeasure);
		cmbUom.addModifyListener(modifyListener);
		// TODO: Add the fill code when the UOM class becomes available
		
		Label lblPackageUnitOf = new Label(this, SWT.NONE);
		lblPackageUnitOf.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPackageUnitOf.setText("Package Unit of Measure");
		
		cmbPkgUom = new Combo(this, SWT.NONE);
		cmbPkgUom.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		cmbPkgUom.setData(strPkgUnitOfMeasure);
		cmbPkgUom.addModifyListener(modifyListener);
		//Add call to populate list here.
		
		Label lblUnitPrice = new Label(this, SWT.NONE);
		lblUnitPrice.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblUnitPrice.setText("Unit Price: ");
		
		txtUnitPrice = new Text(this, SWT.BORDER);
		txtUnitPrice.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtUnitPrice.setData(strUnitPrice);
		txtUnitPrice.addModifyListener(modifyListener);
		
		Label lblPackagePrice = new Label(this, SWT.NONE);
		lblPackagePrice.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPackagePrice.setText("Package Price: ");
		
		txtPackagePrice = new Text(this, SWT.BORDER);
		txtPackagePrice.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtPackagePrice.setData(strPackagePrice);
		txtPackagePrice.addModifyListener(modifyListener);
		
		btnHasARecipe_1 = new Button(this, SWT.CHECK);
		btnHasARecipe_1.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		btnHasARecipe_1.setText("Has a recipe");
		new Label(this, SWT.NONE);
		
		btnHasARecipe_1.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (ingredient.isRecipe()) {
					ingredient.setRecipe(false);
				} else {
					ingredient.setRecipe(true);
				}
			}
		});
		
		// Placeholders for permanent methods.
		fillTypesList();
		fillMeasuresList();
		
		//Temporary call to populate combo box
		populateLists();		
		m_bindingContext = initDataBindings();
	}

	private void fillTypesList() {
		// TODO Create permanent method for filling the ingredient types list.
		// TODO: UnMarshall the ingredient types from a table and into the combo.
	}
	
	private void fillMeasuresList() {
		//TODO: Create the fillMeasuresList() method. 
		//TODO: UnMarshall the measurement types from persistent storage and populate the combos.
	}

	private void dirtyFlag(boolean b) {
		dirty = b;
	}

	private void updateModel() {
		Double up;		//Unit price
		Double pp;		//Package price
		
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
		
		dirtyFlag(false);
		logger.log(Level.INFO,"\nIngredientCompositeBase.updateModel()\nModel checked and updated");
//		showModel();
	}
	
	public void showModel() {
		System.out.println("The state of the model\n-------------------------------");
		System.out.println("Ingredient name: " + ingredient.getIngredientName());
		System.out.println("Description: " + ingredient.getIngredientDescription());
		System.out.println("Unit of measure: " + ingredient.getPkgUom());
		System.out.println("Package unit of measure: " + ingredient.getPkgUom());
		System.out.println("Unit price: " + ingredient.getUnitPrice());
		System.out.println("Package price: " + ingredient.getPkgPrice());
		System.out.println("This ingredient has a recipe: " + ingredient.isRecipe());
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
	
	private void setDirty(boolean b) {
		dirty = true;		//TODO: The dirty flag in the composite. Do I need this?
		IngredientEditor ce =  (IngredientEditor)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		ce.makeDirty(true);
	}
	
	/**
	 * Returns the ingredient populated by this composite's widgets.
	 * @return The ingredient created by this composite, null if the ingredient
	 * is not defined.
	 */
	public Ingredient getIngredient() {
		if (isDirty()) {
			return ingredient;
		}
		return null;
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
		//TODO: Write code to get list contents from persistent storage and 
		//      then remove this temporary method.
		List<String> units = new ArrayList<String>();
		units.add("ounces");
		units.add("pounds");
		units.add("grams");
		units.add("cups");
		units.add("fl ounces");
		units.add("each");
		units.add("other");
		
		for(String s : units) {
			cmbUom.add(s);
			cmbPkgUom.add(s);
		}
		
		// Now populate the ingredient types
		List<String> types = new ArrayList<String>();
		types.add("Staples");
		types.add("Canned goods");
		types.add("Dry goods");
		types.add("Baking");
		types.add("Frozen foods");
		types.add("Fruits & vegetables");
		
		for(String t : types) {
			cmbType.add(t);
		}
	}
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue observeTextTxtUnitPriceObserveWidget = WidgetProperties.text(SWT.Modify).observe(txtUnitPrice);
		IObservableValue unitPriceIngredientObserveValue = PojoProperties.value("unitPrice").observe(ingredient);
		bindingContext.bindValue(observeTextTxtUnitPriceObserveWidget, unitPriceIngredientObserveValue, null, null);
		//
		IObservableValue observeTextCmbTypeObserveWidget = WidgetProperties.text().observeDelayed(4, cmbType);
		IObservableValue typeNameIngredientTypesObserveValue = PojoProperties.value("typeName").observe(ingredientTypes);
		bindingContext.bindValue(observeTextCmbTypeObserveWidget, typeNameIngredientTypesObserveValue, null, null);
		//
		IObservableValue observeTextTxtPackagePriceObserveWidget = WidgetProperties.text(SWT.Modify).observe(txtPackagePrice);
		IObservableValue pkgPriceIngredientObserveValue = PojoProperties.value("pkgPrice").observe(ingredient);
		bindingContext.bindValue(observeTextTxtPackagePriceObserveWidget, pkgPriceIngredientObserveValue, null, null);
		//
		IObservableValue observeTextCmbPkgUomObserveWidget = WidgetProperties.text().observe(cmbPkgUom);
		IObservableValue pkgUomIngredientObserveValue = PojoProperties.value("pkgUom").observe(ingredient);
		bindingContext.bindValue(observeTextCmbPkgUomObserveWidget, pkgUomIngredientObserveValue, null, null);
		//
		IObservableValue observeTextCmbUomObserveWidget = WidgetProperties.text().observe(cmbUom);
		IObservableValue strUomIngredientObserveValue = PojoProperties.value("strUom").observe(ingredient);
		bindingContext.bindValue(observeTextCmbUomObserveWidget, strUomIngredientObserveValue, null, null);
		//
		IObservableValue observeSelectionBtnHasARecipe_1ObserveWidget = WidgetProperties.selection().observe(btnHasARecipe_1);
		IObservableValue recipeIngredientObserveValue = PojoProperties.value("recipe").observe(ingredient);
		bindingContext.bindValue(observeSelectionBtnHasARecipe_1ObserveWidget, recipeIngredientObserveValue, null, null);
		//
		IObservableValue observeTextTxtIngredientNameObserveWidget_1 = WidgetProperties.text(SWT.Modify).observe(txtIngredientName);
		IObservableValue ingredientNameIngredientObserveValue = PojoProperties.value("ingredientName").observe(ingredient);
		bindingContext.bindValue(observeTextTxtIngredientNameObserveWidget_1, ingredientNameIngredientObserveValue, null, null);
		//
		IObservableValue observeTextTxtIngredientDescriptionObserveWidget = WidgetProperties.text(SWT.Modify).observe(txtIngredientDescription);
		IObservableValue ingredientDescriptionIngredientObserveValue = PojoProperties.value("ingredientDescription").observe(ingredient);
		bindingContext.bindValue(observeTextTxtIngredientDescriptionObserveWidget, ingredientDescriptionIngredientObserveValue, null, null);
		//
		
		// Show the state of the model
		showModel();
		
		return bindingContext;
	}
}
