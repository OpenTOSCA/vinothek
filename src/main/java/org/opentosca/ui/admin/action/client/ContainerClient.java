package org.opentosca.ui.admin.action.client;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.struts2.ServletActionContext;
import org.opentosca.model.tosca.TPlan;
import org.opentosca.model.tosca.extension.transportextension.TParameterDTO;
import org.opentosca.model.tosca.extension.transportextension.TPlanDTO;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;

/**
 * Copyright 2012 IAAS University of Stuttgart <br>
 * <br>
 * Customized version of original ContainerClient by Markus to be used in a WAR
 * Frontend for openTosca.
 * 
 * @author Christian Endres - endrescn@studi.informatik.uni-stuttgart.de
 * 
 *         Original version programmed by:
 * @author Markus Fischer - fischema@studi.informatik.uni-stuttgart.de
 * @author Nedim Karaoguz - karaognm@studi.informatik.uni-stuttgart.de
 * 
 */
public class ContainerClient {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4221259341944728001L;
	
	public static URI BASEURI;
	private Client client;
	
	// Singleton Pattern
	private static final ContainerClient INSTANCE = new ContainerClient();
	
	
	public static ContainerClient getInstance() {
		return ContainerClient.INSTANCE;
	}
	
	/**
	 * To decode a encoded URL back to original
	 * 
	 * @param encodedURL as String
	 * @return decoded URL
	 */
	public static String URLdecode(String encodedURL) {
		
		try {
			return URLDecoder.decode(encodedURL, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException();
		}
	}
	
	/**
	 * To Encode a URL
	 * 
	 * @param url as String
	 * @return encoded URL
	 */
	public static String URLencode(String url) {
		
		try {
			return URLEncoder.encode(url, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException();
		}
	}
	
	/**
	 * Constructor
	 */
	private ContainerClient() {
		
		ClientConfig config = new DefaultClientConfig();
		client = Client.create(config);
		client.setChunkedEncodingSize(1024);
		
		// We assume that the OpenTOSC container is running on port 1337 on the
		// same machine as the GUI Backend.
		// TODO Move Container API port and path information to e.g. external
		// properties file, should not be fixed in the code.
		String host;
		try {
			host = ServletActionContext.getRequest().getServerName();
		} catch (NullPointerException e) {
			host = "localhost";
		}
		ContainerClient.BASEURI = UriBuilder.fromUri("http://" + host + ":1337/containerapi").build();
		
	}
	
	public List<String> deleteCSAR(String csarId) {
		
		List<String> result = new ArrayList<String>();
		ClientResponse resp = null;
		try {
			resp = getBaseService().path("CSARs").path(csarId).delete(ClientResponse.class);
			if (!resp.getClientResponseStatus().equals(Status.OK)) {
				System.out.println("Error occured while deleting Csar " + csarId + ". Server returned: " + resp.getClientResponseStatus());
				result.add("Delete Error, Server returned" + resp.getClientResponseStatus());
			}
		} catch (ClientHandlerException e) {
			System.out.println("An Error occurred while deleting! Maybe the Container is not running or cannot be accessed.");
			System.out.println(e);
			result.add("An Error occurred while deleting! Maybe the Container is not running or cannot be accessed.");
		}
		return result;
	}
	
	public void destroy() {
		
		client.destroy();
	}
	
	public File downloadFile(String relativeFileLocation) {
		
		File ret = getGenericService(relativeFileLocation).accept(MediaType.APPLICATION_OCTET_STREAM).get(File.class);
		System.out.println(ret.getName());
		System.out.println(ret.getTotalSpace());
		
		return ret;
	}
	
	/**
	 * @param input
	 * @param withSelf
	 * @return String
	 */
	private List<String[]> filterXlinkReferences(String input, Boolean withSelf) {
		
		// System.out.println(input);
		List<String[]> result = new ArrayList<String[]>();
		// get the References
		String[] subStrings = input.split("Reference");
		
		for (String sub : subStrings) {
			// it is only a Reference if it contains the xmlns
			if (sub.contains("xlink:")) {
				// get the xlink elements
				String[] subsubStrings = sub.split("xlink:");
				
				Boolean hadTitle = false;
				Boolean hadHref = false;
				
				String[] element = new String[2];
				
				for (String subsub : subsubStrings) {
					// get the title
					if (subsub.startsWith("title=")) {
						element[0] = getBetweenQuotes(subsub);
						hadTitle = true;
						// do only return selflinks if requested
						if (element[0].equals("Self") && !withSelf) {
							hadTitle = false;
						}
					}
					// get the link
					if (subsub.startsWith("href=")) {
						element[1] = getBetweenQuotes(subsub);
						hadHref = true;
					}
				}
				// return element only if it had the fields title and href
				if (hadTitle && hadHref) {
					// System.out.println("Adding: ");
					// this.printStringArray(element);
					result.add(element);
				}
			}
		}
		
		return result;
	}
	
	/**
	 * Provides a WebResource to send requests to the ContainerBaseUri
	 * 
	 * @return WebResource
	 */
	private WebResource getBaseService() {
		
		return client.resource(ContainerClient.BASEURI);
	}
	
	/**
	 * Returns the String between to Quotes. Input: "anything", Output:anything
	 * 
	 * @param s
	 * @return String
	 */
	public String getBetweenQuotes(String s) {
		
		int first = s.indexOf("\"") + 1;
		int last = s.lastIndexOf("\"");
		return s.substring(first, last);
	}
	
	/**
	 * Provides a WebResource to send requests to anyUri
	 * 
	 * @param URI as String
	 * @return WebResource
	 */
	public WebResource getGenericService(String uri) {
		
		return client.resource(uri);
	}
	
	public List<String[]> getLinksFromUri(String uri, Boolean withSelf) {
		
		return filterXlinkReferences(getGenericService(uri).accept(MediaType.TEXT_XML).get(String.class), withSelf);
	}
	
	public List<String[]> getLinksWithExtension(String pathExtension, Boolean withSelf) {
		
		List<String[]> list = null;
		try {
			list = filterXlinkReferences(getBaseService().path(pathExtension).accept(MediaType.TEXT_XML).get(String.class), withSelf);
		} catch (ClientHandlerException e) {
			System.out.println("An Error occurred! Maybe the Container is not running or cannot be accessed!");
			System.out.println(e);
		}
		return list;
	}
	
	public List<String> getOperations(String csar) {
		
		String inputString = getBaseService().path("CSARControl").path(ContainerClient.URLencode(csar)).path("Operations").accept(MediaType.TEXT_PLAIN).get(String.class);
		String[] methods = inputString.split("&");
		List<String> response = new ArrayList<String>();
		for (String m : methods) {
			if (!m.isEmpty()) {
				response.add(m);
			}
		}
		
		return response;
	}
	
	/**
	 * Gets a specific plan invocation information from History.
	 * 
	 * TODO Refactor
	 * 
	 * @author Christian Endres - endrescn@studi.informatik.uni-stuttgart.de
	 * 
	 * @param csar CSARID String
	 * @param internalInstanceID the CSAR-Instance ID
	 * @param correlationID the CorrelationID which identifies the plan
	 *            invocation in history
	 * @return TPlanDTOs
	 */
	public TPlanDTO getPlanDTOFromHistory(String csar, String internalInstanceID, String correlationID) {
		
		WebResource src = getBaseService().path("CSARs").path(csar).path("Instances").path(internalInstanceID).path("history").path(correlationID);
		System.out.println(src.getURI());
		TPlanDTO ret = src.accept(MediaType.APPLICATION_XML).get(TPlanDTO.class);
		if (null == ret) {
			System.out.println("Did not find any invocation information!");
		} else {
			ret.getId();
		}
		return ret;
	}
	
	/**
	 * Gets a specific PlanDTO xml for a certain CSAR and a plan type and ID.
	 * 
	 * @author Christian Endres - endrescn@studi.informatik.uni-stuttgart.de
	 * 
	 * @param csarID the CSARID String
	 * @param planType the plan type String (BUILD, OTHERMANAGEMENT,
	 *            TERMINATION)
	 * @param internalID the ID which identifies the PlanDTO, i.e., path
	 *            http://localhost:1337/containerapi/CSARs/[CSARID]/
	 *            BoundaryDefinitions/Interfaces/[InterfaceName]/Operations/[
	 *            OperationName]/Plan/[PlanName]/
	 * @return PublicPlan
	 */
	public List<TPlan> getPlanDTOs(String csarID) {
		
		List<TPlan> plans = new ArrayList<TPlan>();
		
		WebResource src;
		for (String[] intfName : getLinksWithExtension("CSARs/" + csarID + "/BoundaryDefinitions/Interfaces", false)) {
			
			for (String[] opName : getLinksWithExtension("CSARs/" + csarID + "/BoundaryDefinitions/Interfaces/" + intfName[0] + "/Operations", false)) {
				
				for (String[] planName : getLinksWithExtension("CSARs/" + csarID + "/BoundaryDefinitions/Interfaces/" + intfName[0] + "/Operations/" + opName[0] + "/Plan/", false)) {
					
					String plan = null;
					for (int itr = 0; itr < planName.length; itr++) {
						if (!planName[itr].equalsIgnoreCase("self") && !planName[itr].equalsIgnoreCase("PlanWithMinimalInput") && !planName[itr].startsWith("http")) {
							// System.out.println(planName[itr]);
							plan = planName[itr];
							System.out.println("Take plan " + plan);
							
							src = getBaseService().path("CSARs").path(csarID).path("BoundaryDefinitions").path("Interfaces").path(intfName[0]).path("Operations").path(opName[0]).path("Plan").path(plan);
							
							System.out.println("Try to get plan from " + src.getURI());
							
							plans.add(src.accept(MediaType.APPLICATION_XML).get(TPlan.class));
						}
					}
				}
			}
		}
		if (plans.size() < 1) {
			System.out.println("Did not find a plan!");
		} else {
			System.out.println("Found Plans: ");
			for (TPlan dto : plans) {
				System.out.println("   " + dto.getId() + " of type " + dto.getPlanType());
			}
		}
		return plans;
	}
	
	public List<String> getPlanDTOsAsXML(String csarID) {
		
		List<String> plans = new ArrayList<String>();
		
		WebResource src;
		for (String[] intfName : getLinksWithExtension("CSARs/" + csarID + "/BoundaryDefinitions/Interfaces", false)) {
			
			for (String[] opName : getLinksWithExtension("CSARs/" + csarID + "/BoundaryDefinitions/Interfaces/" + intfName[0] + "/Operations", false)) {
				
				for (String[] planName : getLinksWithExtension("CSARs/" + csarID + "/BoundaryDefinitions/Interfaces/" + intfName[0] + "/Operations/" + opName[0] + "/Plan/", false)) {
					
					String plan = null;
					for (int itr = 0; itr < planName.length; itr++) {
						if (!planName[itr].equalsIgnoreCase("self") && !planName[itr].equalsIgnoreCase("PlanWithMinimalInput") && !planName[itr].startsWith("http")) {
							// System.out.println(planName[itr]);
							plan = planName[itr];
							System.out.println("Take plan " + plan);
							
							src = getBaseService().path("CSARs").path(csarID).path("BoundaryDefinitions").path("Interfaces").path(intfName[0]).path("Operations").path(opName[0]).path("Plan").path(plan);
							
							System.out.println("Try to get plan from " + src.getURI());
							
							plans.add("\"" + plan + "\":$.parseXML(" + src.accept(MediaType.APPLICATION_XML).get(String.class).replace("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>", "") + ");");
						}
					}
				}
			}
		}
		if (plans.size() < 1) {
			System.out.println("Did not find a plan!");
		} else {
			System.out.println("Found Plans: ");
			for (String dto : plans) {
				System.out.println("   " + dto);
			}
		}
		return plans;
	}
	
	public List<String> getMinimalPlanAsXML(String csarID) {
		
		List<String> plans = new ArrayList<String>();
		
		WebResource src;
		for (String[] intfName : getLinksWithExtension("CSARs/" + csarID + "/BoundaryDefinitions/Interfaces", false)) {
			
			for (String[] opName : getLinksWithExtension("CSARs/" + csarID + "/BoundaryDefinitions/Interfaces/" + intfName[0] + "/Operations", false)) {
				
				for (String[] planName : getLinksWithExtension("CSARs/" + csarID + "/BoundaryDefinitions/Interfaces/" + intfName[0] + "/Operations/" + opName[0] + "/Plan/", false)) {
					
					String plan = null;
					for (int itr = 0; itr < planName.length; itr++) {
						if (!planName[itr].equalsIgnoreCase("self") && !planName[itr].equalsIgnoreCase("PlanWithMinimalInput") && !planName[itr].startsWith("http")) {
							// System.out.println(planName[itr]);
							plan = planName[itr];
							System.out.println("Take plan " + plan);
							
							src = getBaseService().path("CSARs").path(csarID).path("BoundaryDefinitions").path("Interfaces").path(intfName[0]).path("Operations").path(opName[0]).path("Plan").path(plan).path("PlanWithMinimalInput");
							
							System.out.println("Try to get plan from " + src.getURI());
							
							plans.add(// "\"" + plan + "\":" +
									src.accept(MediaType.APPLICATION_XML).get(String.class).replace("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>", "")
									// + ";"
									);
						}
					}
				}
			}
		}
		if (plans.size() < 1) {
			System.out.println("Did not find a plan!");
		} else {
			System.out.println("Found Plans: ");
			for (String dto : plans) {
				System.out.println("   " + dto);
			}
		}
		return plans;
	}
	
	public List<TPlan> getMinimalPlanDTO(String csarID) {
		
		List<TPlan> plans = new ArrayList<TPlan>();
		
		WebResource src;
		for (String[] intfName : getLinksWithExtension("CSARs/" + csarID + "/BoundaryDefinitions/Interfaces", false)) {
			
			for (String[] opName : getLinksWithExtension("CSARs/" + csarID + "/BoundaryDefinitions/Interfaces/" + intfName[0] + "/Operations", false)) {
				
				for (String[] planName : getLinksWithExtension("CSARs/" + csarID + "/BoundaryDefinitions/Interfaces/" + intfName[0] + "/Operations/" + opName[0] + "/Plan/", false)) {
					
					String plan = null;
					for (int itr = 0; itr < planName.length; itr++) {
						if (!planName[itr].equalsIgnoreCase("self") && !planName[itr].equalsIgnoreCase("PlanWithMinimalInput") && !planName[itr].startsWith("http")) {
							// System.out.println(planName[itr]);
							plan = planName[itr];
							System.out.println("Take plan " + plan);
							
							src = getBaseService().path("CSARs").path(csarID).path("BoundaryDefinitions").path("Interfaces").path(intfName[0]).path("Operations").path(opName[0]).path("Plan").path(plan).path("PlanWithMinimalInput");
							
							System.out.println("Try to get plan from " + src.getURI());
							
							plans.add(src.accept(MediaType.APPLICATION_XML).get(TPlan.class));
						}
					}
				}
			}
		}
		if (plans.size() < 1) {
			System.out.println("Did not find a plan!");
		} else {
			System.out.println("Found Plans: ");
			for (TPlan dto : plans) {
				System.out.println("   " + dto.getId() + " of type " + dto.getPlanType());
			}
		}
		return plans;
	}
	
	public List<String> getPOSTURLsOfPlans(String csarID) {
		
		List<String> urls = new ArrayList<String>();
		
		WebResource src;
		for (String[] intfName : getLinksWithExtension("CSARs/" + csarID + "/BoundaryDefinitions/Interfaces", false)) {
			
			for (String[] opName : getLinksWithExtension("CSARs/" + csarID + "/BoundaryDefinitions/Interfaces/" + intfName[0] + "/Operations", false)) {
				
				for (String[] planName : getLinksWithExtension("CSARs/" + csarID + "/BoundaryDefinitions/Interfaces/" + intfName[0] + "/Operations/" + opName[0] + "/Plan/", false)) {
					
					String plan = null;
					for (int itr = 0; itr < planName.length; itr++) {
						if (!planName[itr].equalsIgnoreCase("self") && !planName[itr].equalsIgnoreCase("PlanWithMinimalInput") && !planName[itr].startsWith("http")) {
							// System.out.println(planName[itr]);
							plan = planName[itr];
							System.out.println("Take plan " + plan);
							
							src = getBaseService().path("CSARs").path(csarID).path("BoundaryDefinitions").path("Interfaces").path(intfName[0]).path("Operations").path(opName[0]).path("Plan").path(plan).path("PlanWithMinimalInput");
							
							System.out.println("Try to get plan from " + src.getURI());
							TPlan planObj = src.accept(MediaType.APPLICATION_XML).get(TPlan.class);
							
							String url = "\"" + planObj.getId() + "\":\"" + "http://localhost:1337/containerapi/CSARs/" + csarID + "/BoundaryDefinitions/Interfaces/" + intfName[0] + "/Operations/" + opName[0] + "/Plan/" + planObj.getId() + "\"";
							urls.add(url);
						}
					}
				}
			}
		}
		if (urls.size() < 1) {
			System.out.println("Did not find a plan!");
		} else {
			System.out.println("Found plan urls: ");
			for (String url : urls) {
				System.out.println("   url: " + url);
			}
		}
		return urls;
	}
	
	/**
	 * Gets a specific PublicPlan xml which is active at the moment.
	 * 
	 * TODO Refactor
	 * 
	 * @author Christian Endres - endrescn@studi.informatik.uni-stuttgart.de
	 * 
	 * @param csar CSARID String
	 * @param internalInstanceID the CSAR-Instance-ID
	 * @param correlationID the CorrelationID which identifies the active
	 *            PublicPlan
	 * @return PublicPlan
	 */
	public TPlanDTO getActivePlanDTOs(String csar, String internalInstanceID, String correlationID) {
		
		WebResource src = getBaseService().path("CSARs").path(csar).path("Instances").path(internalInstanceID).path("activePublicPlans").path(correlationID);
		System.out.println(src.getURI());
		TPlanDTO ret = src.accept(MediaType.APPLICATION_XML).get(TPlanDTO.class);
		if (null == ret) {
			System.out.println("Did not get a PublicPlan!");
		} else {
			ret.getId();
		}
		return ret;
	}
	
	public String[] getServiceTemplates(QName csarID) {
		
		String serviceTemplates = getBaseService().path("CSARControl").path(ContainerClient.URLencode(csarID.toString())).path("ServiceTemplates").accept(MediaType.TEXT_PLAIN).get(String.class);
		return serviceTemplates.split("&");
	}
	
	public String getState(String csar) {
		
		return getBaseService().path("CSARControl").path(ContainerClient.URLencode(csar)).path("DeploymentState").accept(MediaType.TEXT_PLAIN).get(String.class);
	}
	
	// public QName getThorQName(String thorURI) {
	//
	// QName thorID = null;
	// try {
	// String name =
	// getGenericService(thorURI).path("QName").accept(MediaType.TEXT_PLAIN).get(String.class);
	// thorID = QName.valueOf(name);
	// } catch (ClientHandlerException e) {
	// System.out.println("An Error occurred! Maybe the Container is not running
	// or cannot be accessed.");
	// System.out.println(e);
	// }
	// return thorID;
	// }
	
	public List<String> invokeMethod(QName thorID, String methodEnum) {
		
		System.out.println("Trying to invoke Method " + methodEnum + " on ThorFile/Process with ID " + thorID.toString());
		ArrayList<String> result = new ArrayList<String>();
		ClientResponse resp = null;
		try {
			resp = getBaseService().path("CSARControl").path(ContainerClient.URLencode(thorID.toString())).post(ClientResponse.class, methodEnum);
			if (!resp.getClientResponseStatus().equals(Status.OK)) {
				System.out.println("Error occurred while invoking Method " + methodEnum + ", Server returned: " + resp.getClientResponseStatus());
				result.add("Invocation Error, Server returned: " + resp.getClientResponseStatus());
			}
		} catch (ClientHandlerException e) {
			System.out.println("An Error occurred while invoking Method " + methodEnum + ". Maybe the Container is not running or cannot be accessed.");
			// System.out.println(e);
			e.printStackTrace();
			result.add("An Error occurred while invoking Method " + methodEnum + "! Maybe the Container is not running or cannot be accessed.");
		} catch (NullPointerException e) {
			result = null;
			System.out.println("An Error occurred while invoking Method " + methodEnum);
		}
		return result;
	}
	
	public List<String> invokeMethod(QName csarID, String serviceTemplate, String methodEnum) {
		
		System.out.println("Trying to invoke Method " + methodEnum + " on ThorFile/Process with ID " + csarID.toString());
		ArrayList<String> result = new ArrayList<String>();
		ClientResponse resp = null;
		try {
			resp = getBaseService().path("CSARControl").path(ContainerClient.URLencode(csarID.toString())).post(ClientResponse.class, methodEnum + "&" + serviceTemplate);
			if (!resp.getClientResponseStatus().equals(Status.OK)) {
				System.out.println("Error occurred while invoking Method " + methodEnum + ", Server returned: " + resp.getClientResponseStatus());
				result.add("Invocation Error, Server returned: " + resp.getClientResponseStatus());
			}
		} catch (ClientHandlerException e) {
			System.out.println("An Error occurred while invoking Method " + methodEnum + ". Maybe the Container is not running or cannot be accessed.");
			// System.out.println(e);
			e.printStackTrace();
			result.add("An Error occurred while invoking Method " + methodEnum + "! Maybe the Container is not running or cannot be accessed.");
		} catch (NullPointerException e) {
			result = null;
			System.out.println("An Error occurred while invoking Method " + methodEnum);
		}
		return result;
	}
	
	/**
	 * Invokes a POST for a PublicPlan and a CSAR-Instance-ID.
	 * 
	 * TODO Refactor
	 * 
	 * @author Christian Endres - endrescn@studi.informatik.uni-stuttgart.de
	 * 
	 * @param TPlanDTO the PublicPlan to invoke
	 * @param instanceID the CSAR-Instance-ID
	 * @return list of status strings
	 */
	public List<String> postNonBUILDPlanDTO(String csarID, TPlanDTO TPlanDTO, int instanceID) {
		
		// System.out.println("manage instance " + instanceID + " for CSAR " +
		// TPlanDTO.getCSARID());
		
		ArrayList<String> result = new ArrayList<String>();
		ClientResponse resp = null;
		
		WebResource src = getBaseService().path("CSARs").path(csarID).path("Instances").path(Integer.toString(instanceID));
		System.out.println("at URI " + src.getURI());
		resp = src.post(ClientResponse.class, TPlanDTO);
		
		System.out.println(resp.getClientResponseStatus());
		
		// resp = src.delete(ClientResponse.class);
		
		if (!resp.getClientResponseStatus().equals(Status.OK)) {
			System.out.println("Error occurred while invoking PublicPlan: " + resp.getClientResponseStatus());
			result.add("Invocation Error, Server returned: " + resp.getClientResponseStatus());
		} else {
			result.add("success");
		}
		return result;
		
	}
	
	/**
	 * POST for a BUILD PublicPlan invocation.
	 * 
	 * TODO Refactor
	 * 
	 * @author Christian Endres - endrescn@studi.informatik.uni-stuttgart.de
	 * 
	 * @param planDTO the BUILD PublicPlan to invoke.
	 * @return list of status Strings
	 */
	public ClientResponse postBUILDPlanDTO(String csarID, TPlanDTO planDTO) {
		
		System.out.println("post public plan " + planDTO.getId());
		
		ClientResponse resp = null;
		
		WebResource src = getBaseService().path("CSARs").path(csarID).path("BoundaryDefinitions").path("Interfaces");
		resp = src.get(ClientResponse.class);
		// System.out.println(resp.getEntity(String.class));
		
		try {
			
			List<String> intfs = new ArrayList<>();
			List<String> ops = new ArrayList<>();
			List<String> plans = new ArrayList<>();
			
			intfs = getRefsWithoutSelf(resp);
			
			for (String intf : intfs) {
				System.out.println("Try interface " + intf);
				
				src = getBaseService().path("CSARs").path(csarID).path("BoundaryDefinitions").path("Interfaces").path(intf).path("Operations");
				resp = src.get(ClientResponse.class);
				ops = getRefsWithoutSelf(resp);
				
				for (String op : ops) {
					System.out.println("Try operation " + op);
					
					src = getBaseService().path("CSARs").path(csarID).path("BoundaryDefinitions").path("Interfaces").path(intf).path("Operations").path(op).path("Plan");
					resp = src.get(ClientResponse.class);
					plans = getRefsWithoutSelf(resp);
					
					for (String planName : plans) {
						System.out.println("Try plan " + planName);
						
						System.out.println(planName + " =? " + planDTO.getId());
						if (planName.trim().equals(planDTO.getId().toString().trim())) {
							
							System.out.println("Post of a PublicPlan on the URI " + src.getURI());
							resp = getBaseService().path("CSARs").path(csarID).path("BoundaryDefinitions").path("Interfaces").path(intf).path("Operations").path(op).path("Plan").path(planName).post(ClientResponse.class, planDTO);
							
							// System.out.println("Plan invocation returned
							// status " + resp.getStatus() + " and message " +
							// resp.getEntity(String.class));
						} else {
							System.out.println("   didnt match");
						}
					}
				}
			}
			
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		
		return resp;
		
	}
	
	public String postXMLPlanToURL(String url, String plan) {
		
		System.out.println("post public plan " + plan + " to URL " + url);
		
		ClientResponse resp = null;
		
		resp = client.resource(url).type(MediaType.APPLICATION_XML).post(ClientResponse.class, plan);
		
		try {
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			Document doc;
			XPathFactory xPathfactory = XPathFactory.newInstance();
			XPath xpath = xPathfactory.newXPath();
			XPathExpression expr;
			
			DocumentBuilder builder = factory.newDocumentBuilder();
			doc = builder.parse(resp.getEntityInputStream());
			expr = xpath.compile("//Reference/@*[local-name()='href']"); // title
			NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
			
			for (int itr = 0; itr < nodes.getLength(); itr++) {
				String str = nodes.item(itr).getTextContent();
				if (!str.equalsIgnoreCase("self")) {
					System.out.println(str);
					return str;
				}
			}
			
		} catch (XPathExpressionException | ParserConfigurationException
				| SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "";
	}
	
	public String postTerminationPlan(String csarID, String serviceID) {
		List<String> xmls = getMinimalPlanAsXML(csarID);
		
		System.out.println();
		System.out.println();
		System.out.println();
		for (TPlan plan : getPlanDTOs(csarID)) {
			if (plan.getPlanType().contains("http://docs.oasis-open.org/tosca/ns/2011/12/PlanTypes/TerminationPlan")) {
				System.out.println("taking " + plan.getId() + " for termination");
				TPlanDTO dto = new TPlanDTO(plan, "");
				for (TParameterDTO para : dto.getInputParameters().getInputParameter()) {
					// System.out.println("para: " + para.getName());
					if (para.getName().equals("selfserviceServiceInstance")) {
						para.setValue(serviceID);
						
						System.out.println("selfserviceServiceInstance set, now search for URLS");
						for (String str : getPOSTURLsOfPlans(csarID)) {
							System.out.println(str);
							if (str.startsWith("\"" + plan.getId() + "\"")) {
								String url = str.substring(plan.getId().length() + 4, str.length() - 1);
								System.out.println("post termination to URL: " + url);
								
								ClientResponse resp = client.resource(url).type(MediaType.APPLICATION_XML).post(ClientResponse.class, dto);
								return resp.getEntity(String.class);
							}
						}
					}
				}
			}
		}
		// getPOSTURLsOfPlans(csarID)
		System.out.println();
		
		// for (String xml : xmls) {
		// System.out.println("trying:\n" + xml);
		// if
		// (xml.contains("\"http://docs.oasis-open.org/tosca/ns/2011/12/PlanTypes/TerminationPlan\""))
		// {
		// DocumentBuilder db;
		// try {
		//
		// System.out.println("taking termination plan:\n" + xml);
		//
		// db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		// InputSource is = new InputSource();
		// is.setCharacterStream(new StringReader(xml));
		//
		// Document doc = db.parse(is);
		// // NodeList nodes = doc.getElementsByTagNameNS("*",
		// // "InputParameter");
		//
		// NodeList planChilds = doc.getFirstChild().getChildNodes();
		//
		// for (int itr = 0; itr < planChilds.getLength(); itr++) {
		// System.out.println("planChilds " +
		// planChilds.item(itr).getNodeName());
		// if
		// (planChilds.item(itr).getNodeName().equalsIgnoreCase("InputParameters"))
		// {
		//
		// NodeList inputParams = planChilds.item(itr).getChildNodes();
		// for (int itr2 = 0; itr < inputParams.getLength(); itr++) {
		//
		// System.out.println(itr2 + ": Found node " +
		// inputParams.item(itr2).getAttributes().getNamedItem("name").getTextContent());//
		// getAttributes().getNamedItem("name").getTextContent());
		// // nodes.item(itr).setTextContent(serviceID);
		// // postXMLPlanToURL(url, plan);
		// }
		//
		// }
		// }
		//
		// // for (String url : getPOSTURLsOfPlans(csarID)) {
		// // JsonParser parser = new JsonParser();
		// // JsonObject json = parser.parse("{" + url +
		// // "}").getAsJsonObject();
		// //
		// // for (Map.Entry<String, JsonElement> entry :
		// // json.entrySet()) {
		// // if (entry.getKey().equals(terminPlan.getId())) {
		// // return
		// // postXMLPlanToURL(entry.getValue().getAsString(),
		// // plan);
		// // break;
		// // } else {
		// // }
		// // }
		// //
		// // }
		//
		// } catch (ParserConfigurationException e) {
		// e.printStackTrace();
		// } catch (SAXException e) {
		// e.printStackTrace();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// }
		// }
		
		return null;
	}
	
	private List<String> getRefsWithoutSelf(ClientResponse resp) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
		
		List<String> list = new ArrayList<>();
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Document doc;
		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();
		XPathExpression expr;
		
		DocumentBuilder builder = factory.newDocumentBuilder();
		doc = builder.parse(resp.getEntityInputStream());
		expr = xpath.compile("//Reference/@*[local-name()='title']");
		NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
		
		for (int itr = 0; itr < nodes.getLength(); itr++) {
			String str = nodes.item(itr).getTextContent();
			if (!str.equalsIgnoreCase("self")) {
				list.add(str);
			}
		}
		
		return list;
	}
	
	/**
	 * DebugMethod to print a Array of Strings
	 * 
	 * @param subStrings
	 */
	public void printStringArray(String[] subStrings) {
		
		for (String sub : subStrings) {
			System.out.println(sub);
		}
	}
	
	public List<String> uploadCSAR(String absoluteFilePath) {
		
		List<String> result = new ArrayList<String>();
		
		System.out.println("Trying to upload ThorFile from: " + absoluteFilePath);
		
		File file = new File(absoluteFilePath);
		
		if (!file.exists()) {
			System.out.println("Error: file does not exist.");
			result.add("Error: file does not exist.");
			return result;
		}
		
		System.out.println("Size of the file to upload: " + file.getTotalSpace());
		
		ClientResponse resp = null;
		FormDataMultiPart multiPart = new FormDataMultiPart();
		
		FormDataContentDisposition.FormDataContentDispositionBuilder dispositionBuilder = FormDataContentDisposition.name("file");
		dispositionBuilder.fileName(file.getName());
		dispositionBuilder.size(file.getTotalSpace());
		
		FormDataContentDisposition formDataContentDisposition = dispositionBuilder.build();
		
		multiPart.bodyPart(new FormDataBodyPart("file", file, MediaType.APPLICATION_OCTET_STREAM_TYPE).contentDisposition(formDataContentDisposition));
		
		resp = getBaseService().path("CSARs").type(MediaType.MULTIPART_FORM_DATA_TYPE).post(ClientResponse.class, multiPart);
		
		result.add(resp.getClientResponseStatus().toString());
		
		return result;
	}
	
	public List<String> uploadCSARDueURL(String urlToUpload) {
		
		System.out.println("Try to send the URL to the ContainerAPI: " + ContainerClient.URLencode(urlToUpload));
		
		ArrayList<String> result = new ArrayList<String>();
		
		ClientResponse resp = getBaseService().path("CSARs").queryParam("url", urlToUpload).post(ClientResponse.class);
		
		if (!resp.getClientResponseStatus().equals(Status.OK)) {
			System.out.println("Error occurred while uploading CSAR from URL " + urlToUpload + ", Server returned: " + resp.getClientResponseStatus());
			result.add("Invocation Error, Server returned: " + resp.getClientResponseStatus());
		} else {
			result.add("Created");
		}
		return result;
	}
}
