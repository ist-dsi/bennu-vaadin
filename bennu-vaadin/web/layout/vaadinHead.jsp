<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>

<%
	final String contextPath = request.getContextPath();
%>
<bean:define id="theme" name="virtualHost" property="theme.name"/>
<link rel="stylesheet" type="text/css" href="<%=contextPath + "/VAADIN/themes/" + theme + "/styles.css"%>">
