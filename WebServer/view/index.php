<?php
session_start();
header("Content-type: text/html; charset=utf-8");

require_once $_SERVER['DOCUMENT_ROOT'] . '/controller/conn/settings.php';
require_once $_SERVER['DOCUMENT_ROOT'] . '/controller/conn/Session.php';
require_once $_SERVER['DOCUMENT_ROOT'] . '/controller/conn/tokenInterceptor.php';//check token
use \Workerman\Worker;
use \Workerman\WebServer;
use \GatewayWorker\Lib\Gateway;

if (
    isset($_REQUEST[post_user_email]) &&
    isset($_REQUEST[post_meeting_url]) &&
    isset($_REQUEST[post_user_family_name]) &&
    isset($_REQUEST[post_user_given_name])

) {
    ?>
    <html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
        <title>canvas</title>

        <meta charset="UTF-8">
        <meta content="width=device-width,initial-scale=1.0,maximum-scale=1.0,user-scalable=no" name="viewport">

        <style type="text/css">
            .fs-container {
                width: auto;
                margin: auto;
                max-width: 100%;
            }

        </style>
        <link href="css/boardcanvas.css" rel="stylesheet">
        <link href="css/bootstrap.min.css" rel="stylesheet">
        <link href="css/style.css" rel="stylesheet">
        <script type="text/javascript" src="js/swfobject.js"></script>
        <script type="text/javascript" src="js/web_socket.js"></script>
        <script type="text/javascript" src="js/jquery.min.js"></script>
        <script src="js/jquery-1.8.2.js"></script>
        <script src="js/react-0.14.3.js"></script>
        <script src="js/literallycanvas.js"></script>


        <script type="text/javascript">
            /**
             * 开启flash的websocket debug
             */
            WEB_SOCKET_DEBUG = true;
            /**
             * 声明变量
             */
            var ws, name, email, familyName, givenName, client_list = {}, roomid;
            var lc;
            var localStorageKey = 'drawing';
            var select_client_id = 'all';
            var myid = '';
            var unsubscribeofchange;

            /**
             * 连接服务端
             */
            function connect() {
                // 创建websocket
                ws = new WebSocket("ws://118.89.102.238:7272");
                //建立连接
                ws.onopen = onopen;
                // 当有消息时根据消息类型显示不同信息
                ws.onmessage = onmessage;
                ws.onclose = function () {
                    console.log("连接关闭，定时重连");
                };
                ws.onerror = function () {
                    console.log("出现错误");
                };
            }

            /**
             * 连接建立时发送登录信息
             */
            function onopen() {
                familyName = '<?php echo $_REQUEST[post_user_family_name]; ?>';
                givenName = '<?php echo $_REQUEST[post_user_given_name];?>';
                email = '<?php echo $_REQUEST[post_user_email];?>';
                name = email + "-" + familyName + " " + givenName;
                roomid = '<?php echo $_REQUEST[post_meeting_url];?>';
                // 登录
                var login_data = '{"type":"login","client_name":"' + name.replace(/"/g, '\\"') + '","room_id":"' + roomid + '"}';
                console.log("websocket握手成功，发送登录数据:" + login_data);
                ws.send(login_data);
            }

            /**
             * 服务端发来消息时
             */
            function onmessage(e) {
                var data = eval("(" + e.data + ")");
                switch (data['type']) {
                    /**
                     * Events.php中返回的init类型的消息，将client_id发给后台进行uid绑定
                     */
                    case 'init':
                        // 利用jquery发起ajax请求，将client_id发给后端进行uid绑定
//                    $.post(<?php //echo $_SERVER['DOCUMENT_ROOT'] . 'controller/meeting/bind.php';?>//,
//                        {'client_id': data['client_id']},
//                        function (data) {
//                            myid = data['client_id'];//当前用户的 client_id
//                        },
//                        'json');
                        myid = data['client_id'];
                        break;
                    /**
                     * 心跳
                     */
                    case 'ping':
                        ws.send('{"type":"pong"}');
                        break;
                    /**
                     * 登录 更新用户列表
                     */
                    case 'login':
                        //{"type":"login","client_id":xxx,"client_name":"xxx","client_list":"[...]","time":"xxx"}
//                say(data['client_id'], data['client_name'],  data['client_name']+' 加入了聊天室', data['time']);
                        if (data['client_list']) {
                            client_list = data['client_list'];
                        }
                        else {
                            client_list[data['client_id']] = data['client_name'];
                        }
                        flush_client_list();
                        console.log(data['client_name'] + "登录成功");
                        break;
                    /**
                     * 接受到数据
                     */
                    case 'say':
                        //{"type":"say","from_client_id":xxx,"to_client_id":"all/client_id","content":"xxx","time":"xxx"}
                        say(data['from_client_id'], data['from_client_name'], data['content'], data['time']);
                        break;
                    /**
                     * 其他用户退出
                     */
                    case 'logout':
                        //{"type":"logout","client_id":xxx,"time":"xxx"}
//                say(data['from_client_id'], data['from_client_name'], data['from_client_name']+' 退出了', data['time']);
                        delete client_list[data['from_client_id']];
                        flush_client_list();
                }
            }


            /**
             * 提交自己改变的数据
             */
            function onSubmit(datajson) {
                var to_client_id = $("#client_list option:selected").attr("value");
                var to_client_name = $("#client_list option:selected").text();
                var say_data = '{"type":"say","to_client_id":"' + to_client_id + '","to_client_name":"' + to_client_name + '","content":"' + datajson.replace(/\\"/g, '425D8E69BF45B845CB7CF50FA43D64C68D379A46').replace(/"/g, '\\"').replace(/\n/g, '\\n').replace(/\r/g, '\\r') + '"}';
                ws.send(say_data);

            }

            /**
             * 加载接收到的数据
             */
            function say(from_client_id, from_client_name, content, time) {
                var newdata = content.replace(/&quot;/g, '"').replace(/425D8E69BF45B845CB7CF50FA43D64C68D379A46/g, '\\"');
                unsubscribeofchange();
                /**
                 * 如果消息不是自己发出去的
                 */
                if (myid != '' && myid != from_client_id) {

                    lc.loadSnapshot(JSON.parse(newdata));
                }
                listenDrawingChange();

            }
            /**
             * 刷新用户列表框
             */
            function flush_client_list() {
                var userlist_window = $("#userlist");
                var client_list_slelect = $("#client_list");
                userlist_window.empty();
                client_list_slelect.empty();
                userlist_window.append('<h4>在线用户</h4><ul>');
                client_list_slelect.append('<option value="all" id="cli_all">所有人</option>');
                for (var p in client_list) {
                    userlist_window.append('<li id="' + p + '">' + client_list[p] + '</li>');
                    client_list_slelect.append('<option value="' + p + '">' + client_list[p] + '</option>');
                }
                $("#client_list").val(select_client_id);
                userlist_window.append('</ul>');
            }
            /**
             * 监听画板的变化
             */
            function listenDrawingChange() {
                unsubscribeofchange = lc.on('drawingChange', function () {
                    var content = JSON.stringify(lc.getSnapshot());
                    onSubmit(content);
                });
            }
            /**
             * 初始化画板
             */
            $(document).ready(function () {

                lc = LC.init(document.getElementById("lc"), {
                    imageURLPrefix: 'images',
                    toolbarPosition: 'top',
                    defaultStrokeWidth: 2,
                    strokeWidths: [1, 2, 4, 8, 15]
                });
                listenDrawingChange();


            });

        </script>


    </head>

    <!--body onload时 建立连接-->
    <body onload="connect();">

    <!--主体-->
    <div class="container">
        <div class="row clearfix">
            <div class="fs-container col-md-12 column">
                <div id="lc"></div>
            </div>
            <form style="display: none">
                <select style="margin-bottom:8px" id="client_list">
                    <option value="all">所有人</option>
                </select>
                <textarea class="textarea thumbnail" id="textarea"></textarea>
                <div class="say-btn"><input type="submit" class="btn btn-default" value="发表"/></div>
            </form>

            <div class="col-md-3 column">
                <div class="thumbnail">
                    <div class="caption" id="userlist"></div>
                </div>
            </div>
        </div>
    </div>
    </body>
    </html>


    <?php

} else {
    printResult(NO_PARAMS_RECEIVE, '服务器未收到参数', -1);
}
?>
