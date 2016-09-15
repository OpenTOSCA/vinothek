package org.opentosca.ui.vinothek.model;

import org.opentosca.ui.vinothek.CallbackManager;

public class ApplicationInstance {
	
	
	private final String applicationId;
	private final String containerUrl;
	private final String callbackId;
	private String endpointUrl = null;
	private String selfserviceMessage = null;
	private String selfservicePolicyMessage = null;
	private String selfserviceStatus = null;
	private String message = null;
	private String selfserviceServiceInstance = null;
	
	
	public ApplicationInstance(String applicationId, String container) {
		this.applicationId = applicationId;
		containerUrl = container;
		callbackId = CallbackManager.generateCallbackId(applicationId);
	}
	
	public String getApplicationId() {
		return applicationId;
	}
	
	public String getCallbackId() {
		return callbackId;
	}
	
	public String getEndpointUrl() {
		return endpointUrl;
	}
	
	public void setEndpointUrl(String endpointUrl) {
		this.endpointUrl = endpointUrl;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getContainerUrl() {
		return containerUrl;
	}
	
	public String getSelfserviceMessage() {
		return selfserviceMessage;
	}
	
	public void setSelfserviceMessage(String selfserviceMessage) {
		this.selfserviceMessage = selfserviceMessage;
	}
	
	public String getSelfservicePolicyMessage() {
		return selfservicePolicyMessage;
	}
	
	public void setSelfservicePolicyMessage(String selfservicePolicyMessage) {
		this.selfservicePolicyMessage = selfservicePolicyMessage;
	}
	
	public String getSelfserviceStatus() {
		return selfserviceStatus;
	}
	
	public void setSelfserviceStatus(String selfserviceStatus) {
		this.selfserviceStatus = selfserviceStatus;
	}
	
	public void setSelfserviceServiceInstance(String selfserviceServiceInstance) {
		this.selfserviceServiceInstance = selfserviceServiceInstance;
	}
	
	public String getSelfserviceServiceInstance() {
		return selfserviceServiceInstance;
	}
	
}
