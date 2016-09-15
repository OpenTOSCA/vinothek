package org.opentosca.ui.vinothek;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.opentosca.ui.admin.action.client.ContainerClient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;

@WebServlet("/ApplicationInstantiation")
public class ApplicationInstantiationServlet extends HttpServlet {
	
	
	private static final long serialVersionUID = 1L;
	
	
	public ApplicationInstantiationServlet() {
		super();
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String url = request.getParameter("url");
		ClientResponse resp = null;
		Client client = Client.create();
		resp = client.resource(url).get(ClientResponse.class);
		String str = resp.getEntity(String.class);
		
		if (str.equals("{\"result\":{\"status\":\"PENDING\"}}")) {
			str = "{\"result\":{\"status\":\"PENDING\"}}";
		} else {
			str = "{\"result\":{\"status\":\"FINISHED\", \"payload\":" + str + "}}";
		}
		System.out.println("polling result: " + str);
		
		response.setContentType("application/json");
		response.getWriter().append(str);
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String planID = request.getParameter("planID");
		String plan = request.getParameter("xml");
		String csarID = request.getParameter("csarID");
		String pollURL = null;
		
		ContainerClient client = ContainerClient.getInstance();
		for (String url : client.getPOSTURLsOfPlans(csarID)) {
			JsonParser parser = new JsonParser();
			JsonObject json = parser.parse("{" + url + "}").getAsJsonObject();
			
			for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
				if (entry.getKey().equals(planID)) {
					pollURL = client.postXMLPlanToURL(entry.getValue().getAsString(), plan);
					break;
				} else {
				}
			}
			
		}
		
		response.getWriter().append(pollURL);
		// response.getWriter().append("{pollURL:" + pollURL + "}");
		System.out.println(pollURL);
	}
	
}
