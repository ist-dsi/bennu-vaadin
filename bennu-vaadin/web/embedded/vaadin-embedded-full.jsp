<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<bean:define id="theme" name="virtualHost" property="theme.name"/>

<html>
	<head>
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
			document.write("<script language='javascript' src='<%= request.getContextPath()%>/VAADIN/widgetsets/pt.ist.bennu.addons.widgetset.AddonsWidgetSet/pt.ist.bennu.addons.widgetset.AddonsWidgetSet.nocache.js'><\/script>");
		}
		vaadin.vaadinConfigurations["vaadin"] = {appUri:'<%= request.getContextPath() + "/vaadin"%>', themeUri:"<%= request.getContextPath() + "/VAADIN/themes/" + theme %>", versionInfo : {vaadinVersion:"6.5.2",applicationVersion:"NONVERSIONED"}};
		//]]>
		</script>
		<script type="text/javascript">
		//<![CDATA[
		setTimeout('if (typeof pt_ist_bennu_addons_widgetset_AddonsWidgetSet == "undefined") {alert("Failed to load the widgetset: /VAADIN/widgetsets/pt_ist_bennu_addons_widgetset_AddonsWidgetSet/pt_ist_bennu_addons_widgetset_AddonsWidgetSet")};',15000);
		//]]>
		</script>
		<div id="vaadin" class="v-app"/>
	</body>
</html>