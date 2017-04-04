<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>WEDM</title>
        <link rel="stylesheet" type="text/css" href="/epics2web/resources/css/site.css?v=${initParam.epics2webReleaseNumber}"/>  
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/overview.css?v=${initParam.releaseNumber}"/>
    </head>
    <body>
        <h1>WEB Extensible Display Manager (WEDM)</h1>
        <div id="version">Version: ${initParam.releaseNumber} (${initParam.releaseDate})</div>
        <table>
            <tbody>
                <tr>
                    <td>
                        <c:url var="url" value="browse">
                            <c:param name="dir" value="${edlRootDir}"/>
                        </c:url>
                        <a href="${url}">Browse for EDL files</a>    
                    </td>
                </tr>
            </tbody>
        </table>
        <h2>EDM Object Demos</h2>
        <h3>Graphics</h3>
        <table>
            <tbody>
                <tr>
                    <td><a href="screen?edl=wedm/Line.edl">Line (Path)</a></td>
                </tr>
                <tr>
                    <td><a href="screen?edl=wedm/Rectangle.edl">Rectangle</a></td>
                </tr>
                <tr>
                    <td><a href="screen?edl=wedm/Circle.edl">Circle (Ellipse)</a></td>
                </tr>
                <tr>
                    <td><a href="screen?edl=wedm/Arc.edl">Arc</a></td>
                </tr>
                <tr>
                    <td><a href="screen?edl=wedm/Text.edl">Text</a></td>
                </tr>
                <tr>
                    <td><a href="screen?edl=wedm/Embedded.edl">Embedded</a></td>
                </tr>                
            </tbody>
        </table>
        <h3>Monitors</h3>
        <table>
            <tbody>
                <tr>
                    <td><a href="screen?edl=wedm/DynamicText.edl">Dynamic Text</a></td>
                </tr>
                <tr>
                    <td><a href="screen?edl=wedm/Byte.edl">Byte</a></td>
                </tr>
                <tr>
                    <td><a href="screen?edl=wedm/BarMeter.edl">Bar Meter</a></td>
                </tr>
                <tr>
                    <td><a href="screen?edl=wedm/DynamicSymbol.edl">Dynamic Symbol</a></td>
                </tr>
            </tbody>
        </table>        
        <h3>Controls</h3>
        <table>
            <tbody>
                <tr>
                    <td><a href="screen?edl=wedm/Button.edl">Button</a></td>
                </tr>
                <tr>
                    <td><a href="screen?edl=wedm/TextControl.edl">Text Control</a></td>
                </tr>
                <tr>
                    <td><a href="screen?edl=wedm/ChoiceButton.edl">Choice Button</a></td>
                </tr>
                <tr>
                    <td><a href="screen?edl=wedm/RelatedDisplay.edl">Related Display</a></td>
                </tr>                
            </tbody>
        </table>       
    </body>
</html>
