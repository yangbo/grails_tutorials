<!DOCTYPE html>
<html>
<head>
    <title>websocket演示</title>
    <meta name="layout" content="main"/>
    <asset:javascript src="application"/>
    <!-- doc: http://jmesnil.net/stomp-websocket/doc/ -->
    <asset:javascript src="spring-websocket"/>
    <asset:javascript src="sockjs_reconnect"/>
</head>

<body style="margin: 10px">

<button id="helloButton">hello</button>

<p id="status">状态</p>
<hr/>

<p id="last-message" style="float: left; width: 300px">最新消息</p>

<div id="helloDiv" style="float: right; width: 300px"></div>

<script type="text/javascript">
    $(function () {
        let inDest = "/topic/hello";
        let outDest = "/app/hello";
        let socket = new SockJS("${createLink(uri: '/stomp')}");
        let client = webstomp.over(socket);

        client.connect({}, function () {
            client.subscribe(inDest, function (message) {
                $("#helloDiv").append("<p>" + JSON.parse(message.body) + "</p>");
            });
        }, function(){
            console.log("连接断开...");
        });

        $("#helloButton").click(function () {
            client.send(outDest, JSON.stringify("世界"));
        });

        // re-connect
        %{--let new_status = function (status) {--}%
        %{--    $('#status').text(status);--}%
        %{--    if (status === 'connected') {--}%
        %{--        sock.subscribe(inDest, function (message) {--}%
        %{--            $("#last-message").append("<p>" + JSON.parse(message.body) + "</p>");--}%
        %{--        });--}%
        %{--    }--}%
        %{--};--}%
        %{--let on_message = function (msg) {--}%
        %{--    $('#last-message').text(msg.data);--}%
        %{--    setTimeout(function () {--}%
        %{--            if (sock.conn) {--}%
        %{--                sock.send(outDest, JSON.stringify('ping'));--}%
        %{--            }--}%
        %{--        },--}%
        %{--        150);--}%
        %{--};--}%

        %{--let sock = new SockReconnect("${createLink(uri: '/stomp')}", null, new_status, on_message);--}%
        %{--if (window.addEventListener) {--}%
        %{--    window.addEventListener('load', sock.connect, false);--}%
        %{--} else {--}%
        %{--    window.attachEvent('onload', sock.connect);--}%
        %{--}--}%
    });
</script>

</body>
</html>