<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="wedm" uri="http://jlab.org/wedm/functions"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>WEDM - Browse for EDL Files</title>
        <link rel="stylesheet" type="text/css" href="${wedm:epics2webPrefix()}/epics2web/resources/css/site.css?v=${initParam.epics2webReleaseNumber}"/>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/browse.css?v=${initParam.releaseNumber}"/>
    </head>
    <body>
        <h1>WEB Extensible Display Manager (WEDM)</h1>
        <h2>Browse for EDL Files</h2>
        <label for="macros">Macros (optional): </label><input id="macros" type="text" placeholder="comma separated name=value pairs"/>
        <h3>
            <span id="up-directory-widget">
                <c:url var="url" value="browse">
                    <c:param name="dir" value="${parent}"/>
                </c:url>        
                <c:if test="${not parentOutside}">
                    <a href="${url}">Parent Directory</a>
                </c:if>            
            </span>
            Current Directory: <c:out value="${param.dir}"/>
        </h3>
        <table id="files">
            <tbody>
                <c:forEach items="${files}" var="file">
                    <tr>
                        <td>
                            <c:choose>
                                <c:when test="${file.isDirectory()}">
                                    <c:url var="url" value="browse">
                                        <c:param name="dir" value="${file.absolutePath}"/>
                                    </c:url>
                                    <a href="${url}"><c:out value="${file.name}"/></a>
                                </c:when>
                                <c:when test="${file.isFile() and file.name.endsWith('.edl')}">
                                    <c:url var="url" value="screen">
                                        <c:param name="edl" value="${file.absolutePath}"/>
                                    </c:url>
                                    <a href="${url}" class="edl-file"><c:out value="${file.name}"/></a>
                                </c:when>
                                <c:otherwise>
                                    <c:out value="${file.name}"/>
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </c:forEach>                    
            </tbody>
        </table>
        <script type="text/javascript" src="${wedm:epics2webPrefix()}/epics2web/resources/js/jquery-1.10.2.min.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/browse.js?v=${initParam.releaseNumber}"></script>        
    </body>
</html>
