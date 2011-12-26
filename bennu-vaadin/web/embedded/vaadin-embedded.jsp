<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<bean:define id="theme" name="virtualHost" property="theme.name"/>

<script type="text/javascript">
//<![CDATA[
if(!vaadin || !vaadin.vaadinConfigurations) {
 	if(!vaadin) { var vaadin = {}} 
	vaadin.vaadinConfigurations = {};
	if (!vaadin.themesLoaded) { vaadin.themesLoaded = {}; }
	document.write('<iframe tabIndex="-1" id="__gwt_historyFrame" style="position:absolute;width:0;height:0;border:0;overflow:hidden;" src="javascript:false"></iframe>');
	document.write("<script language='javascript' src='<%= request.getContextPath()%>/VAADIN/widgetsets/pt.ist.bennu.widgetset.BennuWidgetSet/pt.ist.bennu.widgetset.BennuWidgetSet.nocache.js'><\/script>");
}
vaadin.vaadinConfigurations["vaadin"] = {appUri:'<%= request.getContextPath() + "/vaadin"%>', standalone: true, themeUri:"<%= request.getContextPath() + "/VAADIN/themes/" + theme %>", versionInfo : {vaadinVersion:"6.7.2",applicationVersion:"NONVERSIONED"}};
//]]>
</script>
<script type="text/javascript">
//<![CDATA[
if(!vaadin.themesLoaded['<%= theme %>']) {
	var stylesheet = document.createElement('link');
	stylesheet.setAttribute('rel', 'stylesheet');
	stylesheet.setAttribute('type', 'text/css');
	stylesheet.setAttribute('href', '<%= request.getContextPath() %>/VAADIN/themes/<%= theme %>/styles.css');
	document.getElementsByTagName('head')[0].appendChild(stylesheet);
	vaadin.themesLoaded['<%= theme %>'] = true;
}
//]]>
</script>
<script type="text/javascript">
//<![CDATA[
setTimeout('if (typeof pt_ist_bennu_widgetset_BennuWidgetSet == "undefined") {alert("Failed to load the widgetset: <%= request.getContextPath() %>/VAADIN/widgetsets/pt_ist_bennu_widgetset_BennuWidgetSet/pt_ist_bennu_widgetset_BennuWidgetSet.nocache.js")};',15000);
//]]>
</script>

<div id="vaadin" class="<%= "v-app v-theme-" + theme + " v-app-EmbeddedApplication" %>" ></div>