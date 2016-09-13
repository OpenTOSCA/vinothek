package org.opentosca.ui.admin.action;

import java.util.ArrayList;
import java.util.List;

import org.opentosca.ui.admin.action.client.ContainerClient;

import com.opensymphony.xwork2.ActionSupport;

/**
 * Get the list of CSARID String representations of CSARs stored in OpenTOSCA.
 * 
 * @author Christian Endres - endrescn@studi.informatik.uni-stuttgart.de
 * 
 */
public class GetUploadedCSARListAction extends ActionSupport {
	
	private static final long	serialVersionUID	= -2399141659533670717L;
	
	private List<String>		csars				= new ArrayList<String>();
	private String				selectedCSAR;
	
	
	@Override
	public String execute() {
	
		ContainerClient client = ContainerClient.getInstance();
		
		// get all the CSARID links
		List<String[]> value = client.getLinksFromUri(ContainerClient.BASEURI
				+ "/CSARs", false);
		
		int size = value.size();
		
		// extract the CSARIDs and cut off the ".csar" at the end
		if (size > 0) {
			for (int i = 0; i < size; i++) {
				String csarName = value.get(i)[0];
				this.csars.add(csarName.substring(0, csarName.lastIndexOf(".")));
			}
		}
		
		// Add this as entry to show, that there is no CSAR stored.
		if (0 == this.csars.size()) {
			this.csars.add("No CSARs uploaded.");
		}
		
		return "success";
		
	}
	
	public List<String> getCsars() {
	
		return this.csars;
	}
	
	public String getSelectedCSAR() {
	
		return this.selectedCSAR;
	}
	
	public void setCsars(List<String> csars) {
	
		this.csars = csars;
	}
	
	public void setSelectedCSAR(String selectedCSAR) {
	
		this.selectedCSAR = selectedCSAR;
	}
	
}
