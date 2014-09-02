package org.opentosca.ui.vinothek;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.opentosca.ui.vinothek.model.ApplicationInstance;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

@WebServlet("/CallbackEndpoint")
public class CallbackEndpointServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static final String NO_APPLICATION_URL = "PLAN_DID_NOT_RETURN_APPLICATION_URL";
	public static final String NO_SELFSERVICE_MESSAGE = "PLAN_DID_NOT_RETURN_SELFSERVICE_MESSAGE";
	public static final String NO_SELFSERVICE_POLICY_MESSAGE = "PLAN_DID_NOT_RETURN_SELFSERVICE_POLICY_MESSAGE";
	public static final String NO_SELFSERVICE_STATUS = "PLAN_DID_NOT_RETURN_SELFSERVICE_STATUS";

	public CallbackEndpointServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		processMessage(request);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		processMessage(request);
	}

	private void processMessage(HttpServletRequest request) {
		System.out.println("CALLBACK MESSAGE FROM BUILD PLAN RECEIVED");
		ApplicationInstance instance = CallbackManager.getInstance(request
				.getParameter("callbackId"));
		String applicationUrl = NO_APPLICATION_URL;
		String selfserviceMessage = NO_SELFSERVICE_MESSAGE;
		String selfservicePolicyMessage = NO_SELFSERVICE_POLICY_MESSAGE;
		String selfserviceStatus = NO_SELFSERVICE_STATUS;

		Document document = null;
		try {
			// Create Document from Message
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			document = builder.parse(request.getInputStream());

			// Convert Message to String
			Transformer transformer = TransformerFactory.newInstance()
					.newTransformer();
			StreamResult transformerResult = new StreamResult(
					new StringWriter());
			transformer.transform(new DOMSource(document), transformerResult);
			String msg = transformerResult.getWriter().toString();
			instance.setMessage(msg);
			System.out.println(" ^ Received Message:\n" + msg);
		} catch (ParserConfigurationException | SAXException | IOException
				| TransformerFactoryConfigurationError | TransformerException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// Extract params from message
		javax.xml.xpath.XPath xpath;
		XPathExpression expr;
		Node result;

		try {
			// Extract applicationUrl
			xpath = XPathFactory.newInstance().newXPath();
			expr = xpath
					.compile("//*[local-name() = '"
							+ WellknownPlanInputMessageParams.OUT_SELFSERVICE_APPLICATIONURL
							+ "']");
			result = (Node) expr.evaluate(document, XPathConstants.NODE);
			applicationUrl = result.getTextContent();
		} catch (Exception e) {
			System.out
					.println(" ^ ERROR parsing plan result message; not all expected WellknownPlan*MessageParams could be extracted");
		}

		try {
			// Extract selfserviceMessage
			xpath = XPathFactory.newInstance().newXPath();
			expr = xpath.compile("//*[local-name() = '"
					+ WellknownPlanInputMessageParams.OUT_SELFSERVICE_MESSAGE
					+ "']");
			result = (Node) expr.evaluate(document, XPathConstants.NODE);
			selfserviceMessage = result.getTextContent();
		} catch (Exception e) {
			System.out
			.println(" ^ ERROR parsing plan result message; not all expected WellknownPlan*MessageParams could be extracted");
		}
		
		try {
			// Extract selfserviceMessage
			xpath = XPathFactory.newInstance().newXPath();
			expr = xpath
					.compile("//*[local-name() = '"
							+ WellknownPlanInputMessageParams.OUT_SELFSERVICE_POLICY_MESSAGE
							+ "']");
			result = (Node) expr.evaluate(document, XPathConstants.NODE);
			selfservicePolicyMessage = result.getTextContent();
		} catch (Exception e) {
			System.out
			.println(" ^ ERROR parsing plan result message; not all expected WellknownPlan*MessageParams could be extracted");
		}

		try {
			// Extract selfserviceMessage
			xpath = XPathFactory.newInstance().newXPath();
			expr = xpath
					.compile("//*[local-name() = '"
							+ WellknownPlanInputMessageParams.OUT_SELFSERVICE_STATUS
							+ "']");
			result = (Node) expr.evaluate(document, XPathConstants.NODE);
			selfserviceStatus = result.getTextContent();
		} catch (Exception e) {
			System.out
			.println(" ^ ERROR parsing plan result message; not all expected WellknownPlan*MessageParams could be extracted");
		}

		System.out.println(" ^ Extracted applicationUrl " + applicationUrl);
		instance.setEndpointUrl(applicationUrl);

		System.out.println(" ^ Extracted selfserviceMessage "
				+ selfserviceMessage);
		instance.setSelfserviceMessage(selfserviceMessage);
		
		System.out.println(" ^ Extracted selfservicePolicyMessage "
				+ selfservicePolicyMessage);
		instance.setSelfservicePolicyMessage(selfservicePolicyMessage);

		System.out.println(" ^ Extracted selfserviceStatus "
				+ selfserviceStatus);
		instance.setSelfserviceStatus(selfserviceStatus);
	}

}
