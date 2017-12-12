<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="wedm" uri="http://jlab.org/wedm/functions"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>WEDM</title> 
        <link rel="stylesheet" type="text/css" href="${wedm:contextPrefix()}/epics2web/resources/css/site.css?v=${initParam.epics2webReleaseNumber}"/> 
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/overview.css?v=${initParam.releaseNumber}"/>
    </head>
    <body>
        <h1>WEB Extensible Display Manager (WEDM)</h1>
        <h2>File Not Found</h2>
        <table>
            <thead>
                <tr>
                    <th>File</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td style="text-align: left;"><c:out value="${edlname}"/></td>
                </tr>
            </tbody>
        </table>                
    </body>
</html>
