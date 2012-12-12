<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>

<bean:define id="theme" name="virtualHost" property="theme.name"/>

<script type="text/javascript">
//<![CDATA[
if(!vaadin || !vaadin.vaadinConfigurations) {
 	if(!vaadin) { var vaadin = {}} 
	vaadin.vaadinConfigurations = {};
	if (!vaadin.themesLoaded) { vaadin.themesLoaded = {}; }
	document.write('<iframe tabIndex="-1" id="__gwt_historyFrame" style="position:absolute;width:0;height:0;border:0;overflow:hidden;" src="javascript:false"></iframe>');
	document.write("<script language='javascript' src='<%= request.getContextPath()%>/VAADIN/widgetsets/pt.ist.vaadinframework.widgetset.BennuWidgetSet/pt.ist.vaadinframework.widgetset.BennuWidgetSet.nocache.js'><\/script>");
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
setTimeout('if (typeof pt_ist_vaadinframework_widgetset_BennuWidgetSet == "undefined") {alert("Failed to load the widgetset: <%= request.getContextPath() %>/VAADIN/widgetsets/pt_ist_vaadinframework_widgetset_BennuWidgetSet/pt_ist_vaadinframework_widgetset_BennuWidgetSet.nocache.js")};',15000);
//]]>
</script>

<div id="vaadin" class="<%= "v-app v-theme-" + theme + " v-app-EmbeddedApplication" %>" ></div>
