package org.opentosca.ui.vinothek;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.winery.highlevelrestapi.HighLevelRestApi;
import org.eclipse.winery.model.selfservice.ApplicationOption;
import org.opentosca.ui.vinothek.model.ApplicationInstance;
import org.opentosca.ui.vinothek.model.ApplicationWrapper;

@WebServlet("/ApplicationInstantiation")
public class ApplicationInstantiationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public ApplicationInstantiationServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("text/plain");		
		
		// Determine application and option
		VinothekContainerClient client = new VinothekContainerClient(request);		
		String applicationId = request.getParameter("applicationId");
		String optionId = request.getParameter("optionId");
		if(applicationId == null || applicationId.length() < 1 || optionId == null || optionId.length() < 1) {
			throw new RuntimeException("ApplicationId or OptionId unknown.");
		}
		
		// Retrieve application and application option
		ApplicationWrapper application = client.getApplication(applicationId);
		ApplicationOption selectedOption = null;
		for(ApplicationOption o : application.getOptions().getOption()) {
			if(o.getId().equals(optionId)) {
				selectedOption = o;
				break;
			}
		}
		if(selectedOption == null) {
			throw new RuntimeException("Option " + optionId + " does not exist.");
		}
		ApplicationInstance applicationInstance = new ApplicationInstance(applicationId, client.getContainerUrl());
	   	    	   
	    String sentPlanInputMessage = request.getParameter("xml");	   
	    
		// Prepare plan invocation
		String planInputMessage = client.get(application, selectedOption.getPlanInputMessageUrl());
		
		if(sentPlanInputMessage != null && sentPlanInputMessage.length() > 1){
			planInputMessage = sentPlanInputMessage;
		}
		
		// Adapt plan input message
		String self = request.getRequestURL().toString().replace("/ApplicationInstantiation", "");
		// CSAR Name
		planInputMessage = planInputMessage.replace("%CSAR-NAME%", application.getCsarName());
		// InstanceData Entrypoint
		planInputMessage = planInputMessage.replace("%CONTAINER-API%", CONFIG.CONTAINER_API);
		// Callback Url
		String callbackEndpoint =  self + "/CallbackEndpoint?callbackId=" + applicationInstance.getCallbackId();
		planInputMessage = planInputMessage.replace("%CALLBACK-URL%", callbackEndpoint);
		// Set Correlation Id
		planInputMessage = planInputMessage.replace("%CORRELATION-ID%", applicationInstance.getCallbackId());
		// set csar entrypoint on container
		planInputMessage = planInputMessage.replace("%CSARENTRYPOINT-URL%", CONFIG.CONTAINER_API + "/CSARs/" + application.getCsarName());
		// set callback address for the invoker ( BPS 2.x needs this as it is unable to fetch it itself
		String planInvokerCallbackEndpoint = "http://" + client.getContainerHost() + ":9763/services/InvokerService/";
		planInputMessage = planInputMessage.replace("%PLANCALLBACKINVOKER-URL%", planInvokerCallbackEndpoint);
		
		// set url for instancedata api
		String instanceDataApiEndpoint = CONFIG.CONTAINER_API +"/instancedata";
		planInputMessage = planInputMessage.replace("%INSTANCEDATA-URL%", instanceDataApiEndpoint);
		
		// Send plan input message to plan endpoint
		String planEndpoint = "http://" + client.getContainerHost() + ":9763/services/" + selectedOption.getPlanServiceName() + "/";
		
		// FOR TESTING
		System.out.println("planEndpoint:" + planEndpoint);
		System.out.println("planInputMessage: " + planInputMessage);
		
		try {
			HighLevelRestApi.Post(planEndpoint, planInputMessage, "");
		} catch (Exception e) {
			response.getWriter().append("ERROR DURING POST");
			throw new RuntimeException("Not able to POST plan input message to " + planEndpoint);
		}

		// Store application instance
		CallbackManager.addInstance(applicationInstance);
		
		// Returning URL to poll for result
		response.getWriter().append(self + "/CallbackStatus?callbackId=" + applicationInstance.getCallbackId());
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().append("No get implemented");
		System.out.println("No get implemented");
	}

}
