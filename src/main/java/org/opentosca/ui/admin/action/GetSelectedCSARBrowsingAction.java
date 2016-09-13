package org.opentosca.ui.admin.action;

import java.util.ArrayList;
import java.util.List;

import org.opentosca.ui.admin.action.client.ContainerClient;

import com.opensymphony.xwork2.ActionSupport;

/**
 * Gets the selected path of the content inside a CSAR.
 * 
 * @author Christian Endres - endrescn@studi.informatik.uni-stuttgart.de
 * 
 */
public class GetSelectedCSARBrowsingAction extends ActionSupport {
	
	private static final long	serialVersionUID			= 6075840970234235142L;
	
	private String				selectedCSAR				= "";
	private List<String>		selectedCsarBrowsingList	= new ArrayList<String>();
	private String				selectedCSARContentPath		= "/";
	private String				selectedCSARTopologyURL		= "";
	
	
	@Override
	public String execute() {
		
		if (null == selectedCSAR || selectedCSAR.equals("")){
			return "success";
		}
	
		this.selectedCsarBrowsingList.clear();
		
		String relativePath = "";
		
		// there is a step back propagated, thus chop off the last part
		if (this.selectedCSARContentPath.endsWith("<<<")) {
			relativePath = this.selectedCSARContentPath;
			relativePath = relativePath.substring(0, relativePath.lastIndexOf("/"));
			relativePath = relativePath.substring(0, relativePath.lastIndexOf("/"));
			this.selectedCSARContentPath = relativePath;
		}
		// root is selected, thus is relative
		else if (this.selectedCSARContentPath.equals("/")) {
			relativePath = this.selectedCSARContentPath;
		}
		// a folder or file is selected, thus is relative
		else {
			relativePath = this.selectedCSARContentPath.concat("/");
		}
		
		// build the absolute path to the content
		String absolutePath = ContainerClient.URLdecode(ContainerClient.BASEURI.toString().concat("/CSARs/").concat(this.selectedCSAR).concat("/Content").concat(relativePath));
		// System.out.println(absolutePath);
		
		// get the content of the current folder
		ContainerClient client = ContainerClient.getInstance();
		List<String[]> links = client.getLinksFromUri(absolutePath, false);
		
		List<String> tempList = new ArrayList<String>();
		
		// "back button" always except in root
		if (!(relativePath.equals("") || relativePath.equals("/"))) {
			tempList.add("<<<");
		}
		
		// extract links from response and build up the "menu"
		if (links.size() > 0) {
			
			// extract each link
			for (int i = 0; i < links.size(); i++) {
				tempList.add(links.get(i)[0]);
			}
		}
		
		this.setSelectedCsarBrowsingList(tempList);
		
		this.selectedCSARTopologyURL = ContainerClient.BASEURI + "/CSARs/"
				+ this.selectedCSAR + "/TopologyPicture";
		
		this.selectedCSARContentPath = this.selectedCSARContentPath.concat("/");
		
		return "success";
		
	}
	
	public String getSelectedCSAR() {
	
		return this.selectedCSAR;
	}
	
	public List<String> getSelectedCsarBrowsingList() {
	
		return this.selectedCsarBrowsingList;
	}
	
	public String getSelectedCSARContentPath() {
	
		return this.selectedCSARContentPath;
	}
	
	public String getSelectedCSARTopologyURL() {
	
		return this.selectedCSARTopologyURL;
	}
	
	public void setSelectedCSAR(String selectedCSAR) {
	
		this.selectedCSAR = selectedCSAR;
	}
	
	public void setSelectedCsarBrowsingList(
			List<String> selectedCsarBrowsingList) {
	
		this.selectedCsarBrowsingList = selectedCsarBrowsingList;
	}
	
	public void setSelectedCSARContentPath(String selectedCSARContentPath) {
	
		System.out.println(selectedCSARContentPath);
		this.selectedCSARContentPath = selectedCSARContentPath;
	}
	
	public void setSelectedCSARTopologyURL(String selectedCSARTopologyURL) {
	
	}
	
}
