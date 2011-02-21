<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<bean:define id="theme" name="virtualHost" property="theme.name"/>

<html>
	<head>
		<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() + "/VAADIN/themes/" + theme + "/styles.css"%>">

		<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() + "/head.css"%>">
	
		<!--[if IE]>
   			<script src="<%= request.getContextPath() + "/VAADIN/js/html5.js"%>"></script>
		<![endif]-->

		<script type="text/javascript"> 
			//<![CDATA[
			if(!vaadin || !vaadin.vaadinConfigurations) {
 			if(!vaadin) { var vaadin = {}} 
			vaadin.vaadinConfigurations = {};
			if (!vaadin.themesLoaded) { vaadin.themesLoaded = {}; }
				vaadin.debug = true;
				document.write('<iframe tabIndex="-1" id="__gwt_historyFrame" style="position:absolute;width:0;height:0;border:0;overflow:hidden;" src="javascript:false"></iframe>');
				document.write("<script language='javascript' src='<%= request.getContextPath() + "/VAADIN/widgetsets/com.vaadin.terminal.gwt.DefaultWidgetSet/com.vaadin.terminal.gwt.DefaultWidgetSet.nocache.js"%>'><\/script>");
			}
			vaadin.vaadinConfigurations["vaadin"] = {appUri:'<%= request.getContextPath() + "/vaadin"%>', pathInfo: '/', themeUri:'<%= request.getContextPath() + "/VAADIN/themes/" + theme %>', versionInfo : {vaadinVersion:"6.5.0",applicationVersion:"NONVERSIONED"}};
		//]]>
		</script>
	</head>
	<body class="top">
		<div id="vaadin" class="v-app"/>
	</body>
</html>