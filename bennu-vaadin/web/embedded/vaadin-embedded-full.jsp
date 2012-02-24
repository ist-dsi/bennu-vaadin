<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<bean:define id="theme" name="virtualHost" property="theme.name"/>

<html>
	<head>
		<title>
			<logic:notEmpty name="virtualHost" property="htmlTitle">
				<bean:write name="virtualHost" property="htmlTitle" />
			</logic:notEmpty> 
			<logic:empty name="virtualHost" property="htmlTitle">
				<bean:write name="virtualHost" property="applicationTitle" />
			</logic:empty>
		</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
		<meta http-equiv="X-UA-Compatible" content="chrome=1"/>
		
		<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() + "/VAADIN/themes/" + theme + "/styles.css"%>">

		<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() + "/head.css"%>">
	
		<!--[if IE]>
   			<script src="<%= request.getContextPath() + "/VAADIN/js/html5.js"%>"></script>
		<![endif]-->
	</head>
	<body class="top">
		<script type="text/javascript">
		//<![CDATA[
		if(!vaadin || !vaadin.vaadinConfigurations) {
		 	if(!vaadin) { var vaadin = {}} 
			vaadin.vaadinConfigurations = {};
			if (!vaadin.themesLoaded) { vaadin.themesLoaded = {}; }
			vaadin.debug = true;
			document.write('<iframe tabIndex="-1" id="__gwt_historyFrame" style="position:absolute;width:0;height:0;border:0;overflow:hidden;" src="javascript:false"></iframe>');
			document.write("<script language='javascript' src='<%= request.getContextPath()%>/VAADIN/widgetsets/pt.ist.vaadinframework.widgetset.BennuWidgetSet/pt.ist.vaadinframework.widgetset.BennuWidgetSet.nocache.js'><\/script>");
		}
		vaadin.vaadinConfigurations["vaadin"] = {appUri:'<%= request.getContextPath() + "/vaadin"%>', themeUri:"<%= request.getContextPath() + "/VAADIN/themes/" + theme %>", versionInfo : {vaadinVersion:"6.5.2",applicationVersion:"NONVERSIONED"}};
		//]]>
		</script>
		<script type="text/javascript">
		//<![CDATA[
		setTimeout('if (typeof pt_ist_vaadinframework_widgetset_BennuWidgetSet == "undefined") {alert("Failed to load the widgetset: /VAADIN/widgetsets/pt_ist_vaadinframework_widgetset_BennuWidgetSet/pt_ist_vaadinframework_widgetset_BennuWidgetSet")};',15000);
		//]]>
		</script>
		<div id="vaadin" class="v-app"/>
	</body>
</html>