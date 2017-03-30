<?php

use \GatewayWorker\Lib\Gateway;

class Events
{
    /**
     * 当有客户端连接时，将client_id返回，让mvc框架判断当前uid并执行绑定
     * @param $client_id
     */
    public static function onConnect($client_id)
    {
        Gateway::sendToClient($client_id, json_encode(array(
            'type' => 'init',
            'client_id' => $client_id
        )));
    }

    /**
     * 有消息时
     * @param int $client_id
     * @param mixed $message
     * @throws Exception
     */
    public static function onMessage($client_id, $message)
    {
        // debug
//        echo "client:{$_SERVER['REMOTE_ADDR']}:{$_SERVER['REMOTE_PORT']} gateway:{$_SERVER['GATEWAY_ADDR']}:{$_SERVER['GATEWAY_PORT']}  client_id:$client_id session:" . json_encode($_SESSION) . " onMessage:" . $message . "\n";

        // 客户端传递的是json数据
        $message_data = json_decode($message, true);
        if (!$message_data) {
            echo "格式不合法\nmessage 长度" . strlen($message);

            return;
        }

        // 根据类型执行不同的业务
        switch ($message_data['type']) {
            // 客户端回应服务端的心跳
            case 'pong':
                return;
            // 客户端登录 message格式: {type:login, name:xx, room_id:1} ，添加到客户端，广播给所有客户端xx进入聊天室
            case 'login':
                echo "#############login" . $message . "\n";
                // 判断是否有房间号
                if (!isset($message_data['room_id'])) {
                    throw new \Exception("\$message_data['room_id'] not set. client_ip:{$_SERVER['REMOTE_ADDR']} \$message:$message");
                }

                // 把房间号昵称放到session中
                $room_id = $message_data['room_id'];
                $client_email = $message_data['client_email'];
                $client_name = htmlspecialchars($message_data['client_name']);
                $_SESSION['room_id'] = $room_id;
                $_SESSION['client_name'] = $client_name;
                $_SESSION['client_email'] = $client_email;
                //绑定clientid 和 client_email
                Gateway::bindUid($client_id, $client_email);

                // 获取房间内所有用户列表
                $clients_list = Gateway::getClientSessionsByGroup($room_id);
                foreach ($clients_list as $tmp_client_id => $item) {
                    $clients_list[$tmp_client_id] = $item['client_name'];
                }
                $clients_list[$client_id] = $client_name;

                // 转播给当前房间的所有客户端，xx进入聊天室 message {type:login, client_id:xx, name:xx}
                $new_message2 = array('type' => 'login', 'client_id' => $client_id, 'client_name' => htmlspecialchars($client_name), 'time' => date('Y-m-d H:i:s'));
                Gateway::sendToGroup($room_id, json_encode($new_message2));
                Gateway::joinGroup($client_id, $room_id);

                // 给当前用户发送用户列表
                $new_message2['client_list'] = $clients_list;
                Gateway::sendToCurrentClient(json_encode($new_message2));

                return;

            // 客户端发言 message: {type:say, to_client_id:xx, content:xx}
            case 'say':
                echo "#############say\n";
                // 非法请求
                if (!isset($_SESSION['room_id'])) {
                    throw new \Exception("\$_SESSION['room_id'] not set. client_ip:{$_SERVER['REMOTE_ADDR']}");
                }
                // 非法请求
                if (!isset($_SESSION['client_name'])) {
                    throw new \Exception("\$_SESSION['client_name'] not set. client_ip:{$_SERVER['REMOTE_ADDR']}");
                }
                $room_id = $_SESSION['room_id'];
                $client_name = $_SESSION['client_name'];

//                // 私聊
//                if($message_data['to_client_id'] != 'all')
//                {
//                    $new_message = array(
//                        'type'=>'say',
//                        'from_client_id'=>$client_id,
//                        'from_client_name' =>$client_name,
//                        'to_client_id'=>$message_data['to_client_id'],
//                        'content'=>"<b>对你说: </b>".nl2br(htmlspecialchars($message_data['content'])),
//                        'time'=>date('Y-m-d H:i:s'),
//                    );
//                    Gateway::sendToClient($message_data['to_client_id'], json_encode($new_message));
//                    $new_message['content'] = "<b>你对".htmlspecialchars($message_data['to_client_name'])."说: </b>".nl2br(htmlspecialchars($message_data['content']));
//                    return Gateway::sendToCurrentClient(json_encode($new_message));
//                }
//                $result=preg_replace('/\\\\\"/i',"@",htmlspecialchars($message_data['content']));
                $new_message = array(
                    'type' => 'say',
                    'from_client_id' => $client_id,
                    'from_client_name' => $client_name,
                    'to_client_id' => 'all',
//                    'content'=>nl2br($result),
                    'content' => nl2br(htmlspecialchars($message_data['content'])),
                    'time' => date('Y-m-d H:i:s')
                );
                return Gateway::sendToGroup($room_id, json_encode($new_message));
            //同步主持人分享数据
            case 'sync':
                echo "sync同步主持人分享数据\n长度" . strlen($message);
                // 非法请求
                if (!isset($_SESSION['room_id'])) {
                    throw new \Exception("\$_SESSION['room_id'] not set. client_ip:{$_SERVER['REMOTE_ADDR']}");
                }
                // 非法请求
                if (!isset($_SESSION['client_name'])) {
                    throw new \Exception("\$_SESSION['client_name'] not set. client_ip:{$_SERVER['REMOTE_ADDR']}");
                }
                $room_id = $_SESSION['room_id'];
                return Gateway::sendToGroup($room_id, $message);
            //主持人取消分享数据
            case 'cancle_sync':
                echo "cancle_sync主持人取消分享数据\n";
                // 非法请求
                if (!isset($_SESSION['room_id'])) {
                    throw new \Exception("\$_SESSION['room_id'] not set. client_ip:{$_SERVER['REMOTE_ADDR']}");
                }
                // 非法请求
                if (!isset($_SESSION['client_name'])) {
                    throw new \Exception("\$_SESSION['client_name'] not set. client_ip:{$_SERVER['REMOTE_ADDR']}");
                }
                $room_id = $_SESSION['room_id'];
                return Gateway::sendToGroup($room_id, $message);
            //加会者初次请求主持人的初始化白板数据->发给主持人
            case 'getInitCanvasData':
                echo "getInitCanvasData加会者初次请求主持人的初始化白板数据->发给主持人\n";
                // 非法请求
                if (!isset($_SESSION['room_id'])) {
                    throw new \Exception("\$_SESSION['room_id'] not set. client_ip:{$_SERVER['REMOTE_ADDR']}");
                }
                // 非法请求
                if (!isset($_SESSION['client_name'])) {
                    throw new \Exception("\$_SESSION['client_name'] not set. client_ip:{$_SERVER['REMOTE_ADDR']}");
                }
                Gateway::sendToUid($message_data['to_client_email'], $message);
                echo "ccccccccccccccccccccccccccccccc1";
                break;
            //转发画板数据给 新加入的与会者->发给加会者
            case 'CanvasData':
                echo "CanvasData转发画板数据给 新加入的与会者->发给加会者\n";
                // 非法请求
                if (!isset($_SESSION['room_id'])) {
                    throw new \Exception("\$_SESSION['room_id'] not set. client_ip:{$_SERVER['REMOTE_ADDR']}");
                }
                // 非法请求
                if (!isset($_SESSION['client_name'])) {
                    throw new \Exception("\$_SESSION['client_name'] not set. client_ip:{$_SERVER['REMOTE_ADDR']}");
                }
                Gateway::sendToUid($message_data['to_client_email'], $message);
                echo "ccccccccccccccccccccccccccccccc2";
                break;
            //加会者初次请求主持人的初始化 共享图片数据->发给主持人
            case 'getInitShareData':
                echo "getInitShareData加会者初次请求主持人的初始化 共享图片数据->发给主持人\n";
                // 非法请求
                if (!isset($_SESSION['room_id'])) {
                    throw new \Exception("\$_SESSION['room_id'] not set. client_ip:{$_SERVER['REMOTE_ADDR']}");
                }
                // 非法请求
                if (!isset($_SESSION['client_name'])) {
                    throw new \Exception("\$_SESSION['client_name'] not set. client_ip:{$_SERVER['REMOTE_ADDR']}");
                }
                Gateway::sendToUid($message_data['to_client_email'], $message);
                echo "@@@@@@@@@@@@@@@@@@@@@1" . $message_data['to_client_email'];
                break;
            //加会者初次请求主持人的初始化 共享图片数据->发给加会者
            case 'ShareData':
                echo "ShareData加会者初次请求主持人的初始化 共享图片数据->发给加会者\n";
                // 非法请求
                if (!isset($_SESSION['room_id'])) {
                    throw new \Exception("\$_SESSION['room_id'] not set. client_ip:{$_SERVER['REMOTE_ADDR']}");
                }
                // 非法请求
                if (!isset($_SESSION['client_name'])) {
                    throw new \Exception("\$_SESSION['client_name'] not set. client_ip:{$_SERVER['REMOTE_ADDR']}");
                }
                Gateway::sendToUid($message_data['to_client_email'], $message);
                echo "@@@@@@@@@@@@@@@@@@@@@2" . $message_data['to_client_email'];
                break;
            //主持人离会->发给加会者
            case 'hostLeaveMeeting':
                echo "hostLeaveMeeting主持人离会->发给加会者\n";
                // 非法请求
                if (!isset($_SESSION['room_id'])) {
                    throw new \Exception("\$_SESSION['room_id'] not set. client_ip:{$_SERVER['REMOTE_ADDR']}");
                }
                // 非法请求
                if (!isset($_SESSION['client_name'])) {
                    throw new \Exception("\$_SESSION['client_name'] not set. client_ip:{$_SERVER['REMOTE_ADDR']}");
                }
                $room_id = $_SESSION['room_id'];
                return Gateway::sendToGroup($room_id, $message);
                break;
            //加会者离会->发给其他人
            case 'leaveMeeting':
                echo "leaveMeeting加会者离会->发给其他人\n";
                // 非法请求
                if (!isset($_SESSION['room_id'])) {
                    throw new \Exception("\$_SESSION['room_id'] not set. client_ip:{$_SERVER['REMOTE_ADDR']}");
                }
                // 非法请求
                if (!isset($_SESSION['client_name'])) {
                    throw new \Exception("\$_SESSION['client_name'] not set. client_ip:{$_SERVER['REMOTE_ADDR']}");
                }
                $room_id = $_SESSION['room_id'];
                return Gateway::sendToGroup($room_id, $message);
                break;


        }
    }

    /**
     * 当客户端断开连接时
     * @param integer $client_id 客户端id
     */
    public static function onClose($client_id)
    {
        // debug
//        echo "client:{$_SERVER['REMOTE_ADDR']}:{$_SERVER['REMOTE_PORT']} gateway:{$_SERVER['GATEWAY_ADDR']}:{$_SERVER['GATEWAY_PORT']}  client_id:$client_id onClose:''\n";

        // 从房间的客户端列表中删除
        if (isset($_SESSION['room_id'])) {
            $room_id = $_SESSION['room_id'];
            $new_message = array('type' => 'logout', 'from_client_id' => $client_id, 'from_client_name' => $_SESSION['client_name'], 'time' => date('Y-m-d H:i:s'));
            Gateway::sendToGroup($room_id, json_encode($new_message));
        }
    }

}
