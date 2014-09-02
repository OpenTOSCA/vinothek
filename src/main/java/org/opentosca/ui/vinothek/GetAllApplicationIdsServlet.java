package org.opentosca.ui.vinothek;

import java.io.IOException;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/GetAllApplicationIds")
public class GetAllApplicationIdsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public GetAllApplicationIdsServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("application/json");
		
		StringBuilder builder = new StringBuilder();
		
		VinothekContainerClient client = new VinothekContainerClient(request);
		Set<String> apps = client.getApplications().keySet();
		
		builder.append("{\"appIds\": [");
		
		// HACK: This should be a JSON Object and serializer...
		int counter = 0;
		for (String id : apps) {
			counter++;
			builder.append("{\"id\":\"");
			builder.append(id);
			builder.append("\"}");
			
			if (counter < apps.size()) {
				builder.append(",");	
			}
		}
		
		builder.append("]}");
				
		// Returning URL to poll for result
		response.getWriter().append(builder.toString());
	}


}
