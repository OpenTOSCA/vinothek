package org.opentosca.ui.vinothek;

import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.opentosca.ui.vinothek.model.ApplicationUnmarshaller;
import org.opentosca.ui.vinothek.model.ApplicationWrapper;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

public class VinothekContainerClient {

	private final String containerHost;
	private final int containerPort;
	private final Client jerseyClient;

	public VinothekContainerClient(HttpServletRequest req) {
		String containerTmp = req.getParameter("container");
		
		// Use localhost if container parameter is not set
		if (containerTmp == null) {
			// We cannot just set 'localhost', because when connection from the
			// browser to a remote server localhost is the actual hostname of
			// the server.
			String self = req.getRequestURL().toString();
			
			try {
				URL url = new URL(self);
				containerTmp = url.getHost();
			} catch (MalformedURLException e) {
				e.printStackTrace();
				containerTmp = "ERROR";
			}
		}
		containerTmp = containerTmp.replaceAll("http://", "").trim();
		if (containerTmp.contains(":")) {
			String[] split = containerTmp.split(":");
			if (split.length != 2) {
				throw new RuntimeException("Invalid Syntax of container parameter (" + containerTmp + ").");
			}
			this.containerHost = split[0];
			this.containerPort = Integer.parseInt(split[1]);
		} else {
			this.containerHost = containerTmp;
			this.containerPort = CONFIG.DEFAULT_CONTAINER_PORT;
		}

		// Create Jersey Client
		ClientConfig config = new DefaultClientConfig();
		this.jerseyClient = Client.create(config);
	}

	/**
	 * Returns URL of current container in the form: http://<host>:<port>
	 * 
	 * @return
	 */
	public String getContainerUrl() {
		return "http://" + this.containerHost + ":" + this.containerPort;
	}

	public String getContainerHost() {
		return this.containerHost;
	}

	public Map<String, ApplicationWrapper> getApplications() {
		Map<String, ApplicationWrapper> apps = new HashMap<String, ApplicationWrapper>();
		String csarUrl = getContainerUrl() + CONFIG.CSAR_LIST_REL_URL;
		
		try {
			String ret = this.jerseyClient.resource(csarUrl).accept(MediaType.MEDIA_TYPE_WILDCARD).get(String.class);
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder builder = factory.newDocumentBuilder();
	        Document parse = builder.parse(new InputSource(new StringReader(ret)));
	        NodeList elementsByTagName = parse.getElementsByTagName("Reference");
	        
	        for (int i=0; i<elementsByTagName.getLength(); i++) {
				Node item = elementsByTagName.item(i);
				String csarBaseUrl = item.getAttributes().getNamedItem("xlink:href").getNodeValue();
				// "self" is no application
				if(item.getAttributes().getNamedItem("xlink:title").getNodeValue().equals("Self")) {
					continue;
				}
				
				// Create Self Service Base Url
				String selfServiceBaseUrl = csarBaseUrl + CONFIG.METADATA_FOLDER;
				String data = get(selfServiceBaseUrl + CONFIG.METADATA_FILE);
				if(data == null || data.isEmpty()) {
					continue;
				}
				
				// Create Application
				ApplicationWrapper application = ApplicationUnmarshaller.unmarshall(data);
				application.setSelfServiceBaseUrl(selfServiceBaseUrl);
				apps.put(selfServiceBaseUrl, application);
			}
	        
		} catch (com.sun.jersey.api.client.ClientHandlerException e) {
			if(e.getCause() != null && e.getCause() instanceof java.net.ConnectException) {
				throw new CannotConnectToContainerException(getContainerUrl(), e.getCause());
			} else {
				throw new RuntimeException("Failed to get applications from " + csarUrl, e);
			}
			
		} catch (com.sun.jersey.api.client.UniformInterfaceException e) {
			throw new CannotConnectToContainerException(csarUrl, (e.getCause() != null) ? e.getCause() : e);
        
		} catch (Exception e) {
			throw new RuntimeException("Failed to get applications from " + csarUrl, e);
		}

		return apps;
	}

	public ApplicationWrapper getApplication(String applicationId) {
		return getApplications().get(applicationId);
	}
	
	private String get(String url) {
		System.out.println("Getting file: " + url);
		try {
			String ret = this.jerseyClient.resource(url)
					.accept(MediaType.APPLICATION_OCTET_STREAM).get(String.class);
			// System.out.println(" ^ " + ret);
			return ret;
		} catch (Throwable e) {
			System.err.println("Unable to load SelfServiceFile " + url);
			return "";
		}
	}

	public String get(ApplicationWrapper application, String url) {
		return get(application.convertToAbsoluteUrl(url));
	}		

}
