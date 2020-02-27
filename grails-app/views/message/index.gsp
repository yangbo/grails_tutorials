<!DOCTYPE html>
<html>
<head>
    <title>websocket演示</title>
    <meta name="layout" content="main"/>

    <asset:javascript src="application"/>
    <asset:javascript src="spring-websocket"/>

    <script type="text/javascript">
        $(function () {
            var socket = new SockJS("${createLink(uri: '/stomp')}");
            var client = webstomp.over(socket);

            client.connect({}, function () {
                client.subscribe("/topic/hello", function (message) {
                    $("#helloDiv").append("<p>"+JSON.parse(message.body)+"</p>");
                });
            });

            $("#helloButton").click(function () {
                client.send("/app/hello", JSON.stringify("世界"));
            });
        });
    </script>
</head>

<body>
<button id="helloButton">hello</button>

<div id="helloDiv"></div>
</body>
</html>