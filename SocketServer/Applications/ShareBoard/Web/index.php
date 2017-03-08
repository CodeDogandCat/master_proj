<?php
require_once 'util/UrlUtil.php';
use \Workerman\Worker;
use \Workerman\WebServer;
use \GatewayWorker\Lib\Gateway;
?>
<html><head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
  <title>canvas</title>
<!--    add -->
   <link href="/_assets/literallycanvas.css" rel="stylesheet">
   <meta charset="UTF-8">
   <meta content="width=device-width,initial-scale=1.0,maximum-scale=1.0,user-scalable=no" name="viewport">

  <style type="text/css">
    .fs-container {
        width: auto;
        margin: auto;
        max-width: 100%;
    }

   </style>
<!--    add end-->
  <link href="/css/bootstrap.min.css" rel="stylesheet">
  <link href="/css/style.css" rel="stylesheet">
	
  <script type="text/javascript" src="/js/swfobject.js"></script>
  <script type="text/javascript" src="/js/web_socket.js"></script>
  <script type="text/javascript" src="/js/jquery.min.js"></script>
    <!--    add-->
    <!-- you really ought to include react-dom, but for react 0.14 you don't strictly have to. -->
    <script src="/_js_libs/jquery-1.8.2.js"></script>
    <script src="/_js_libs/react-0.14.3.js"></script>
    <script src="/_js_libs/literallycanvas.js"></script>


    <!--    add end-->
  <script type="text/javascript">
   // if (typeof console == "undefined") {    this.console = { log: function (msg) {  } };}
    // 如果浏览器不支持websocket，会使用这个flash自动模拟websocket协议，此过程对开发者透明
    //WEB_SOCKET_SWF_LOCATION = "/swf/WebSocketMain.swf";
    // 开启flash的websocket debug
   // WEB_SOCKET_DEBUG = true;
    var ws, name, client_list={};
    var lc;
    var localStorageKey = 'drawing';
    var select_client_id = 'all';
    var myid='';
    var unsubscribeofchange;

    // 连接服务端
    function connect() {
       // 创建websocket
       ws = new WebSocket("ws://"+document.domain+":7272");
	   //console.log("ws://"+document.domain+":7272");
       // 当socket连接打开时，输入用户名
       ws.onopen = onopen;
       // 当有消息时根据消息类型显示不同信息
       ws.onmessage = onmessage; 
       ws.onclose = function() {
    	  console.log("连接关闭，定时重连");
          //connect();
       };
       ws.onerror = function() {
     	  console.log("出现错误");
       };
    }

    // 连接建立时发送登录信息
    function onopen()
    {
//        name=<?php //UrlUtil::getUrlParam('user_email','') ?>//;
        name='<?php echo isset($_GET['user_email']) ? $_GET['user_email'] : '2662083658@qq.com'?>';
//        if(!name)
//        {
//            show_prompt();
//        }
        // 登录
        var login_data = '{"type":"login","client_name":"'+name.replace(/"/g, '\\"')+'","room_id":"<?php echo isset($_GET['room_id']) ? $_GET['room_id'] : 1?>"}';
        console.log("websocket握手成功，发送登录数据:"+login_data);
//        console.log("url:"+<?php //UrlUtil::request_url()?>//);
        console.log("url:"+<?php $_SERVER['GATEWAY_ADDR']?>);
        ws.send(login_data);
    }

    // 服务端发来消息时
    function onmessage(e)
    {
//        console.log(e.data);
        var data = eval("("+e.data+")");
        switch(data['type']){
            // 服务端ping客户端
            case 'ping':
                ws.send('{"type":"pong"}');
                break;
            case 'getID':
                if(data['client_id'])
                {
                    myid = data['client_id'];
                }
				console.log("收到的自己的id"+myid);
				break;
            // 登录 更新用户列表
            case 'login':
                //{"type":"login","client_id":xxx,"client_name":"xxx","client_list":"[...]","time":"xxx"}
//                say(data['client_id'], data['client_name'],  data['client_name']+' 加入了聊天室', data['time']);
                if(data['client_list'])
                {
                    client_list = data['client_list'];
                }
                else
                {
                    client_list[data['client_id']] = data['client_name']; 
                }
                flush_client_list();
                console.log(data['client_name']+"登录成功");
                break;
            // 发言
            case 'say':
                //{"type":"say","from_client_id":xxx,"to_client_id":"all/client_id","content":"xxx","time":"xxx"}
                say(data['from_client_id'], data['from_client_name'], data['content'], data['time']);
                break;
            // 用户退出 更新用户列表
            case 'logout':
                //{"type":"logout","client_id":xxx,"time":"xxx"}
//                say(data['from_client_id'], data['from_client_name'], data['from_client_name']+' 退出了', data['time']);
                delete client_list[data['from_client_id']];
                flush_client_list();
        }
    }

    // 输入姓名
    function show_prompt(){  
        name = prompt('输入你的名字：', '');
        if(!name || name=='null'){  
            name = '游客';
        }
    }  

    // 提交对话
    function onSubmit(datajson) {
//      var input = document.getElementById("textarea");
      var to_client_id = $("#client_list option:selected").attr("value");
      var to_client_name = $("#client_list option:selected").text();
      var say_data ='{"type":"say","to_client_id":"'+to_client_id+'","to_client_name":"'+to_client_name+'","content":"'+datajson.replace(/\\"/g, '425D8E69BF45B845CB7CF50FA43D64C68D379A46').replace(/"/g, '\\"').replace(/\n/g,'\\n').replace(/\r/g, '\\r')+'"}';
      console.log("提交"+say_data);
      ws.send(say_data);

//      input.value = "";
//      input.focus();
    }

    // 刷新用户列表框
    function flush_client_list(){
    	var userlist_window = $("#userlist");
    	var client_list_slelect = $("#client_list");
    	userlist_window.empty();
    	client_list_slelect.empty();
    	userlist_window.append('<h4>在线用户</h4><ul>');
    	client_list_slelect.append('<option value="all" id="cli_all">所有人</option>');
    	for(var p in client_list){
            userlist_window.append('<li id="'+p+'">'+client_list[p]+'</li>');
            client_list_slelect.append('<option value="'+p+'">'+client_list[p]+'</option>');
        }
    	$("#client_list").val(select_client_id);
    	userlist_window.append('</ul>');
    }

    // 发言
    function say(from_client_id, from_client_name, content, time){
        var newdata=content.replace(/&quot;/g, '"').replace(/425D8E69BF45B845CB7CF50FA43D64C68D379A46/g, '\\"');
        console.log("收到"+newdata);
        unsubscribeofchange();
        //如果消息不是自己发出去的
        if(myid!=''&&myid!=from_client_id){

            console.log('加载...');
            lc.loadSnapshot(JSON.parse(newdata));
            console.log("加载完成");
        }
        listenDrawingChange();
//        localStorage.setItem(localStorageKey, content);
//    	$("#dialog").append('<div class="speech_item"><img src="http://lorempixel.com/38/38/?'+from_client_id+'" class="user_icon" /> '+from_client_name+' <br> '+time+'<div style="clear:both;"></div><p class="triangle-isosceles top">'+content+'</p> </div>');
    }
    function listenDrawingChange() {
        unsubscribeofchange=lc.on('drawingChange', function() {
//            localStorage.setItem(localStorageKey, JSON.stringify(lc.getSnapshot()));
            var content=JSON.stringify(lc.getSnapshot());
//            console.log("change"+content);
            onSubmit(content);
        });
    }
    $(document).ready(function() {
//        var watermarkImage = new Image();
//        watermarkImage.src = '/_static/watermark.jpg';

        lc = LC.init(document.getElementById("lc"), {
            imageURLPrefix: '/_assets/lc-images',
            toolbarPosition: 'top',
            defaultStrokeWidth: 2,
            strokeWidths: [1, 2, 4, 8, 15]
//            watermarkImage: watermarkImage,
//            watermarkScale: 0.5  // you can scale it
        });
        listenDrawingChange();






    });

  </script>


</head>
<body onload="connect();">
    <div class="container">
	    <div class="row clearfix">
<!--	        <div class="col-md-1 column">-->
<!--	        </div>-->
            <div class="fs-container col-md-12 column">
                <div id="lc"></div>
            </div>
<!--	        <div class="col-md-12 column">-->
<!--	           <div class="thumbnail">-->
<!--	               <div class="caption" id="dialog"></div>-->
<!--	           </div>-->
	           <form  style="display: none">
	                <select style="margin-bottom:8px" id="client_list">
                        <option value="all">所有人</option>
                    </select>
                    <textarea class="textarea thumbnail" id="textarea"></textarea>
                    <div class="say-btn"><input type="submit" class="btn btn-default" value="发表" /></div>
               </form>

<!--	        </div>-->
	        <div class="col-md-3 column">
                <div class="thumbnail">
                    <div class="caption" id="userlist"></div>
                </div>
            </div>
	    </div>
    </div>
  <!--   <script type="text/javascript">var _bdhmProtocol = (("https:" == document.location.protocol) ? " https://" : " http://");document.write(unescape("%3Cscript src='" + _bdhmProtocol + "hm.baidu.com/h.js%3F7b1919221e89d2aa5711e4deb935debd' type='text/javascript'%3E%3C/script%3E"));</script>-->
</body>
</html>
