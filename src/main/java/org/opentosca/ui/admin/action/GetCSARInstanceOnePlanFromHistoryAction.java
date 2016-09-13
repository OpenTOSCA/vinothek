package org.opentosca.ui.admin.action;

import java.util.ArrayList;
import java.util.List;

import org.opentosca.model.tosca.extension.transportextension.TPlanDTO;
import org.opentosca.ui.admin.action.client.ContainerClient;

import com.opensymphony.xwork2.ActionSupport;

/**
 * Gathers up a specific PublicPlan from History.
 * 
 * @author Christian Endres - endrescn@studi.informatik.uni-stuttgart.de
 * 
 */
public class GetCSARInstanceOnePlanFromHistoryAction extends ActionSupport {

    private static final long serialVersionUID = -252865773415470831L;
    private String csarID;
    private String internalInstanceID;
    private String correlationID;
    private List<TPlanDTO> plans = new ArrayList<TPlanDTO>();

    /**
     * Gathers up a specific PublicPlan from History.
     */
    @Override
    public String execute() {

	System.out.println("get the plans from history");

	ContainerClient client = ContainerClient.getInstance();

	// get the PublicPlan
	TPlanDTO publicPlan = client.getPlanDTOFromHistory(csarID, internalInstanceID, correlationID);

	plans.add(publicPlan);

	return "success";
    }

    public String getCorrelationID() {

	return correlationID;
    }

    public String getCsarID() {

	return csarID;
    }

    public String getInternalInstanceID() {

	return internalInstanceID;
    }

    public List<TPlanDTO> getPlans() {

	return plans;
    }

    public void setCorrelationID(String correlationID) {

	this.correlationID = correlationID;
    }

    public void setCsarID(String csarID) {

	this.csarID = csarID;
    }

    public void setInternalInstanceID(String internalInstanceID) {

	this.internalInstanceID = internalInstanceID;
    }

    public void setPlans(List<TPlanDTO> plans) {

	this.plans = plans;
    }

}
