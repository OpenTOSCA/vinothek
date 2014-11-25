<%@page import="org.apache.commons.lang3.StringEscapeUtils"%>
<%@page import="org.opentosca.ui.vinothek.CallbackEndpointServlet"%>
<%@page import="org.eclipse.winery.model.selfservice.ApplicationOption"%>
<%@page import="org.opentosca.ui.vinothek.model.ApplicationWrapper"%>
<%@page import="org.opentosca.ui.vinothek.VinothekContainerClient"%>
<%@page import="org.opentosca.ui.vinothek.CallbackStatusServlet"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%
	try {
		VinothekContainerClient client = new VinothekContainerClient(request);
		
		String applicationId = request.getParameter("applicationId");
		ApplicationWrapper app = client.getApplication(applicationId);
		
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>OpenTOSCA - Vinothek</title>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" href="design.css" type="text/css" />
<script src="jquery-1.8.2.min.js"></script>
<link type="text/css" href="jquery-ui.css" rel="stylesheet">
<script type="text/javascript" src="jquery-ui.min.js"></script>
<link type="text/css" rel="stylesheet" href="lib/codemirror/codemirror.css">
<script type="text/javascript" src="lib/codemirror/codemirror.js"></script>
<script src="lib/codemirror/xml/xml.js"></script>

<script>
	// var of codemirror editor
	var editor = null;

	$(function(){
		// init option dialog and option list
		$("#OptionList").selectable({filter: "tr",selected : setOptionButton });
		$("#OptionDialog").dialog({autoOpen: false,modal: true,resizable: true, width: "auto", height:600, maxWidth: 600, maxHeight: 600});
		$("#EditInputDialog").dialog({autoOpen: false,modal: true,resizable: true, width: 600, height:"auto", buttons:{"OK": function(){closeEditPlanInputDialog();}}});		
		
		// init codemirror
		var editorArea = $("#EditInputDialogContent")[0];		
		editor = CodeMirror.fromTextArea(editorArea, {			 
			  mode:  "xml"			  
			});		
		
	});
	
	var checkCallbackStatusUrl = "";
	var planResult = {};
	
	function startInstance() {
		
		if(!inputMessageHasMissingFields()){
		
			var applicationId = "<%=applicationId%>";
			var containerHost = "<%=client.getContainerHost()%>";
			//var optionId = $("select#optionComboBox :selected").attr("id");
			var optionId = $("#SelectOptionButton")[0].getAttribute("optionId");
			var optionInputMessageElement = $("#OptionListInput[optionid=" + optionId + "]")[0];
			var requestBody = optionInputMessageElement.textContent;
		
			var instantiateTriggeringUrl = "ApplicationInstantiation?container=" + containerHost + "&applicationId=" + applicationId + "&optionId=" + optionId;
			
			$.ajax({
				url: instantiateTriggeringUrl,
				data: {xml: requestBody},
				success: function(data) {
					checkCallbackStatusUrl = data;								
				},
				dataType: "text"
				});
			
			$("#actionContainer").fadeOut("slow");
			$("#loadingContainer").fadeIn("slow");
			startWaitingForPlanResult();
		} else {
			alert("Application needs propery input data, please fill in the missing fields");
		}
	}
	
	function openEditPlanInputDialog(){
		// fetch selected option, it's input message and the orion editor element
		var optionId = $("#SelectOptionButton")[0].getAttribute("optionId");
		var optionInputMessageElement = $("#OptionListInput[optionid=" + optionId + "]")[0];
		var optionInputMessageValue = optionInputMessageElement.textContent;
		
		// format
		optionInputMessageValue = formatXml(optionInputMessageValue);
		
		editor.setValue(optionInputMessageValue);
		
		// reindent etc.
		var lineCount = editor.lineCount();		
		for(var i = 0 ; i < lineCount; i++){
			editor.indentLine(i);
		}
		
		editor.save();		
				
		$("#EditInputDialog").dialog("open");
		editor.refresh();
	}
	
	function closeEditPlanInputDialog(){
		var optionId = $("#SelectOptionButton")[0].getAttribute("optionId");
		var optionInputMessageElement = $("#OptionListInput[optionid=" + optionId + "]")[0];
		var editorContents = editor.getValue();
		
		// save input temporarily
		optionInputMessageElement.textContent = editorContents;
		
		$("#EditInputDialog").dialog("close");
	}
	
	function inputMessageHasMissingFields(){		
		var optionId = $("#SelectOptionButton")[0].getAttribute("optionId");
		var optionInputMessageElement = $("#OptionListInput[optionid=" + optionId + "]")[0];
		var optionInputMessageValue = optionInputMessageElement.textContent;

		// check whether we contain some empty/missing input
		if(optionInputMessageValue.indexOf("Please fill in") != -1){
			return true;
		} else {
			return false;
		}
	}
			
	function startWaitingForPlanResult() {
		setTimeout(
			function() {
				
				$.get(checkCallbackStatusUrl, function(data) {
					if (data["<%=CallbackStatusServlet.NO_CALLBACK_RECEIVED_YET%>"]) {
						startWaitingForPlanResult();
								} else {
									planResult = data;
									updateUiWithPlanResultMessages();

									$("#loadingContainer").fadeOut("slow");
									$("#applicationUrlContainer").fadeIn("slow");
								}
							}, "json");

		}, 3000);
	}
	
	function updateUiWithPlanResultMessages() {
		if("<%=CallbackEndpointServlet.NO_SELFSERVICE_MESSAGE%>" != planResult.selfserviceMessage) {
			$("#applicationUrlContainer #SelfserviceMessage").text(planResult.selfserviceMessage);
		}
		if("<%=CallbackEndpointServlet.NO_SELFSERVICE_POLICY_MESSAGE%>" != planResult.selfservicePolicyMessage) {
			setTimeout(function(){
				alert(planResult.selfservicePolicyMessage);
			}, 500);
		}
		if ("OK" == planResult.selfserviceStatus) {
			$("#successSymbol").show();
		}
		if ("FAILED" == planResult.selfserviceStatus) {
			$("#failedSymbol").show();
		}
		if ("" != planResult.applicationUrl) {
			$("#playSymbol").show();
		}
	}

	function startApplication() {
		window.open(planResult.applicationUrl);
	}

	function openOptionDialog() {
		$("#OptionDialog").dialog("open");
	}

	function setOptionButton(event, ui) {
		// get selected element
		var s = $(this).find('.ui-selected');

		// get child elements and data
		var optionIdName = s[0].getAttribute("optionId");
		var tdElements = s[0].getElementsByTagName("td");
		var imgElement = tdElements[0].getElementsByTagName("img")[0];
		var pElements = tdElements[1].getElementsByTagName("p");
		var imgSrc = imgElement.src;
		var optionName = pElements[0].innerHTML;
		var optionButton = $("#SelectOptionButton")[0];
		var optionButtonImage = $("#SelectedOptionImage")[0];
		var optionButtonText = $("#SelectOptionButtonText")[0];

		// set data on button
		optionButton.setAttribute("optionId", optionIdName);
		optionButtonImage.setAttribute("src", imgSrc);
		// we allow max. 21 chars, so if >21 we replace last 3 chars with dots
		if (optionName.length > 21) {
			optionName = optionName.substring(0, 18) + "...";
		}
		optionButtonText.textContent = optionName;

		// Close Dialog
		$("#OptionDialog").dialog("close");		
	}
	
	function formatXml(xml) {
		var formatted = '';
		var reg = /(>)(<)(\/*)/g;
		xml = xml.replace(reg, '$1\r\n$2$3');
		var pad = 0;
		jQuery.each(xml.split('\r\n'), function(index, node) {
		var indent = 0;
		if (node.match( /.+<\/\w[^>]*>$/ )) {
		indent = 0;
		} else if (node.match( /^<\/\w/ )) {
		if (pad != 0) {
		pad -= 1;
		}
		} else if (node.match( /^<\w[^>]*[^\/]>.*$/ )) {
		indent = 1;
		} else {
		indent = 0;
		}
		 
		var padding = '';
		for (var i = 0; i < pad; i++) {
		padding += ' ';
		}
		 
		formatted += padding + node + '\r\n';
		pad += indent;
		});
		 
		return formatted;
	} 
	
</script>

</head>

<body>
	<style>
div.mainContainer {
	background: url('images/appViewContainerBackground.jpg');
}

div.appIconRowContainer#row1 {
	top: 117px;
}

div.appIconRowContainer#row1>span {
	color: white;
	font-size: 50px;
	position: absolute;
	top: 103px;
	left: 200px;
	text-shadow: 7px 6px 10px #787878;
	width: 600px;
	overflow-x: hidden;
	overflow-y: hidden;
	height: 68px;
}

div.appIconRowContainer#row1>span.mirror {
	transform: scaleY(-1);
	-moz-transform: scaleY(-1);
	-webkit-transform: scaleY(-1);
	display: inline-block;
	top: 156px;
}
</style>


	<div class="mainContainer">
		<div class="appIconRowContainer" id="row1">
			<a href="javascript:void(0)">
				<div id="app1" class="appIconContainer">
					<img src="<%=app.convertToAbsoluteUrl(app.getIconUrl())%>">
					<div class="appIconContainerForeground"></div>
				</div>
			</a> <span> <%=app.getDisplayName()%>
			</span>
		</div>

		<div id="contentContainer">
			<div id="screenshotContainer">
				<img width="270"
					src="<%=app.convertToAbsoluteUrl(app.getImageUrl())%>" />
			</div>
			<div id="descriptionContainer">
				<%=app.getDescription()%>
			</div>
		</div>

		<div id="actionContainer">
			<div id="optionSelectionContainer">
				<!-- Here must be some fancy button, which opens a dialog to select the options -->
				<a href="javascript:openOptionDialog();"
					style="text-decoration: none;">
					<div id="SelectOptionButton"
						optionId="<%=app.getOptions().getOption().get(0).getId()%>">
						<img id="SelectedOptionImage"
							src="<%=app.convertToAbsoluteUrl(app.getOptions().getOption().get(0).getIconUrl())%>">
						<%
							if (app.getOptions().getOption().get(0).getName().length() > 11) {
						%>
						<span id="SelectOptionButtonText"> <%=app.getOptions().getOption().get(0).getName()
							.substring(0, 11)
							+ ".."%>
							<%
								} else {
							%> <span id="SelectOptionButtonText"> <%=app.getOptions().getOption().get(0).getName()%>
								<%
									}
								%>
						</span>
						</span>
					</div>
				</a>
			</div>

			<a href="javascript:startInstance();" id="StartInstanceButton"></a> 
			<a href="javascript:openEditPlanInputDialog();" id="EditInputButton">
				<span id="EditInputButtonText">Edit Parameters</span>
			</a>


			<div id="OptionDialog" title="Select Option">
				<table id="OptionList">
					<%
						for (ApplicationOption o : app.getOptions().getOption()) {
					%>
					<tr class="ui-widget-content" optionId="<%=o.getId()%>">
						<td id="OptionListItem"><img
							src="<%=app.convertToAbsoluteUrl(o.getIconUrl())%>"></td>
						<td id="OptionListItem">
							<p id="OptionListName"><%=o.getName()%></p>
							<p id="OptionListText"><%=o.getDescription()%></p>
							<div id="OptionListInput" style="display: none;" optionId="<%=o.getId()%>"><%=StringEscapeUtils.escapeHtml4(client.get(app, o.getPlanInputMessageUrl()))%></div>
						</td>
					</tr>
					<%
						}
					%>
				</table>
			</div>

			<div id="EditInputDialog" title="Please fill in the unspecified fields">			
				<textarea id="EditInputDialogContent" style="width:100%;height:auto"></textarea>	
			</div>

		</div>
		<div id="loadingContainer">
			<br>
			<!-- Generated by http://www.ajaxload.info/ -->
			<img src="images/loadingAnimation.gif"> <br> <span>Instantiating
				Application... Please wait</span>
		</div>
		<div id="applicationUrlContainer">
			<a id="playButton" href="javascript:startApplication();"><img
				id="playSymbol" style="border: 0px; display: none;" height="59px"
				src="images/playbutton.png"></a> <img id="failedSymbol"
				style="border: 0px; display: none;" height="59px"
				src="images/failSymbol.png" /> <img id="successSymbol"
				style="border: 0px; display: none;" height="59px"
				src="images/successSymbol.png" /> <br> <span
				id="SelfserviceMessage">Provisioning finished.</span>
			<!-- <span id="SelfservicePolicyMessage"></span> -->
		</div>
</body>

<%
	} catch (Throwable t) {
%>
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
</head>
<body>
	<br>
	<br>
	<center>
		<b><font color=red>ERROR: <%=t.getMessage()%></font></b>
	</center>
</body>
</html>
<%
	}
%>