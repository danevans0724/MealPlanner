package org.evansnet.ingredient.ui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Button;

import org.evansnet.ingredient.model.Ingredient;
import org.evansnet.ingredient.model.IngredientType;
import org.evansnet.ingredient.model.Measures;
import org.evansnet.ingredient.repository.IngredientTypeRepository;
import org.evansnet.repository.core.IRepository;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.HashMap;

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
	public static final String strBtnHasARecipe = "HAS_A_RECIPE";
	
	

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
	public static ModifyListener modifyListener;
	
	Ingredient ingredient;
	
	// Temporary objects remove and re-code when UOM is available.
	List<String> uom = new ArrayList<String>();
	private Label lblIngredientName;
	private Map<Integer, Object> ingredientTypes;
	

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public IngredientCompositeBase(Composite parent, int style, Ingredient i) {
		super(parent, style);
		ingredientTypes = new HashMap<Integer, Object>();
		
		modifyListener = new ModifyListener() {
			//Set the editor dirty if any of the controls have had text edited
			@Override
			public void modifyText(ModifyEvent e) {
				switch((String)e.widget.getData()) {
				case strIngredientName:
				case strIngredientDesc:
				case strUnitPrice:
				case strPackagePrice:
				case strUnitOfMeasure:
				case strPkgUnitOfMeasure:
				case strBtnHasARecipe:
					setDirty(true);
					break;
				}
			}
		};
		
		if (i == null) {
			ingredient = new Ingredient();
			//Insure the ingredient type isn't zero to start.
			ingredient.setIngredientType(1);
		} else {
			// An ingredient is passed in if one was selected from the tree. Allows editing.
			ingredient = i;
		}
		
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
		cmbType.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {				
				cmbType.setText(cmbType.getItem(cmbType.getSelectionIndex()));
				ingredient.setIngredientType(cmbType.getSelectionIndex() +1 );
				setDirty(true);
			}
		});
		
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
		//TODO: Add the fill code when the UOM class becomes available
		
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
		
		btnHasARecipe = new Button(this, SWT.CHECK);
		btnHasARecipe.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		btnHasARecipe.setText("Has a recipe");
		btnHasARecipe.setData(strBtnHasARecipe);
		new Label(this, SWT.NONE);
		
		btnHasARecipe.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (ingredient.getIsRecipe()) {
					ingredient.setIsRecipe(false);
				} else {
					ingredient.setIsRecipe(true);
				}
			setDirty(true);
			}
		});
		
//		fetchIngredientTypes(); // Go to the repository and get the ingredient types.
		
		// Placeholders for permanent methods.
		fillTypesList();
		fillMeasuresList();
		
		//Temporary call to populate combo box
		populateLists();
		
		m_bindingContext = initDataBindings();
		// Set the type name of the current ingredient.
//		int ingType = ingredient.getIngredientType();
//		if (ingType < 1) {
//			//If the ingredient type is zero set it to 1 in order to avoid an exception later.
//			ingredient.setIngredientType(1);
//			ingType = 1;
//		}
//		IngredientType type = (IngredientType)(ingredientTypes.get(ingType));
//		cmbType.setText(type.getTypeName());
	}

	private void fillTypesList() {
		logger.logp(Level.INFO, IngredientCompositeBase.class.getName(), 
				"fillTypesList()", "Filling the ingredient type list.");
			for (Object o : ingredientTypes.values()) {
				cmbType.add(((IngredientType)o).getTypeName());
			}
	}
	
	private void fetchIngredientTypes() {
		try {
			IRepository typeRepo = new IngredientTypeRepository();
			typeRepo.getRepository();
			ingredientTypes = typeRepo.fetchAll();
		} catch ( Exception e) {
			e.printStackTrace();
		}	
	}
	
	private void fillMeasuresList() {
		//TODO: Create the fillMeasuresList() method. 
	}

	public boolean isDirty() {
		return dirty;
	}
	
	private void setDirty(boolean b) {
		dirty = true;
		IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if (editor instanceof IngredientEditor) {
			((IngredientEditor) editor).makeDirty(true);
		}
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
		Measures measures = new Measures();
		ArrayList<String> units = measures.getMeasures();
		
		for (String s : units) {
			cmbUom.add(s);
			cmbPkgUom.add(s);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected DataBindingContext initDataBindings() {
		m_bindingContext = new DataBindingContext();
		//
		IObservableValue observeTextTxtUnitPriceObserveWidget = WidgetProperties.text(SWT.Modify).observe(txtUnitPrice);
		IObservableValue unitPriceIngredientObserveValue = PojoProperties.value("unitPrice").observe(ingredient);
		m_bindingContext.bindValue(observeTextTxtUnitPriceObserveWidget, unitPriceIngredientObserveValue, null, null);
		//
		IObservableValue observeTextCmbTypeObserveWidget = WidgetProperties.singleSelectionIndex().observeDelayed(4, cmbType);
		IObservableValue typeNameIngredientTypesObserveValue = PojoProperties.value("typeName").observe(ingredient);
		m_bindingContext.bindValue(observeTextCmbTypeObserveWidget, typeNameIngredientTypesObserveValue, null, null);
		//
		IObservableValue observeTextTxtPackagePriceObserveWidget = WidgetProperties.text(SWT.Modify).observe(txtPackagePrice);
		IObservableValue pkgPriceIngredientObserveValue = PojoProperties.value("pkgPrice").observe(ingredient);
		m_bindingContext.bindValue(observeTextTxtPackagePriceObserveWidget, pkgPriceIngredientObserveValue, null, null);
		//
		IObservableValue observeTextCmbPkgUomObserveWidget = WidgetProperties.singleSelectionIndex().observe(cmbPkgUom);
		IObservableValue pkgUomIngredientObserveValue = PojoProperties.value("pkgUom").observe(ingredient);
		m_bindingContext.bindValue(observeTextCmbPkgUomObserveWidget, pkgUomIngredientObserveValue, null, null);
		//
		IObservableValue observeTextCmbUomObserveWidget = WidgetProperties.singleSelectionIndex().observe(cmbUom);
		IObservableValue strUomIngredientObserveValue = PojoProperties.value("strUom").observe(ingredient);
		m_bindingContext.bindValue(observeTextCmbUomObserveWidget, strUomIngredientObserveValue, null, null);
		//
		IObservableValue observeSelectionBtnHasARecipe_1ObserveWidget = WidgetProperties.selection().observe(btnHasARecipe);
		IObservableValue recipeIngredientObserveValue = PojoProperties.value("isRecipe").observe(ingredient);
		m_bindingContext.bindValue(observeSelectionBtnHasARecipe_1ObserveWidget, recipeIngredientObserveValue, null, null);
		//
		IObservableValue observeTextTxtIngredientNameObserveWidget_1 = WidgetProperties.text(SWT.Modify).observe(txtIngredientName);
		IObservableValue ingredientNameIngredientObserveValue = PojoProperties.value("ingredientName").observe(ingredient);
		m_bindingContext.bindValue(observeTextTxtIngredientNameObserveWidget_1, ingredientNameIngredientObserveValue, null, null);
		//
		IObservableValue observeTextTxtIngredientDescriptionObserveWidget = WidgetProperties.text(SWT.Modify).observe(txtIngredientDescription);
		IObservableValue ingredientDescriptionIngredientObserveValue = PojoProperties.value("ingredientDescription").observe(ingredient);
		m_bindingContext.bindValue(observeTextTxtIngredientDescriptionObserveWidget, ingredientDescriptionIngredientObserveValue, null, null);
		//
		
		return m_bindingContext;
	}
}
