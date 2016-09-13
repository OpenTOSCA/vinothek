package org.opentosca.ui.admin.action;

import org.opentosca.ui.admin.action.client.ContainerClient;

import com.opensymphony.xwork2.ActionSupport;

/**
 * Get the upload status of a CSAR. With this you can check if TOSCA is
 * processed, IAs are deployed and BPEL-Plans are deployed.
 * 
 * @author Christian Endres - endrescn@studi.informatik.uni-stuttgart.de
 * 
 */
public class GetSelectedCSARUploadStatusAction extends ActionSupport {
	
	private static final long	serialVersionUID	= 9085497808371207836L;
	
	private String				selectedCSAR;
	private String				state				= "";
	
	
	@Override
	public String execute() {
	
		ContainerClient client = ContainerClient.getInstance();
		
		this.state = client.getState(this.selectedCSAR);
		
		return "success";
	}
	
	public String getSelectedCSAR() {
	
		return this.selectedCSAR;
	}
	
	public String getState() {
	
		return this.state;
	}
	
	public void setSelectedCSAR(String selectedCSAR) {
	
		this.selectedCSAR = selectedCSAR;
	}
	
	public void setState(String state) {
	
		this.state = state;
	}
	
}
