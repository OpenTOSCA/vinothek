package org.opentosca.ui.admin.action;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.opentosca.model.tosca.extension.transportextension.TPlanDTO;
import org.opentosca.ui.admin.action.client.ContainerClient;

import com.opensymphony.xwork2.ActionSupport;

/**
 * Get the active PublicPlans of a CSAR-Instance.
 * 
 * @author Christian Endres - endrescn@studi.informatik.uni-stuttgart.de
 * 
 */
public class GetCSARInstanceRunningPlansAction extends ActionSupport {

    private static final long	serialVersionUID	= -252865773415470831L;
    private String				csarID;
    private String				internalInstanceID;
    private List<String>		activePlans			= new ArrayList<String>();


    @Override
    public String execute() {

	System.out.println("get the running plans for displaying");

	ContainerClient client = ContainerClient.getInstance();

	// get the CorrelationID links
	List<String[]> value = client.getLinksFromUri(ContainerClient.BASEURI
	    + "/CSARs/" + csarID + "/Instances/"
	    + internalInstanceID + "/activePublicPlans", false);

	int size = value.size();
	System.out.println("running plans: " + size);

	if (size > 0) {

	    // means there are #size instances
	    List<String> correlationList = new ArrayList<String>();

	    // get the CorrelationIDs
	    for (int itr = 0; itr < size; itr++) {
		String res = value.get(itr)[0];
		System.out.println(res);
		correlationList.add(res);
	    }

	    // sort chronologically
	    Collections.sort(correlationList);

	    for (String buildCorrelation : correlationList) {

		// calculate the date of invocation
		long millis = Long.parseLong(buildCorrelation.substring(0, buildCorrelation.indexOf("-")));
		Date date = new Date(millis);
		System.out.println(millis);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String invocationDate = formatter.format(date);

		// get the PublicPlan and store the informations
		TPlanDTO plan = client.getActivePlanDTOs(csarID, internalInstanceID, buildCorrelation);
		activePlans.add(invocationDate + " "
		    + plan.getId());
	    }
	}

	return "success";
    }

    public List<String> getActivePlans() {

	return activePlans;
    }

    public String getCsarID() {

	return csarID;
    }

    public String getInternalInstanceID() {

	return internalInstanceID;
    }

    public void setActivePlans(List<String> activePlans) {

	this.activePlans = activePlans;
    }

    public void setCsarID(String csarID) {

	this.csarID = csarID;
    }

    public void setInternalInstanceID(String internalInstanceID) {

	this.internalInstanceID = internalInstanceID;
    }

}
