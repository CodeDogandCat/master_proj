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
                $client_family_name = htmlspecialchars($message_data['client_family_name']);
                $client_given_name = htmlspecialchars($message_data['client_given_name']);
                $client_avatar = $message_data['client_avatar'];
                $client_type = $message_data['client_type'];
                $client_is_drawable = $message_data['client_is_drawable'];
                $client_is_talkable = $message_data['client_is_talkable'];

                $_SESSION['room_id'] = $room_id;
                $_SESSION['client_family_name'] = $client_family_name;
                $_SESSION['client_given_name'] = $client_given_name;
                $_SESSION['client_email'] = $client_email;
                $_SESSION['client_avatar'] = $client_avatar;
                $_SESSION['client_type'] = $client_type;
                $_SESSION['client_is_drawable'] = $client_is_drawable;
                $_SESSION['client_is_talkable'] = $client_is_talkable;


                //绑定clientid 和会议室
                Gateway::joinGroup($client_id, $room_id);
                //绑定clientid 和 client_email
                Gateway::bindUid($client_id, $client_email);
                // 转播给当前房间的所有客户端，xx 进会 ,其他人的参与者列表增加一个
                Gateway::sendToGroup($room_id, $message);


                // 获取房间内之前所有用户列表
                $clients_list = Gateway::getClientSessionsByGroup($room_id);
                $members_list = array();

                foreach ($clients_list as $tmp_client_id => $item) {
                    array_push($members_list, $item);
                }
//                echo '所有session1';
//                var_dump($members_list);
//                echo '所有session2';

                // 同步全部参与者列表到  自己
                $member_info = array('type' => 'all_members', 'client_email' => $client_email, 'client_list' => $members_list);
                Gateway::sendToCurrentClient(json_encode($member_info));


                return;

            // 客户端发言 message: {type:say, to_client_id:xx, content:xx}
            case 'say':
                echo "#############say\n";
                // 非法请求
                if (!isset($_SESSION['room_id'])) {
                    throw new \Exception("\$_SESSION['room_id'] not set. client_ip:{$_SERVER['REMOTE_ADDR']}");
                }
                $room_id = $_SESSION['room_id'];
                $client_name = $_SESSION['client_family_name'] . $_SESSION['client_given_name'];

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
                $room_id = $_SESSION['room_id'];
                return Gateway::sendToGroup($room_id, $message);
            //主持人取消分享数据
            case 'cancle_sync':
                echo "cancle_sync主持人取消分享数据\n";
                // 非法请求
                if (!isset($_SESSION['room_id'])) {
                    throw new \Exception("\$_SESSION['room_id'] not set. client_ip:{$_SERVER['REMOTE_ADDR']}");
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
                $room_id = $_SESSION['room_id'];
                return Gateway::sendToGroup($room_id, $message);
                break;
            //主持人修改权限
            case 'alter_permission':
                echo "alterUserPermission用socket 转发主持人修改权限的消息\n";
                // 非法请求
                if (!isset($_SESSION['room_id'])) {
                    throw new \Exception("\$_SESSION['room_id'] not set. client_ip:{$_SERVER['REMOTE_ADDR']}");
                }
                Gateway::sendToUid($message_data['to_client_email'], $message);
                break;
            //主持人 踢人
            case 'kickout':
                echo "kickout用socket 转发主持人踢人的消息\n";
                // 非法请求
                if (!isset($_SESSION['room_id'])) {
                    throw new \Exception("\$_SESSION['room_id'] not set. client_ip:{$_SERVER['REMOTE_ADDR']}");
                }
                Gateway::sendToUid($message_data['to_client_email'], $message);
                break;
            //发消息
            case 'sendmsg':
                echo "sendmsg用socket发消息\n";
                // 非法请求
                if (!isset($_SESSION['room_id'])) {
                    throw new \Exception("\$_SESSION['room_id'] not set. client_ip:{$_SERVER['REMOTE_ADDR']}");
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
            $new_message = array('type' => 'logout', 'from_client_id' => $client_id, 'from_client_name' => $_SESSION['client_family_name'] . $_SESSION['client_given_name'], 'time' => date('Y-m-d H:i:s'));
            Gateway::sendToGroup($room_id, json_encode($new_message));
        }
    }

}
