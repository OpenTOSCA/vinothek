package org.opentosca.ui.admin.action;

import java.util.List;

import javax.xml.namespace.QName;

import org.opentosca.model.tosca.extension.transportextension.TParameterDTO;
import org.opentosca.model.tosca.extension.transportextension.TPlanDTO;
import org.opentosca.ui.admin.action.client.ContainerClient;
import org.opentosca.ui.admin.action.model.Parameter;

import com.opensymphony.xwork2.ActionSupport;
import com.sun.jersey.api.client.ClientResponse;

/**
 * This Action is a PUT invocation of a BUILD PublicPlan.
 * 
 * @author Christian Endres - endrescn@studi.informatik.uni-stuttgart.de
 * 
 */
public class PutPublicPlanBUILDInvocationAction extends ActionSupport {
	
	
	private static final long serialVersionUID = -5469450674071901529L;
	
	private String csarID;
	private String planID;
	private List<Parameter> parameters;
	
	
	@Override
	public String execute() {
		
		// set all the needed informations
		TPlanDTO plan = new TPlanDTO();
		// plan.setCSARID(csarID);
		// plan.setInternalPlanID(internalID);
		plan.setId(new QName(planID));
		
		// skip the parameter types correlation, callbackaddress,
		// containerApiAddress, and csarName
		if (null != parameters) {
			for (Parameter param : parameters) {
				// if (!(param.getType().equals("correlation") ||
				// param.getType().equals("callbackaddress")
				// || param.getType().equals("containerApiAddress") ||
				// param.getType().equals("csarName"))) {
				TParameterDTO paramDTO = new TParameterDTO();
				paramDTO.setName(param.getName());
				paramDTO.setValue(param.getValue());
				paramDTO.setType(param.getType());
				plan.getInputParameters().getInputParameter().add(paramDTO);
				// }
			}
		}
		
		System.out.println("invoke build plan " + plan.getId().toString());
		
		// PUT
		ContainerClient client = ContainerClient.getInstance();
		ClientResponse result = client.postBUILDPlanDTO(csarID, plan);
		
		String res = result.getEntity(String.class);
		
		System.out.println(result.getStatus() + ": " + res);
		
		return res;
	}
	
	public String getCsarID() {
		
		return csarID;
	}
	
	public List<Parameter> getParameters() {
		
		return parameters;
	}
	
	public String getPlanID() {
		
		return planID;
	}
	
	public void setCsarID(String csarID) {
		
		this.csarID = csarID;
	}
	
	public void setParameters(List<Parameter> parameters) {
		
		this.parameters = parameters;
	}
	
	public void setPlanID(String planID) {
		
		this.planID = planID;
	}
}
