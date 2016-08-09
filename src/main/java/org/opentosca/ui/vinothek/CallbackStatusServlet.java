package org.opentosca.ui.vinothek;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.opentosca.ui.vinothek.model.ApplicationInstance;

@WebServlet("/CallbackStatus")
public class CallbackStatusServlet extends HttpServlet {
	
	public static final String NO_CALLBACK_RECEIVED_YET = "NO-CALLBACK-RECEIVED-YET";
	
	private static final long serialVersionUID = 1L;

	public CallbackStatusServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("text/plain");

		// Get and check callback id
		String callbackId = request.getParameter("callbackId");
		if (callbackId == null || callbackId.length() < 3) {
			response.getWriter().append("Unkown or no callbackId");
			return;
		}

		// Check application instance
		ApplicationInstance instance = CallbackManager.getInstance(callbackId);
		if (instance == null) {
			response.getWriter().append("Unkown or no callbackId");
			return;
		}

		// return URL as result
		response.setContentType("application/json");
		if(instance.getSelfserviceStatus() == null) {
			response.getWriter().append("{\"" + NO_CALLBACK_RECEIVED_YET + "\":true}");
		} else {
			// TODO: use a json object builder
			response.getWriter().append("{");
			response.getWriter().append("\"applicationUrl\":\"");
			response.getWriter().append(instance.getEndpointUrl());
			response.getWriter().append("\", ");
			response.getWriter().append("\"selfserviceMessage\":\"");
			response.getWriter().append(instance.getSelfserviceMessage());
			response.getWriter().append("\",");
			response.getWriter().append("\"selfservicePolicyMessage\":\"");
			response.getWriter().append(instance.getSelfservicePolicyMessage());
			response.getWriter().append("\",");
			response.getWriter().append("\"selfserviceServiceInstance\":\"");
			response.getWriter().append(instance.getSelfserviceServiceInstance());
			response.getWriter().append("\",");			
			response.getWriter().append("\"selfserviceStatus\":\"");
			response.getWriter().append(instance.getSelfserviceStatus());
			response.getWriter().append("\"}");
		}
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		System.out.println("No post implemented");
		response.getWriter().append("No post implemented");
	}

}
