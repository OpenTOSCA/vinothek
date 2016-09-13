package org.opentosca.ui.admin.action;

import org.opentosca.ui.admin.action.client.ContainerClient;

import com.opensymphony.xwork2.ActionSupport;

/**
 * 
 * This class is the Struts2 Action which sends a deletion request to OpenTOSCA.
 * 
 * @author Christian Endres - endrescn@studi.informatik.uni-stuttgart.de
 * 
 */
public class DeleteCSARAction extends ActionSupport {
	
	private static final long	serialVersionUID	= -6150522857920548025L;
	
	private String				selectedCSAR		= "";
	
	
	/**
	 * Uses the ContainerClient to send the deletion request of selected CSAR.
	 */
	@Override
	public String execute() {
	
		System.out.println("execute delete of " + this.selectedCSAR);
		
		ContainerClient client = ContainerClient.getInstance();
		client.deleteCSAR(this.selectedCSAR);
		
		return "success";
	}
	
	public String getSelectedCSAR() {
	
		return this.selectedCSAR;
	}
	
	public void setSelectedCSAR(String selectedCSAR) {
	
		this.selectedCSAR = selectedCSAR;
	}
}
