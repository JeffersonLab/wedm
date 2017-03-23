<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>WEDM - ${screen.title ne null ? screen.title : param.edl}</title>
        <link rel="stylesheet" type="text/css" href="/epics2web/resources/css/epics2web.css?v=${initParam.epics2webReleaseNumber}"/>        
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/screen.css?v=${initParam.releaseNumber}"/>
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
        <img class="ws-disconnected" title="Socket Disconnected" width="24px" height="24px" style="vertical-align: middle;" src="/epics2web/resources/img/disconnected.svg?v=${initParam.epics2webReleaseNumber}"/>
        <img class="ws-connecting connecting-spinner" title="Socket Connecting" width="24px" height="24px" style="vertical-align: middle; display: none;" src="/epics2web/resources/img/connecting.svg?v=${initParam.epics2webReleaseNumber}"/>                
        <img class="ws-connected" title="Socket Connected" width="24px" height="24px" style="vertical-align: middle; display: none;" src="/epics2web/resources/img/connected.svg?v=${initParam.epics2webReleaseNumber}"/>
    </div>        
${screen.getHtml()} 
    <script type="text/javascript" src="/epics2web/resources/js/jquery-1.10.2.min.js"></script>
    <script type="text/javascript" src="/epics2web/resources/js/epics2web.js?v=${initParam.epics2webReleaseNumber}"></script>    
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/screen.js?v=${initParam.releaseNumber}"></script>
    <script type="text/javascript">
${screen.getJs()}
    </script>       
</body>
</html>
