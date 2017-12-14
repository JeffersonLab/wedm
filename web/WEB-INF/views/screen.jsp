<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="wedm" uri="http://jlab.org/wedm/functions"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>WEDM - ${screen.title ne null ? screen.title : param.edl}</title>
        <link rel="stylesheet" type="text/css" href="${wedm:contextPrefix()}/epics2web/resources/css/epics2web.min.css?v=${initParam.epics2webReleaseNumber}"/>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/screen.css?v=${initParam.releaseNumber}"/>
        <c:choose>
            <c:when test="${initParam.productionRelease eq 'true'}">
                <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/combined.min.css?v=${initParam.releaseNumber}"/>
            </c:when>
            <c:otherwise>
                <c:forEach items="${widgets}" var="name">
                    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/widgets/${fn:escapeXml(wedm:escapeFileName(name))}/widget.css?v=${initParam.releaseNumber}"/>
                </c:forEach>
            </c:otherwise>
        </c:choose>
        <style type="text/css">
${screen.getCss()}
        </style>
    </head>
    <body>
        <svg id="svg-defs">
            <defs>
                <symbol id="arrow-head" viewBox="0 0 10 10">
                    <path d="M0,2 L0,8 8,5 z"/>
                </symbol>
            </defs>
        </svg>
        <div class="connection-state-panel">
            <img class="ws-disconnected" title="Socket Disconnected" width="24px" height="24px" style="vertical-align: middle;" src="${wedm:contextPrefix()}/epics2web/resources/img/disconnected.svg?v=${initParam.epics2webReleaseNumber}"/>
            <img class="ws-connecting connecting-spinner" title="Socket Connecting" width="24px" height="24px" style="vertical-align: middle; display: none;" src="${wedm:contextPrefix()}/epics2web/resources/img/connecting.svg?v=${initParam.epics2webReleaseNumber}"/>                
            <img class="ws-connected" title="Socket Connected" width="24px" height="24px" style="vertical-align: middle; display: none;" src="${wedm:contextPrefix()}/epics2web/resources/img/connected.svg?v=${initParam.epics2webReleaseNumber}"/>
        </div>        
${screen.getHtml()}
        <div id="tooltip"/>
        <script type="text/javascript" src="${wedm:contextPrefix()}/epics2web/resources/js/jquery-1.10.2.min.js"></script>
        <script type="text/javascript" src="${wedm:contextPrefix()}/epics2web/resources/js/epics2web.min.js?v=${initParam.epics2webReleaseNumber}"></script> 
        <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/screen.js?v=${initParam.releaseNumber}"></script>
        <c:choose>
            <c:when test="${initParam.productionRelease eq 'true'}">
        <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/combined.min.js?v=${initParam.releaseNumber}"></script>
            </c:when>
            <c:otherwise>
                <c:forEach items="${widgets}" var="name">
        <script type="text/javascript" src="${pageContext.request.contextPath}/resources/widgets/${fn:escapeXml(wedm:escapeFileName(name))}/widget.js?v=${initParam.releaseNumber}"></script>
                </c:forEach>
            </c:otherwise>
        </c:choose>
        <script type="text/javascript">
${screen.getJs()}
jlab.wedm.macroString = '${fn:escapeXml(macroString)}';
jlab.contextPrefix = '${wedm:contextPrefix()}';
        </script>       
    </body>
</html>
