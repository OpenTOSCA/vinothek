<%@page import="org.opentosca.ui.vinothek.CannotConnectToContainerException"%>
<%@page import="org.opentosca.ui.vinothek.VinothekContainerClient"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">

		<style>
			body {
				font-family: Calibri;
			}
			
			a {
				color: #3f3f3f;
				font-family: Calibri;
				text-decoration: none;
			}
		</style>
	
		<link rel="stylesheet" href="design.css" type="text/css" />
		<script src="jquery-1.8.2.min.js"></script>
		<script src="jquery.easing.1.3.js"></script>	
		
	<title>OpenTOSCA - Vinothek</title>
	</head>
	
	<body>
	
	<%
	try {
	%>
		<jsp:include page="ApplicationList.jsp" />
	<%
	} catch (Exception e) {
		if(e.getCause() != null && e.getCause() instanceof CannotConnectToContainerException) {
			CannotConnectToContainerException ee = (CannotConnectToContainerException)e.getCause();
			%>
			<br><br><center><b><font color=red><h1>Vinothek failed to connect to the OpenTOSCA Container</h1></font></b></center>
			<br><center><b><font color=red><h3>
				Vinothek failed to connect to '<%=ee.getContainerURL() %>'! Please make sure OpenTOSCA<br>
				runs properly on this machine and is accessible. In particular, check if the listed port is<br>
				accessible from the outside, i.e., from the machine Vinothek is running on and from the<br>
				user's machine. If OpenTOSCA runs (in its default configuration) on a virtual machine, you<br>
				need to configure the firewall so at least ports 22, 1337, 8080, 9443 and 9763 are open.
			</h3></font></b></center>
			<br><center><b><font color=red>
				Details:
				<% if(e.getCause() != null) { %>
					<%=e.getCause().getMessage() %>
				<% } else { %>
					<%=e.getMessage() %>
				<% } %>
			</font></b></center>
			<%
		} else {
			%>
			<br><br><center><b><font color=red><h1>Vinothek has thrown an exception</h1></font></b></center>
			<br><center><b><font color=red><h3>
				Details:
				<% if(e.getCause() != null) { %>
					<%=e.getCause().getMessage() %>
					<% e.getCause().printStackTrace(); %>
				<% } %>
				<br>
				<%=e.getMessage() %>
				<% e.printStackTrace(); %>
			</h3></font></b></center>
			<%
		}
	}
	%>
		
	</body>
</html>