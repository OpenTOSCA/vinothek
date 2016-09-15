package org.opentosca.ui.vinothek.integration;

import java.io.IOException;

import javax.servlet.ServletException;

public interface TOSCARuntimeConnector {
	
	
	String instantiateServiceTemplate(String self, String containerUrl, String applicationId, String optionId, String sentPlanInputMessage) throws ServletException, IOException;
	
	String terminateServiceTemplate(String self, String containerUrl, String applicationId, String selfserviceServiceInstance);
	
}