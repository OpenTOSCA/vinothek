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
 * 
 * This class is the Struts2 Action which gathers the BUILD informations of a
 * CSAR Instance from the container.
 * 
 * @author Christian Endres - endrescn@studi.informatik.uni-stuttgart.de
 * 
 */
public class GetBuildInformationsFromHistoryAction extends ActionSupport {

    private static final long serialVersionUID = -252865773415470831L;
    private String csarID;
    private String internalInstanceID;
    private String invocationDate = "";
    private TPlanDTO plan;

    @Override
    public String execute() {

	System.out.println("get the build plan from history");

	ContainerClient client = ContainerClient.getInstance();

	// get all the CorrelationIDs in History for CSARID and InstanceID
	List<String[]> correlationIDStringArrays = client.getLinksFromUri(
	    ContainerClient.BASEURI + "/CSARs/" + csarID + "/Instances/" + internalInstanceID + "/history", false);

	int size = correlationIDStringArrays.size();
	System.out.println("Plans in history: " + size);

	// there is one or more PublicPlan in History, thus the BUILD is done
	if (size > 0) {
	    System.out.println("There are " + size + " plans in history");

	    List<String> correlationList = new ArrayList<String>();

	    // get the CorrelationIDs
	    for (int itr = 0; itr < size; itr++) {
		String res = correlationIDStringArrays.get(itr)[0];
		System.out.println(res);
		correlationList.add(res);
	    }

	    // sort, so the first one is the build plan (because of timestamp)
	    Collections.sort(correlationList);

	    String buildCorrelation = correlationList.get(0);

	    System.out.println("build correlation: " + buildCorrelation);

	    // calculate the date of invocation
	    long millis = Long.parseLong(buildCorrelation.substring(0, buildCorrelation.indexOf("-")));
	    System.out.println(millis);
	    Date date = new Date(millis);
	    SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	    invocationDate = formatter.format(date);
	    System.out.println(invocationDate);

	    // get the PublicPlan
	    plan = client.getPlanDTOFromHistory(csarID, internalInstanceID, buildCorrelation);
	    System.out.println(plan.getId());
	}

	return "success";
    }

    public String getCsarID() {

	return csarID;
    }

    public String getInternalInstanceID() {

	return internalInstanceID;
    }

    public String getInvocationDate() {

	return invocationDate;
    }

    public TPlanDTO getPublicPlan() {

	return plan;
    }

    public void setCsarID(String csarID) {

	this.csarID = csarID;
    }

    public void setInternalInstanceID(String internalInstanceID) {

	this.internalInstanceID = internalInstanceID;
    }

    public void setInvocationDate(String invocationDate) {

	this.invocationDate = invocationDate;
    }

    public void setPublicPlan(TPlanDTO plan) {

	this.plan = plan;
    }

}
