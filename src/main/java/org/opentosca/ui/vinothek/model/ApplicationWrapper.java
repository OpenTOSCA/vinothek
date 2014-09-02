package org.opentosca.ui.vinothek.model;

import org.eclipse.winery.model.selfservice.Application;
import org.opentosca.ui.vinothek.CONFIG;

public class ApplicationWrapper extends Application {

	private String selfServiceBaseUrl = "";

	public void setSelfServiceBaseUrl(String selfServiceBaseUrl) {
		this.selfServiceBaseUrl = selfServiceBaseUrl;
		// Ensures that this URL ends with '/'
		if (!this.selfServiceBaseUrl.endsWith("/")) {
			this.selfServiceBaseUrl += "/";
		}
	}

	/**
	 * Base URL of the application's self service folder to retrieve files. Ends
	 * with '/'!
	 * 
	 * @return
	 */
	public String getSelfServiceBaseUrl() {
		return this.selfServiceBaseUrl;
	}

	/**
	 * Returns an absolute URL for a given relativeUrl of this application
	 * 
	 * @param relativeUrl
	 * @return
	 */
	public String convertToAbsoluteUrl(String relativeUrl) {
		// to prevent NPE
		if(relativeUrl == null) {
			return "";
		}
		
		// If path is absolute, return it
		if (relativeUrl.startsWith("http")) {
			return relativeUrl;
		}
		// If it starts with / remove it
		if (relativeUrl.startsWith("/")) {
			relativeUrl = relativeUrl.substring(1);
		}
		
		// If this is a relative URL it is relative to the self-service folder
		// This is how the OpenTOSCA REST API currently works
		return getSelfServiceBaseUrl() + relativeUrl;
	}
	
	/**
	 * Extracts the name of the CSAR file represented by this application.<br>
	 * E.g., Moodle.csar
	 * 
	 * @return
	 */
	public String getCsarName() {
		String csarName = getSelfServiceBaseUrl().replace(CONFIG.METADATA_FOLDER, "");
		return csarName.substring(csarName.lastIndexOf("/") + 1);
	}
}
