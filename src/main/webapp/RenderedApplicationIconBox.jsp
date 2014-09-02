<%@page import="org.eclipse.winery.model.selfservice.ApplicationOption"%>
<%@page import="org.opentosca.ui.vinothek.model.ApplicationWrapper"%>
<%@page import="org.opentosca.ui.vinothek.VinothekContainerClient"%>


<%
	VinothekContainerClient client = new VinothekContainerClient(request);
	String applicationId = request.getParameter("applicationId");	
	ApplicationWrapper app = client.getApplication(applicationId);
%>

	<a href="ApplicationElement.jsp?applicationId=<%=applicationId%>&container=<%=client.getContainerHost()%>">
		<div id="<%=applicationId%>" class="appIconContainer">
			<img src="<%=app.convertToAbsoluteUrl(app.getIconUrl())%>">
			<div class="appIconContainerForeground"></div>							
		</div>
	</a>

