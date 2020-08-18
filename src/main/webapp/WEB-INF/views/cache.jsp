<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="wedm" uri="http://jlab.org/wedm/functions"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>WEDM</title>
        <link rel="stylesheet" type="text/css" href="${wedm:epics2webPrefix()}/epics2web/resources/css/site.css?v=${initParam.epics2webReleaseNumber}"/>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/overview.css?v=${initParam.releaseNumber}"/>
    </head>
    <body>
        <h1>WEB Extensible Display Manager (WEDM)</h1>
        <h2>Screen Cache</h2>
        <table>
            <thead>
                <tr>
                    <th>File</th>
                    <th>Title</th>
                    <th>Size (Characters)</th>
                    <th>Time to Generate (Seconds)</th>
                    <th>Usage Count</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${screens}" var="screen">
                    <tr>
                        <c:url value="screen" var="url">
                            <c:param name="edl" value="${screen.canonicalPath}"/>
                        </c:url>
                        <td style="text-align: left;"><a href="${url}"><c:out value="${screen.canonicalPath}"/></a></td>
                        <td style="text-align: left;"><c:out value="${screen.title}"/></td>
                        <td style="text-align: right;"><fmt:formatNumber value="${screen.characterCount}"/></td>
                        <td style="text-align: right;"><fmt:formatNumber value="${screen.generateSeconds}"/></td>
                        <td style="text-align: right;"><fmt:formatNumber value="${screen.usageCount}"/></td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>                
    </body>
</html>
