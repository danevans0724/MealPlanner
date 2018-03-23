package org.evansnet.ingredient.app;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class Perspective implements IPerspectiveFactory {
	public static final String ID = "org.evansnet.ingredient.perspective"; 
	
	public static final String THIS_CLASS_NAME = "org.evansnet.ingredient.app.Perspective";
	public Logger javaLogger = Logger.getLogger(THIS_CLASS_NAME);

	public void createInitialLayout(IPageLayout layout) {
		javaLogger.log(Level.INFO, THIS_CLASS_NAME, "Creating initial layout: createInitialLayout()");
		layout.setEditorAreaVisible(true);
		layout.setFixed(true);
	}

}
