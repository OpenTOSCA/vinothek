package org.opentosca.ui.vinothek.integration;

import java.io.IOException;

import javax.servlet.ServletException;

import org.eclipse.winery.highlevelrestapi.HighLevelRestApi;
import org.eclipse.winery.model.selfservice.ApplicationOption;
import org.opentosca.ui.vinothek.CONFIG;
import org.opentosca.ui.vinothek.CallbackManager;
import org.opentosca.ui.vinothek.VinothekContainerClient;
import org.opentosca.ui.vinothek.model.ApplicationInstance;
import org.opentosca.ui.vinothek.model.ApplicationWrapper;

/**
 * @author Kálmán Képes - kalman.kepes@iaas.uni-stuttgart.de
 *
 */
public class OpenTOSCAConnector implements TOSCARuntimeConnector {
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opentosca.ui.vinothek.integration.TOSCARuntimeConnector#
	 * instantiateServiceTemplate(java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public String instantiateServiceTemplate(String self, String containerUrl, String applicationId, String optionId, String sentPlanInputMessage) throws ServletException, IOException {
		
		// Determine application and option
		VinothekContainerClient client = new VinothekContainerClient(containerUrl);
		if (applicationId == null || applicationId.length() < 1 || optionId == null || optionId.length() < 1) {
			throw new RuntimeException("ApplicationId or OptionId unknown.");
		}
		
		// Retrieve application and application option
		ApplicationWrapper application = client.getApplication(applicationId);
		ApplicationOption selectedOption = null;
		for (ApplicationOption o : application.getOptions().getOption()) {
			if (o.getId().equals(optionId)) {
				selectedOption = o;
				break;
			}
		}
		if (selectedOption == null) {
			throw new RuntimeException("Option " + optionId + " does not exist.");
		}
		ApplicationInstance applicationInstance = new ApplicationInstance(applicationId, client.getContainerUrl());
		
		// Prepare plan invocation
		String planInputMessage = client.get(application, selectedOption.getPlanInputMessageUrl());
		
		if (sentPlanInputMessage != null && sentPlanInputMessage.length() > 1) {
			planInputMessage = sentPlanInputMessage;
		}
		
		// CSAR Name
		planInputMessage = planInputMessage.replace("%CSAR-NAME%", application.getCsarName());
		// InstanceData Entrypoint
		planInputMessage = planInputMessage.replace("%CONTAINER-API%", CONFIG.CONTAINER_API);
		// Callback Url
		String callbackEndpoint = self + "/CallbackEndpoint?callbackId=" + applicationInstance.getCallbackId();
		planInputMessage = planInputMessage.replace("%CALLBACK-URL%", callbackEndpoint);
		// Set Correlation Id
		planInputMessage = planInputMessage.replace("%CORRELATION-ID%", applicationInstance.getCallbackId());
		// set csar entrypoint on container
		planInputMessage = planInputMessage.replace("%CSARENTRYPOINT-URL%", CONFIG.CONTAINER_API + "/CSARs/" + application.getCsarName());
		// set callback address (the address where the invoker sends its
		// responses to) for the invoker (BPS 2.x needs this as it is unable to
		// fetch it itself)
		String planInvokerCallbackEndpoint = "http://" + client.getContainerHost() + ":9763/services/" + application.getCsarName() + "InvokerService/";
		planInputMessage = planInputMessage.replace("%PLANCALLBACKINVOKER-URL%", planInvokerCallbackEndpoint);
		
		// set url for instancedata api
		String instanceDataApiEndpoint = CONFIG.CONTAINER_API + "/instancedata";
		planInputMessage = planInputMessage.replace("%INSTANCEDATA-URL%", instanceDataApiEndpoint);
		
		// Send plan input message to plan endpoint
		String planEndpoint = "http://" + client.getContainerHost() + ":9763/services/" + selectedOption.getPlanServiceName() + "/";
		
		// FOR TESTING
		System.out.println("planEndpoint:" + planEndpoint);
		System.out.println("planInputMessage: " + planInputMessage);
		
		try {
			HighLevelRestApi.Post(planEndpoint, planInputMessage, "");
		} catch (Exception e) {
			throw new RuntimeException("Not able to POST plan input message to " + planEndpoint);
		}
		
		// Store application instance
		CallbackManager.addInstance(applicationInstance);
		
		return applicationInstance.getCallbackId();
	}
	
	@Override
	public String terminateServiceTemplate(String self, String containerUrl, String applicationId, String selfserviceServiceInstance) {
		VinothekContainerClient client = new VinothekContainerClient(containerUrl);
		
		ApplicationInstance applicationInstance = new ApplicationInstance(applicationId, client.getContainerUrl());
		
		ApplicationWrapper wrapper = client.getApplication(applicationId);
		ApplicationOption foundTerminationOption = null;
		for (ApplicationOption appOption : wrapper.getOptions().getOption()) {
			// find the first option that can terminate an instance
			if (appOption.getName().startsWith("Terminate")) {
				foundTerminationOption = appOption;
				break;
			}
		}
		
		String planInputMessage = client.get(wrapper, foundTerminationOption.getPlanInputMessageUrl());
		
		planInputMessage = planInputMessage.replace("%SERVICEINSTANCE-URL%", selfserviceServiceInstance);
		planInputMessage = planInputMessage.replace("%CORRELATION-ID%", applicationInstance.getCallbackId());
		// set url for instancedata api
		String instanceDataApiEndpoint = CONFIG.CONTAINER_API + "/instancedata";
		planInputMessage = planInputMessage.replace("%INSTANCEDATA-URL%", instanceDataApiEndpoint);
		
		// Callback Url
		String callbackEndpoint = self + "/CallbackEndpoint?callbackId=" + applicationInstance.getCallbackId();
		planInputMessage = planInputMessage.replace("%CALLBACK-URL%", callbackEndpoint);
		
		// set callback address (the address where the invoker sends its
		// responses to) for the invoker (BPS 2.x needs this as it is unable to
		// fetch it itself)
		String planInvokerCallbackEndpoint = "http://" + client.getContainerHost() + ":9763/services/" + wrapper.getCsarName() + "InvokerService/";
		planInputMessage = planInputMessage.replace("%PLANCALLBACKINVOKER-URL%", planInvokerCallbackEndpoint);
		
		// Send plan input message to plan endpoint
		String planEndpoint = "http://" + client.getContainerHost() + ":9763/services/" + foundTerminationOption.getPlanServiceName() + "/";
		
		// FOR TESTING
		System.out.println("planEndpoint:" + planEndpoint);
		System.out.println("planInputMessage: " + planInputMessage);
		
		try {
			HighLevelRestApi.Post(planEndpoint, planInputMessage, "");
		} catch (Exception e) {
			throw new RuntimeException("Not able to POST plan input message to " + planEndpoint);
		}
		
		// Store application instance
		CallbackManager.addInstance(applicationInstance);
		
		return applicationInstance.getCallbackId();
	}
	
}
