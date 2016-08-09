package org.opentosca.ui.vinothek;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.opentosca.ui.vinothek.integration.TOSCARuntimeConnector;
import org.opentosca.ui.vinothek.integration.TOSCARuntimeConnectorFactory;

/**
 * @author Kálmán Képes - kepeskn@iaas.uni-stuttgart.de
 *
 */
@WebServlet("/ApplicationTermination")
public class ApplicationTerminationServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	public ApplicationTerminationServlet(){
		super();
	}

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

}
