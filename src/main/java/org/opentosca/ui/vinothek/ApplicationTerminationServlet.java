package org.opentosca.ui.vinothek;

import java.io.IOException;
import java.io.StringReader;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.opentosca.ui.admin.action.client.ContainerClient;
import org.opentosca.ui.vinothek.integration.TOSCARuntimeConnector;
import org.opentosca.ui.vinothek.integration.TOSCARuntimeConnectorFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author Kálmán Képes - kepeskn@iaas.uni-stuttgart.de
 *
 */
@WebServlet("/ApplicationTermination")
public class ApplicationTerminationServlet extends HttpServlet {
	
	
	private static final long serialVersionUID = 1L;
	
	
	public ApplicationTerminationServlet() {
		super();
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("Terminating Instance");
		
		String applicationId = request.getParameter("applicationId");
		String containerHost = request.getParameter("container");
		String selfserviceServiceInstance = request.getParameter("serviceInstance");
		String self = request.getRequestURL().toString().replace("/ApplicationTermination", "");
		TOSCARuntimeConnector connector = TOSCARuntimeConnectorFactory.getInstance().getConnector();
		String callbackId = connector.terminateServiceTemplate(self, containerHost, applicationId, selfserviceServiceInstance);
		
		response.setContentType("text/plain");
		
		response.getWriter().append(self + "/CallbackStatus?callbackId=" + callbackId);
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String csarID = request.getParameter("csarID");
		String serviceID = request.getParameter("serviceID");
		
		String pollURL = ContainerClient.getInstance().postTerminationPlan(csarID, serviceID);
		
		try {
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			Document doc;
			XPathFactory xPathfactory = XPathFactory.newInstance();
			XPath xpath = xPathfactory.newXPath();
			XPathExpression expr;
			
			DocumentBuilder builder = factory.newDocumentBuilder();
			doc = builder.parse(new InputSource(new StringReader(pollURL)));
			expr = xpath.compile("//Reference/@*[local-name()='href']"); // title
			NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
			
			for (int itr = 0; itr < nodes.getLength(); itr++) {
				String str = nodes.item(itr).getTextContent();
				if (!str.equalsIgnoreCase("self")) {
					System.out.println("string for xpath: " + str);
					pollURL = str;
					break;
				}
			}
			
		} catch (XPathExpressionException | ParserConfigurationException
				| SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		response.getWriter().append(pollURL);
		// response.getWriter().append("{pollURL:" + pollURL + "}");
		System.out.println("poll url for termination: " + pollURL);
	}
	
}
