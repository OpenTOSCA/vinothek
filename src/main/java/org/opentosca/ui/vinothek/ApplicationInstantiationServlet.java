package org.opentosca.ui.vinothek;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.opentosca.ui.vinothek.integration.TOSCARuntimeConnector;
import org.opentosca.ui.vinothek.integration.TOSCARuntimeConnectorFactory;

@WebServlet("/ApplicationInstantiation")
public class ApplicationInstantiationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public ApplicationInstantiationServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		TOSCARuntimeConnector connector = TOSCARuntimeConnectorFactory.getInstance().getConnector();

		String self = request.getRequestURL().toString().replace("/ApplicationInstantiation", "");

		String callbackId = connector.instantiateServiceTemplate(self, request.getParameter("container"),
				request.getParameter("applicationId"), request.getParameter("optionId"), request.getParameter("xml"));

		response.setContentType("text/plain");

		response.getWriter().append(self + "/CallbackStatus?callbackId=" + callbackId);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.getWriter().append("No get implemented");
		System.out.println("No get implemented");
	}

}
