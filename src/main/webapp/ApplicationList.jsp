<%@page import="org.opentosca.ui.vinothek.model.ApplicationWrapper"%>
<%@page import="org.opentosca.ui.vinothek.VinothekContainerClient"%>
<%@page import="java.util.Map"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

		<div class="mainContainer">
		
			<a style="position:absolute; top: 588px; left: 858px;" href="javascript:reloadApps()">
				<img height="35" src="images/refreshbutton.png" />
			</a>
		
			<div class="appIconRowContainer" id="row1">

				<%
					VinothekContainerClient client = new VinothekContainerClient(request);
											Map<String,ApplicationWrapper> apps = client.getApplications();
											
											if (apps == null || apps.size() == 0) {

											} else {
												for (String key : apps.keySet()) {
				%>							
							<jsp:include page="RenderedApplicationIconBox.jsp">
								<jsp:param name="applicationId" value="<%=key%>" />
							</jsp:include>
							<%
						}
					}
				%>
																		
			</div>
		</div>
				
		<script>
		
			function animateNewApps() {
		
				$("div.newAppHidden").animate(
					{
						width: '156'
					}, 
					{ 
						duration: 1000,
						easing: 'jswing',
						complete: function() {
							$("div.newAppHidden > a").fadeIn("slow");
							$(this).removeClass("newAppHidden");
							$(this).addClass("newAppShown");
						}
					}		
				);			
				
			}
				
			function reloadApps() {
				$.get("GetAllApplicationIds?container=<%=client.getContainerHost()%>", function(data) {
					
					for (iter in data.appIds) {
						appId = data.appIds[iter].id;
					
						var contained = false;
						
						$("div.appIconContainer").each (function() {
							if ($(this).attr("id") == appId) {
								contained = true;
							}
						});
						
						if (!contained) {
							
							$.get("RenderedApplicationIconBox.jsp?container=<%=client.getContainerHost()%>&applicationId=" + appId, function(newAppHiddenRendered) {
								$("div.appIconRowContainer#row1").prepend ("<div class='newAppHidden'>" + newAppHiddenRendered + "</div>");
								animateNewApps();						
							});
								
						}				
					}					
				});	
			}
			
		</script>
