package org.opentosca.ui.admin.action;

import java.net.URI;

import org.opentosca.ui.admin.action.client.ContainerClient;

import com.opensymphony.xwork2.ActionSupport;

public class GetContainerBaseURI extends ActionSupport {
	
	private static final long serialVersionUID = 3873685154540964423L;
	
	String url;
	String host;
	
	int port;
	
	
	@Override
	public String execute() {
		URI uri = ContainerClient.BASEURI;
		
		this.url = uri.toString();
		this.host = uri.getHost();
		this.port = uri.getPort();
		
		return "success";
	}
	
	public String getHost() {
		return this.host;
	}
	
	public void setHost(String host) {
		this.host = host;
	}
	
	public String getUrl() {
		return this.host;
	}
	
	public void setUrl(String url) {
		this.host = url;
	}
	
	public int getPort() {
		return this.port;
	}
	
	public void setPort(int port) {
		this.port = port;
	}
}
