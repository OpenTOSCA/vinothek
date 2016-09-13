package org.opentosca.ui.admin.action;

import java.util.List;

import org.opentosca.ui.admin.action.client.ContainerClient;

import com.opensymphony.xwork2.ActionSupport;

/**
 * This Action recieves a File of the Client Browser which should be a Cloud
 * Service Archive. The it takes it and uploads it to OpenTOSCA.
 * 
 * @author Christian Endres - endrescn@studi.informatik.uni-stuttgart.de
 * 
 */
public class UploadCSARFromURLAction extends ActionSupport {

	private static final long serialVersionUID = -5536842710743698964L;
	private String urlToUpload = "";

	@Override
	public String execute() {

		ContainerClient client = ContainerClient.getInstance();

		List<String> result = client.uploadCSARDueURL(urlToUpload);
		if (result.get(0).equalsIgnoreCase("Created")) {
			return "success";
		} else {
			System.out.println(result.get(0));
			return "success";
		}
	}

	public String getUrlToUpload() {
		return urlToUpload;
	}

	public void setUrlToUpload(String urlToUpload) {
		this.urlToUpload = urlToUpload;
	}

}
