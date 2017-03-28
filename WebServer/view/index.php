<?php
session_start();
header("Content-type: text/html; charset=utf-8");

require_once $_SERVER['DOCUMENT_ROOT'] . '/controller/conn/settings.php';
require_once $_SERVER['DOCUMENT_ROOT'] . '/controller/conn/Session.php';
require_once $_SERVER['DOCUMENT_ROOT'] . '/controller/conn/tokenInterceptor.php';//check token
require_once $_SERVER['DOCUMENT_ROOT'] . '/util/EncryptUtil.php';
use \Workerman\Worker;
use \Workerman\WebServer;
use \GatewayWorker\Lib\Gateway;

if (
    isset($_REQUEST[post_user_email]) &&
    isset($_REQUEST[post_meeting_url]) &&
    isset($_REQUEST[post_user_family_name]) &&
    isset($_REQUEST[post_meeting_check_in_type]) &&
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
                width: 100%;
                height: 100%;
                margin: auto;
                /*max-width: 100%;*/
            }

            canvas {
                width: auto;
                height: auto;
                max-width: 100%;
                max-height: 100%;
            }

            .lc-picker {
                height: auto;
                max-height: 100%;
            }

            #lc {
                width: 100%;
                height: 100%;

            }

            .label {
                font-size: 100%;
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
            var host_email, check_in_type;
            var init_times = 0;

            /**
             * 连接服务端
             */
            function connect() {
                check_in_type = <?php echo $_REQUEST[post_meeting_check_in_type]?>;

                if (check_in_type == 1) {
                    <?php
                    if(isset($_REQUEST[post_meeting_host_email])){
                    Session::set(SESSION_HOST_EMAIL, $_REQUEST[post_meeting_host_email], 2592000);//30天过期
                    ?>
                    host_email = '<?php echo $_REQUEST[post_meeting_host_email]?>';
                    console.log("host_email赋值" + host_email);
                    console.log("check_in_type赋值" + check_in_type);
                    <?php
                    }else {
                    ?>
                    return;
                    <?php
                    }
                    ?>

                }
                console.log("即将建立连接");

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
                var login_data = '{"type":"login","client_name":"' + name.replace(/"/g, '\\"') + '","client_email":"' + email + '","room_id":"' + roomid + '"}';
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
//                        if (data['client_list']) {
//                            client_list = data['client_list'];
//                        }
//                        else {
//                            client_list[data['client_id']] = data['client_name'];
//                        }
//                        flush_client_list();
                        console.log(data['client_name'] + "登录成功");
                        if (check_in_type == 1 && init_times == 0) {
                            init_times = 1;
                            getInitCanvasData();
                        }
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
                        break;
                    //{"type":"logout","client_id":xxx,"time":"xxx"}
//                say(data['from_client_id'], data['from_client_name'], data['from_client_name']+' 退出了', data['time']);
//                        delete client_list[data['from_client_id']];
//                        flush_client_list();

                    /**
                     * 发送base64给 Native 与会者
                     */
                    case 'sync':
                        window.board.syncContent(data['sync_pic']);
                        break;
                    /**
                     *广播主持人取消共享的消息给Native 与会者
                     */
                    case 'cancle_sync':
                        window.board.cancleSync();
                        break;
                    /**
                     * 主持人收到加会者请求 画板数据的请求
                     */
                    case 'getInitCanvasData':
                        console.log("主持人收到数据请求");
                        if (check_in_type == 2) {

                            var datajson = JSON.stringify(lc.getSnapshot());
                            var sync_data = '{"type":"CanvasData","from_client_email":"' + data['to_client_email'] + '","' +
                                'to_client_email":"' + data['from_client_email'] + '",' +
                                '"content":"' + datajson.replace(/\\"/g, '425D8E69BF45B845CB7CF50FA43D64C68D379A46').replace(/"/g, '\\"')
                                    .replace(/\n/g, '\\n').replace(/\r/g, '\\r') + '"}';
                            ws.send(sync_data);
                        }
                        break;
                    //接收到主持人传来的 画板初始数据
                    case 'CanvasData':
                        console.log("加会者收到数据");
                        if (check_in_type == 1) {
                            var newdata = data['content'].replace(/&quot;/g, '"').replace(/425D8E69BF45B845CB7CF50FA43D64C68D379A46/g, '\\"');
                            lc.loadSnapshot(JSON.parse(newdata));
                            getInitShareData();
                        }
                        break;
                    case 'getInitShareData':
                        console.log("主持人收到share数据请求");
                        if (check_in_type == 2) {
                            //调用主持人 native 函数
                            window.board.getSharePic(data['from_client_email']);
                        }
                        break;
                    //接收到主持人传来的 画板初始数据
                    case 'ShareData':
                        console.log("加会者收到share数据");
                        if (check_in_type == 1) {
                            //调用加会者 native 函数
                            window.board.initShareContent(data['content']);
                        }
                        break;

                }
            }
            /**
             *用socket 转发主持人share图片->新加会的那个人
             * 主持人 native来调用
             */
            function syncPicToNewer(to_client_email, base64Str) {


                var sync_data = '{"type":"ShareData","from_client_email":"' + email + '","to_client_email":"' + to_client_email + '","content":"' + base64Str + '"}';
                console.log("syncPicToNewer用socket 转发主持人share图片->新加会的那个人");
//                console.log("js base64长度" + base64Str.length);
                console.log(sync_data);
//                console.log("js message长度" + sync_data.length);


                ws.send(sync_data);

            }
            /**
             *用socket 转发初始share数据请求->主持人
             *js来调用
             */
            function getInitShareData() {
                console.log("getInitShareData用socket 转发初始share数据请求->主持人");
                var login_data = '{"type":"getInitShareData","from_client_email":"' + email + '","to_client_email":"' + host_email + '"}';
                console.log("向主持人请求初始图片数据" + login_data);
                ws.send(login_data);
            }
            /**
             *用socket 转发初始画板数据请求->主持人
             *js 来调用
             */
            function getInitCanvasData() {
                var login_data = '{"type":"getInitCanvasData","from_client_email":"' + email + '","to_client_email":"' + host_email + '"}';
                console.log("向主持人请求初始画板数据" + login_data);
                ws.send(login_data);
            }

            /**
             * 用socket转发 主持人取消共享->与会者
             * 由native 来调用
             */
            function cancleSyncPic() {
                //发送到websocket
                var to_client_id = "all";
                var to_client_name = "所有人";

                var sync_data = '{"type":"cancle_sync","to_client_id":"' + to_client_id + '","to_client_name":"' + to_client_name + '"}';
                ws.send(sync_data);
            }

            /**
             *用 socket转发 主持人同步图片->与会者
             * 由native 来调用
             */
            function syncPic(base64Str) {
                //发送到websocket
                var to_client_id = "all";
                var to_client_name = "所有人";
                console.log("js base64长度" + base64Str.length);
                var sync_data = '{"type":"sync","to_client_id":"' + to_client_id + '","to_client_name":"' + to_client_name + '","sync_pic":"' + base64Str + '"}';
                ws.send(sync_data);
                //回复发送结果
                window.board.syncResultCode("success");
            }

            /**
             * 提交自己改变的数据
             */
            function onSubmit(datajson) {
//                var to_client_id = $("#client_list option:selected").attr("value");
//                var to_client_name = $("#client_list option:selected").text();
                var to_client_id = "all";
                var to_client_name = "所有人";

                var say_data = '{"type":"say","to_client_id":"' + to_client_id + '","to_client_name":"' + to_client_name + '",' +
                    '"content":"' + datajson.replace(/\\"/g, '425D8E69BF45B845CB7CF50FA43D64C68D379A46').replace(/"/g, '\\"')
                        .replace(/\n/g, '\\n').replace(/\r/g, '\\r') + '"}';
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
//                var userlist_window = $("#userlist");
//                var client_list_slelect = $("#client_list");
//                userlist_window.empty();
//                client_list_slelect.empty();
//                userlist_window.append('<h4>在线用户</h4><ul>');
//                client_list_slelect.append('<option value="all" id="cli_all">所有人</option>');
//                for (var p in client_list) {
//                    userlist_window.append('<li id="' + p + '">' + client_list[p] + '</li>');
//                    client_list_slelect.append('<option value="' + p + '">' + client_list[p] + '</option>');
//                }
//                $("#client_list").val(select_client_id);
//                userlist_window.append('</ul>');
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

            /*
             * 初始化画板
             */
            $(document).ready(function () {

                lc = LC.init(document.getElementById("lc"), {
                    imageURLPrefix: 'images',
                    toolbarPosition: 'top',
                    defaultStrokeWidth: 2,
                    strokeWidths: [2, 4, 8, 10, 15, 20],
                    tools: [
                        LC.tools.Pencil,//画笔
                        LC.tools.Eraser,//橡皮
                        LC.tools.Line,//直线
                        LC.tools.Rectangle,//矩形
                        LC.tools.Ellipse,//椭圆
                        LC.tools.Polygon,//多边形
                        LC.tools.Text,//文字
                        LC.tools.Pan,//缩放
                        LC.tools.SelectShape//选择移动

                    ]
                });
                listenDrawingChange();


            });


        </script>


    </head>

    <!--body onload时 建立连接-->
    <body onload="connect();">

    <!--主体-->
    <div class="fs-container">
        <div id="lc"></div>
    </div>
    <!--            <form style="display: none">-->
    <!--                <select style="margin-bottom:8px" id="client_list">-->
    <!--                    <option value="all">所有人</option>-->
    <!--                </select>-->
    <!--                <textarea class="textarea thumbnail" id="textarea"></textarea>-->
    <!--                <div class="say-btn"><input type="submit" class="btn btn-default" value="发表"/></div>-->
    <!--            </form>-->
    <!---->
    <!--            <div class="col-md-3 column" style="visibility: hidden">-->
    <!--                <div class="thumbnail">-->
    <!--                    <div class="caption" id="userlist"></div>-->
    <!--                </div>-->
    <!--            </div>-->
    </body>
    </html>


    <?php

} else {
    printResult(NO_PARAMS_RECEIVE, '服务器未收到参数', -1);
}
?>
