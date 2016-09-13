package org.opentosca.ui.admin.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.opentosca.model.tosca.extension.transportextension.TPlanDTO;
import org.opentosca.ui.admin.action.client.ContainerClient;

import com.opensymphony.xwork2.ActionSupport;

/**
 * 
 * 
 * @author Christian Endres - endrescn@studi.informatik.uni-stuttgart.de
 * 
 */
public class GetCSARInstancePlanHistoryAction extends ActionSupport {

    private static final long serialVersionUID = -252865773415470831L;
    private String csarID;
    private String internalInstanceID;
    private List<TPlanDTO> plans = new ArrayList<TPlanDTO>();

    @Override
    public String execute() {

	System.out.println("get the plans from history");

	ContainerClient client = ContainerClient.getInstance();

	// get the CorrelationID links from History
	List<String[]> value = client.getLinksFromUri(
	    ContainerClient.BASEURI + "/CSARs/" + csarID + "/Instances/" + internalInstanceID + "/history", false);

	int size = value.size();
	System.out.println("Plans in history: " + size);

	if (size > 0) {
	    // means there are #size instances
	    System.out.println("There are " + size + " plans in history");

	    List<String> correlationList = new ArrayList<String>();

	    // get the CorrelationIDs
	    for (int itr = 0; itr < size; itr++) {
		String res = value.get(itr)[0];
		correlationList.add(res);
	    }

	    // sort chronologically
	    Collections.sort(correlationList);

	    for (String correlation : correlationList) {

		System.out.println("correlation: " + correlation);

		// calculate the date of invocation
		// long millis = Long.parseLong(correlation.substring(0,
		// correlation.indexOf("-")));
		// Date date = new Date(millis);
		// SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd
		// HH:mm:ss");
		// String invocationDate = formatter.format(date);

		TPlanDTO plan = client.getPlanDTOFromHistory(csarID, internalInstanceID, correlation);
		// plan.setInterfaceName(invocationDate);
		// plan.setOperationName(correlation);

		// store the PublicPlan to the list
		plans.add(plan);
	    }
	}

	return "success";
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
