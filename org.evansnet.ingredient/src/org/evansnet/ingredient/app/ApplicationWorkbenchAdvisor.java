package org.evansnet.ingredient.app;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

	private static final String PERSPECTIVE_ID = "org.evansnet.ingredient.perspective";
	
	public static final String THIS_CLASS_NAME = "org.evansnet.ApplicationWorkbenchAdvisor";
	public Logger javaLogger  = Logger.getLogger(THIS_CLASS_NAME);

	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(
			IWorkbenchWindowConfigurer configurer) {
		javaLogger.log(Level.INFO, THIS_CLASS_NAME, "Creating workbenchWindowAdvisor");
		return new ApplicationWorkbenchWindowAdvisor(configurer);
	}

	public String getInitialWindowPerspectiveId() {
		javaLogger.log(Level.INFO, THIS_CLASS_NAME, "Getting initial perspective");
		return PERSPECTIVE_ID;
	}

}
