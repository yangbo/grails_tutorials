<!doctype html>
<html>
    <head>
        <title>HTTP请求错误</title>
        <meta name="layout" content="main">
        <g:if env="development"><asset:stylesheet src="errors.css"/></g:if>
    </head>
    <body>
        <ul class="errors">
            <li>错误码: (${response.status})</li>
            <li>请求网址: ${request.requestURL}</li>
        </ul>
    </body>
</html>
