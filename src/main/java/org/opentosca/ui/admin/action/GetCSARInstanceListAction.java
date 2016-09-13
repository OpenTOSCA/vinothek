package org.opentosca.ui.admin.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.opentosca.ui.admin.action.client.ContainerClient;

import com.opensymphony.xwork2.ActionSupport;

/**
 * 
 * This Class gathers up the CSAR-Instances of a CSAR.
 * 
 * @author Christian Endres - endrescn@studi.informatik.uni-stuttgart.de
 * 
 */
public class GetCSARInstanceListAction extends ActionSupport {
	
	private static final long	serialVersionUID	= 6574166362364524165L;
	
	private String				selectedCSAR;
	private List<String>		availableInstances	= new ArrayList<String>();
	
	
	/**
	 * Gets the CSAR-Instances.
	 */
	@Override
	public String execute() {
	
		ContainerClient client = ContainerClient.getInstance();
		
		// get the links of the CSAR-Instances
		List<String[]> value = client.getLinksFromUri(ContainerClient.BASEURI
				+ "/CSARs/" + this.selectedCSAR + "/Instances", false);
		
		int size = value.size();
		
		if (size > 0) {
			// means there are #size instances
			System.out.println("There are " + size + " instances for CSAR "
					+ this.selectedCSAR);
			
			// get the number and produce the name (ie: Instance 0)
			for (int itr = 0; itr < size; itr++) {
				String res = value.get(itr)[0];
				System.out.println(res);
				this.availableInstances.add("Instance " + res);
			}
			
			// sort
			Collections.sort(this.availableInstances);
		}
		
		return "success";
	}
	
	public List<String> getAvailableInstances() {
	
		return this.availableInstances;
	}
	
	public String getSelectedCSAR() {
	
		return this.selectedCSAR;
	}
	
	public void setAvailableInstances(List<String> availableInstances) {
	
		this.availableInstances = availableInstances;
	}
	
	public void setSelectedCSAR(String selectedCSAR) {
	
		this.selectedCSAR = selectedCSAR;
	}
	
}
