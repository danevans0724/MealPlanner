package org.evansnet.ingredient.test;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import org.evansnet.ingredient.ui.IngredientCompositeBase;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.SWT;


public class TestIngredientComposite {
	
	IngredientCompositeBase ingTest;
	

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		TestIngredientComposite t = new TestIngredientComposite();
		Display display = Display.getDefault();
		Shell shell = new Shell();
		shell.setSize(450, 300);
		shell.setText("Ingredient Test Window");
		shell.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		t.ingTest = new IngredientCompositeBase(shell, SWT.FILL);
		shell.pack();

		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
}
