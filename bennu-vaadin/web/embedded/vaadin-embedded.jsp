<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<bean:define id="theme" name="virtualHost" property="theme.name"/>

<div id="vaadin" class="<%= "v-app v-theme-" + theme + " v-app-EmbeddedApplication" %>" />