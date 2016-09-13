package org.opentosca.ui.admin.action;

import java.util.ArrayList;
import java.util.List;

import org.opentosca.model.tosca.TPlan;
import org.opentosca.ui.admin.action.client.ContainerClient;

import com.opensymphony.xwork2.ActionSupport;

/**
 * Gets the PublicPlans of a specific type (BUILD, OTHERMANAGEMENT, TERMINATION)
 * of a CSAR.
 * 
 * @author Christian Endres - endrescn@studi.informatik.uni-stuttgart.de
 * 
 */
public class GetSelectedCSARPublicPlansAction extends ActionSupport {
	
	
	private static final long serialVersionUID = -1306906449002508071L;
	
	private String selectedCSAR = "";
	// private String planType = "";
	private List<TPlan> plans = new ArrayList<TPlan>();
	// the PublicPlan ID
	
	
	@Override
	public String execute() {
		
		ContainerClient client = ContainerClient.getInstance();
		
		plans.addAll(client.getPlanDTOs(selectedCSAR));
		
		// // get the links to the PublicPlans (contains the PublicPlan ID)
		// List<String[]> value = client.getLinksFromUri(
		// ContainerClient.BASEURI + "/CSARs/" + selectedCSAR + "/PublicPlans/"
		// + planType, false);
		//
		// int size = value.size();
		//
		// if (size > 0) {
		// // means there are #size PublicPlans
		// System.out.println("There are " + size + " PublicPlans for CSAR " +
		// selectedCSAR);
		//
		// for (int itr = 0; itr < size; itr++) {
		//
		// // get each PublicPlan
		// String res = value.get(itr)[1];
		// int id = Integer.parseInt(res.substring(res.lastIndexOf("/") + 1));
		// List<TPlanDTO> plan = client.getPlanDTOs(selectedCSAR);
		//
		// // store the PublicPlan to the return list
		// plans.add(plan);
		// }
		// }
		
		return "success";
	}
	
	// public String getPlanType() {
	//
	// return planType;
	// }
	
	public List<TPlan> getPublicPlans() {
		
		return plans;
	}
	
	public String getSelectedCSAR() {
		
		return selectedCSAR;
	}
	
	// public void setPlanType(String planType) {
	//
	// this.planType = planType;
	// }
	
	public void setPublicPlans(List<TPlan> publicPlans) {
		
		plans = publicPlans;
	}
	
	public void setSelectedCSAR(String selectedCSAR) {
		
		this.selectedCSAR = selectedCSAR;
	}
}
