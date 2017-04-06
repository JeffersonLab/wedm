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
        <h2>Objects</h2>
        <h3>Graphics</h3>
        <table>
            <tbody>
                <tr>
                    <td><a href="screen?edl=wedm/objects/Line.edl">Line (Path)</a></td>
                </tr>
                <tr>
                    <td><a href="screen?edl=wedm/objects/Rectangle.edl">Rectangle</a></td>
                </tr>
                <tr>
                    <td><a href="screen?edl=wedm/objects/Circle.edl">Circle (Ellipse)</a></td>
                </tr>
                <tr>
                    <td><a href="screen?edl=wedm/objects/Arc.edl">Arc</a></td>
                </tr>
                <tr>
                    <td><a href="screen?edl=wedm/objects/Text.edl">Text</a></td>
                </tr>
                <tr>
                    <td><a href="screen?edl=wedm/objects/Embedded.edl">Embedded</a></td>
                </tr>                
            </tbody>
        </table>
        <h3>Monitors</h3>
        <table>
            <tbody>
                <tr>
                    <td><a href="screen?edl=wedm/objects/DynamicText.edl">Dynamic Text</a></td>
                </tr>
                <tr>
                    <td><a href="screen?edl=wedm/objects/Byte.edl">Byte</a></td>
                </tr>
                <tr>
                    <td><a href="screen?edl=wedm/objects/BarMeter.edl">Bar Meter</a></td>
                </tr>
                <tr>
                    <td><a href="screen?edl=wedm/objects/DynamicSymbol.edl">Dynamic Symbol</a></td>
                </tr>
            </tbody>
        </table>        
        <h3>Controls</h3>
        <table>
            <tbody>
                <tr>
                    <td><a href="screen?edl=wedm/objects/Button.edl">Button</a></td>
                </tr>
                <tr>
                    <td><a href="screen?edl=wedm/objects/TextControl.edl">Text Control</a></td>
                </tr>
                <tr>
                    <td><a href="screen?edl=wedm/objects/ChoiceButton.edl">Choice Button</a></td>
                </tr>
                <tr>
                    <td><a href="screen?edl=wedm/objects/RelatedDisplay.edl">Related Display</a></td>
                </tr>                
            </tbody>
        </table> 
        <h2>Features</h2>
        <table>
            <tbody>
                <tr>
                    <td><a href="screen?edl=wedm/features/LocalVariable.edl">Local Variables</a></td>
                </tr>
                <tr>
                    <td><a href="screen?edl=wedm/features/Macro.edl&%24(A)=World">Macros</a></td>
                </tr>
                <tr>
                    <td><a href="screen?edl=wedm/features/TextResize.edl">Text Resize</a></td>
                </tr>            
            </tbody>
        </table>
        <div id="version">Version: ${initParam.releaseNumber} (${initParam.releaseDate})</div>        
    </body>
</html>
