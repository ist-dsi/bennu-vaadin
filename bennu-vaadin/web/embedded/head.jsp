<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<bean:define id="theme" name="virtualHost" property="theme.name"/>

<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() + "/VAADIN/themes/" + theme + "/styles.css"%>">

<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() + "/head.css"%>">
	
	<!--[if IE]>
    	<script src="http://html5shiv.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->
    
<script type="text/javascript">
	var vaadin = {
        vaadinConfigurations: {
            'vaadin': {
                appUri:'<%= request.getContextPath() + "/vaadin"%>',
                pathInfo: '/',
                themeUri: '<%= request.getContextPath() + "/VAADIN/themes/" + theme %>',
                versionInfo : {
                    vaadinVersion:"6.5.0-INTERNAL-NONVERSIONED-DEBUG-BUILD",
                    applicationVersion:"NONVERSIONED"
                }
            }
        }};
</script>
<script language='javascript'
	src='<%=request.getContextPath()
		    + "/VAADIN/widgetsets/pt.ist.vaadinframework.AddonsWidgetSet/pt.ist.vaadinframework.AddonsWidgetSet.nocache.js"%>'></script>
     
