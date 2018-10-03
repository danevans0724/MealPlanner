package org.evansnet.common.configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.cert.Certificate;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Contains methods for obtaining the common file paths and 
 * other configuration data for other plugins.
 * 
 * @author Daniel Evans 2018
 */
public final class Globals {
	public static final String THIS_CLASS_NAME = Globals.class.getName();
	private final String CONFIGURATION_PROPERTIES = "configuration.properties";
	private final String SECURITY_PROPERTIES_FILE = "security.properties";

	private static Properties evansnetGlobalProperties;
	private static String configPath;

	public Logger javaLogger = Logger.getLogger(THIS_CLASS_NAME);
	private Properties sysProp;
	private FileSystem filesystem;
	private Path userPath;
	private String userWorkingDir;
	private String user;
	private String SECURITY_FOLDER;				// The name of the folder that holds SECURITY_PROPERTIES_FILE
	private char[] certPath;					// The path to the certificate used to get the password for the credentials cert.
	private String CERT_STORE; 					// The name of the certificate store.
	private char[] storePw;
	
	public Globals() {
		javaLogger.log(Level.INFO, "Constructing global configuration.");
		if (!checkConfigFile()) {
			try {
				sysProp = System.getProperties();
				evansnetGlobalProperties = new Properties();
				filesystem  = FileSystems.getDefault();
				user = sysProp.getProperty("user.name");
				userPath = filesystem.getPath(sysProp.getProperty("user.home"));
				userWorkingDir = userPath + File.separator + "evansnet";
				configPath =  new String(userPath + File.separator + "evansnet" + File.separator + "cfg");
				SECURITY_FOLDER = userWorkingDir + File.separator + "store";	
				certPath = SECURITY_FOLDER.toCharArray();
				CERT_STORE = SECURITY_FOLDER + File.separator + "certstore.truststore";
				storePw = new char[] {'B','B','v','1','0','|','e','t'};
				setEvansnetGlobalProperties();
			} catch (UnsupportedOperationException uoe) {
				uoe.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			javaLogger.log(Level.INFO, "Fetching configuration from file.");
			fetchConfig();
		}
	}

	public Path getUserPath() {
		return userPath;
	}

	public void setUserPath(Path userPath) {
		this.userPath = userPath;
	}

	public String getUserWorkingDir() {
		return userWorkingDir;
	}

	public void setUserWorkingDir(String userWorkingDir) {
		this.userWorkingDir = userWorkingDir;
	}

	public String getUser() {
		return user;
	}
	
	public String getSECURITY_FOLDER() {
		return SECURITY_FOLDER;
	}

	public void setSECURITY_FOLDER(String sECURITY_FOLDER) {
		SECURITY_FOLDER = sECURITY_FOLDER;
	}

	private boolean checkConfigFile() {
		javaLogger.log(Level.INFO, "Checking presence of configuration file.");
		if (Files.exists(filesystem.getPath(certPath.toString() + File.separator + CONFIGURATION_PROPERTIES))) {
			return true;
		} else {
			return false;
		}
	}
	
	private void setEvansnetGlobalProperties() {
		javaLogger.log(Level.INFO, "Setting configuration values.");
		evansnetGlobalProperties.put("user", user);
		evansnetGlobalProperties.put("userPath", userPath);
		evansnetGlobalProperties.put("userWorkingDir", userWorkingDir);
		evansnetGlobalProperties.put("configuration_properties", CONFIGURATION_PROPERTIES);
		evansnetGlobalProperties.put("configPath", configPath);
		evansnetGlobalProperties.put("security_folder", SECURITY_FOLDER);
		evansnetGlobalProperties.put("security_properties_file", SECURITY_PROPERTIES_FILE);
		evansnetGlobalProperties.put("certificatePath", certPath.toString());
		evansnetGlobalProperties.put("certificate_store", CERT_STORE);
	}
	
	/**
	 * Returns the public key certificate used to access the secure store.
	 * @return The publci key certificate.
	 */
	public Certificate fetchSecureStoreCert() {
		//TODO: Write the fetchSecureStoreCert() method.
		return null;
	}
	
	/**
	 * Retrieves the secure store password from the configuration. 
	 * @return The encrypted secure store password. 
	 */
	public char[] fetchSecureStoreCredentials() {
		//TODO: Write the fetchSecureStoreCredentials() method.
		return null;
	}
	
	/**
	 * Allows the user to set the secure store credential used in the security plugin.
	 * The product comes with a default credentials which a user can change using this method.
	 * @param c - A char[] containing the credential to store
	 */
	public void setSecureStoreCredentials(char[] c) {
		storePw = c;
	}

	/**
	 * Retrieve the configuration info from the system disk.
	 * Configuration is saved in the user's directory.
	 */
	public void fetchConfig() {		
		try {
			evansnetGlobalProperties.load(new FileReader(configPath + File.separator + CONFIGURATION_PROPERTIES));
		} catch (FileNotFoundException e) {
			javaLogger.log(Level.SEVERE, "ERROR! Configuration properties file not found!" + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			javaLogger.log(Level.SEVERE, "ERROR! IOException caught while fetching configuration. " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * Save the configuration to the global configuration store. The
	 * store is located in the user's directory.
	 */
	public void saveConfig() {
		javaLogger.log(Level.INFO, "Saving configuration to disk");
		try {
			if (Files.notExists(Paths.get(userWorkingDir))) {
				Files.createDirectories(Paths.get(configPath));
				Files.createFile(Paths.get(configPath + File.separator + CONFIGURATION_PROPERTIES));				
			}
			evansnetGlobalProperties.store(new FileWriter(configPath + File.separator + CONFIGURATION_PROPERTIES),
					                        "Configuration properties for evansnet tools.");
			javaLogger.log(Level.INFO, "Configuration saved successfully.");
		} catch (IOException e) {
			javaLogger.log(Level.SEVERE, "Error saving configuration: " + e.getMessage() + "\n");
			e.printStackTrace();
		}
	}

}
