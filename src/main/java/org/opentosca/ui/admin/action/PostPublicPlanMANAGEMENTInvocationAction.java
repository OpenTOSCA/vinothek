package org.opentosca.ui.admin.action;

import java.util.List;

import javax.xml.namespace.QName;

import org.opentosca.model.tosca.extension.transportextension.TParameterDTO;
import org.opentosca.model.tosca.extension.transportextension.TPlanDTO;
import org.opentosca.ui.admin.action.client.ContainerClient;

import com.opensymphony.xwork2.ActionSupport;

/**
 * POST of a OTHERMANAGEMENT PublicPlan.
 * 
 * @author Christian Endres - endrescn@studi.informatik.uni-stuttgart.de
 * 
 */
public class PostPublicPlanMANAGEMENTInvocationAction extends ActionSupport {

    private static final long serialVersionUID = -5469450674071901529L;

    private String csarID;
    private String planType = "OTHERMANAGEMENT";

    private String planID;

    private String parameters;

    private int internalID = -1;
    private String internalInstanceID;

    @Override
    public String execute() {

	// set the needed informations
	TPlanDTO plan = new TPlanDTO();
	// publicPlan.setCSARID(csarID);
	plan.setPlanType(planType);
	// publicPlan.setInternalPlanID(internalID);
	plan.setId(new QName(planID));
	// publicPlan.setInternalInstanceInternalID(Integer.parseInt(internalInstanceID));

	// extract the parameters
	for (String str : parameters.split("\\$\\$\\$\\$\\$\\$\\$\\$\\$\\$\\$\\$\\$!")) {
	    TParameterDTO param = new TParameterDTO();
	    String[] str2 = str.split("\\$\\$\\$\\$\\$\\$\\$\\$\\$\\$\\$\\$\\$");
	    param.setName(str2[0]);
	    if (str2.length == 2) {
		param.setValue(str2[1]);
	    } else {
		param.setValue("");
	    }

	    plan.getInputParameters().getInputParameter().add(param);
	}

	// post
	ContainerClient client = ContainerClient.getInstance();
	List<String> result = client.postNonBUILDPlanDTO(csarID, plan, Integer.parseInt(internalInstanceID));

	for (String str : result) {
	    System.out.println(str);
	}

	return result.get(0);
    }

    public String getCsarID() {

	return csarID;
    }

    public int getInternalID() {

	return internalID;
    }

    public String getInternalInstanceID() {

	return internalInstanceID;
    }

    public String getParameters() {

	return parameters;
    }

    public String getPlanID() {

	return planID;
    }

    public String getPlanType() {

	return planType;
    }

    public void setCsarID(String csarID) {

	this.csarID = csarID;
    }

    public void setInternalID(int internalID) {

	this.internalID = internalID;
    }

    public void setInternalInstanceID(String internalInstanceID) {

	this.internalInstanceID = internalInstanceID;
    }

    public void setParameters(String parameters) {

	this.parameters = parameters;
    }

    public void setPlanID(String planID) {

	this.planID = planID;
    }

    public void setPlanType(String planType) {

	this.planType = planType;
    }

}
