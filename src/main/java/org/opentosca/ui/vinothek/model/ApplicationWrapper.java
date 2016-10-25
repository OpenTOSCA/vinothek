package org.opentosca.ui.vinothek.model;

import java.util.List;

import org.eclipse.winery.model.selfservice.Application;
import org.eclipse.winery.model.selfservice.ApplicationOption;
import org.opentosca.model.tosca.TParameter;
import org.opentosca.model.tosca.TPlan;
import org.opentosca.ui.admin.action.client.ContainerClient;
import org.opentosca.ui.vinothek.CONFIG;

public class ApplicationWrapper extends Application {
	
	
	private String selfServiceBaseUrl = "";
	
	
	public void setSelfServiceBaseUrl(String selfServiceBaseUrl) {
		this.selfServiceBaseUrl = selfServiceBaseUrl;
		// Ensures that this URL ends with '/'
		if (!this.selfServiceBaseUrl.endsWith("/")) {
			this.selfServiceBaseUrl += "/";
		}
	}
	
	/**
	 * Base URL of the application's self service folder to retrieve files. Ends
	 * with '/'!
	 * 
	 * @return
	 */
	public String getSelfServiceBaseUrl() {
		return selfServiceBaseUrl;
	}
	
	/**
	 * Returns an absolute URL for a given relativeUrl of this application
	 * 
	 * @param relativeUrl
	 * @return
	 */
	public String convertToAbsoluteUrl(String relativeUrl) {
		// to prevent NPE
		if (relativeUrl == null) {
			return "";
		}
		
		// If path is absolute, return it
		if (relativeUrl.startsWith("http")) {
			return relativeUrl;
		}
		// If it starts with / remove it
		if (relativeUrl.startsWith("/")) {
			relativeUrl = relativeUrl.substring(1);
		}
		
		// If this is a relative URL it is relative to the self-service folder
		// This is how the OpenTOSCA REST API currently works
		return getSelfServiceBaseUrl() + relativeUrl;
	}
	
	/**
	 * Extracts the name of the CSAR file represented by this application.<br>
	 * E.g., Moodle.csar
	 * 
	 * @return
	 */
	public String getCsarName() {
		String csarName = getSelfServiceBaseUrl().replace(CONFIG.METADATA_FOLDER, "");
		return csarName.substring(csarName.lastIndexOf("/") + 1);
	}
	
	@Override
	public Options getOptions() {
		
		System.out.println("Get all plans for option selection");
		
		Options options = new Options();
		
		ContainerClient client = ContainerClient.getInstance();
		List<TPlan> plans = client.getPlanDTOs(getCsarName());
		
		for (TPlan plan : plans) {
			System.out.println("   " + plan.getId());
			
			ApplicationOption option = new ApplicationOption();
			option.setPlanServiceName(plan.getId());
			option.setDescription(plan.getId() + ": " + plan.getPlanType());
			option.setIconUrl("");
			option.setId(plan.getId());
			option.setName(plan.getId());
			options.getOption().add(option);
		}
		
		System.out.println("Default is: " + options.getOption().get(0).getId());
		
		return options;
	}
	
	public String getInputArray() {
		
		StringBuilder builder = new StringBuilder();
		
		ContainerClient client = ContainerClient.getInstance();
		builder.append("{");
		for (TPlan plan : client.getMinimalPlanDTO(getCsarName())) {
			if (null != plan.getInputParameters()) {
				builder.append("\"" + plan.getId() + "\":[");
				for (TParameter param : plan.getInputParameters().getInputParameter()) {
					builder.append("\"" + param.getName() + "\",");
				}
				builder.replace(builder.length() - 1, builder.length(), "]");
				builder.append("]");
			}
		}
		builder.replace(builder.length() - 1, builder.length(), "}");
		
		return builder.toString();
	}
	
	public String getPlanURLArray() {
		
		StringBuilder builder = new StringBuilder();
		
		ContainerClient client = ContainerClient.getInstance();
		builder.append("{");
		for (String url : client.getPOSTURLsOfPlans(getCsarName())) {
			if (null != url) {
				builder.append(url + ",");
			}
		}
		builder.replace(builder.length() - 1, builder.length(), "}");
		
		return builder.toString();
	}
	
	public String getPlanXMLArray() {
		
		StringBuilder builder = new StringBuilder();
		
		ContainerClient client = ContainerClient.getInstance();
		builder.append("{");
		for (String plan : client.getPlanDTOsAsXML(getCsarName())) {
			if (null != plan) {
				builder.append(plan + ",");
			}
		}
		builder.replace(builder.length() - 1, builder.length(), "}");
		
		return builder.toString();
	}
	
	public String getPlanXMLArray(String planName) {
		
		StringBuilder builder = new StringBuilder();
		
		ContainerClient client = ContainerClient.getInstance();
		builder.append("{");
		for (String plan : client.getPlanDTOsAsXML(getCsarName())) {
			if (null != plan && plan.contains("id=\"" + planName + "\"")) {
				System.out.println("Plan found: " + plan);
				builder.append(plan + ",");
			}
		}
		builder.replace(builder.length() - 1, builder.length(), "}");
		
		return builder.toString();
	}
	
	public String getMinimalPlanXML(String planName) {
		
		StringBuilder builder = new StringBuilder();
		
		ContainerClient client = ContainerClient.getInstance();
		//		builder.append("{");
		for (String plan : client.getMinimalPlanAsXML(getCsarName())) {
			if (null != plan && plan.contains("id=\"" + planName + "\"")) {
				System.out.println("Minimal plan found with ID " + planName + "\n" + plan);
				builder.append(plan);// + ",");
			}
		}
		//		builder.replace(builder.length() - 1, builder.length(), "}");
		
		System.out.println(builder.toString());
		return builder.toString();
	}
}
