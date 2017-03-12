<?php
session_start();
header("Content-type: text/html; charset=utf-8");
//error_reporting(0);

// GatewayClient 3.0.0版本开始要使用命名空间
use GatewayClient\Gateway;

try {
    require_once $_SERVER['DOCUMENT_ROOT'] . '/controller/conn/Session.php';
    //加载GatewayClient
    require_once $_SERVER['DOCUMENT_ROOT'] . '/vendor/workerman/GatewayClient/Gateway.php';

    // 设置GatewayWorker服务的Register服务ip和端口，请根据实际情况改成实际值
    Gateway::$registerAddress = '127.0.0.1:1236';
    if (isset($_REQUEST['client_id'])) {

        $client_id = $_REQUEST['client_id'];
        // 假设用户已经登录，用户uid和群组id在session中
        if (($user_email = Session::get(SESSION_EMAIL)) != false) {//可能从来都不存在或者过期啦
            if (($group_id = Session::get(SESSION_MEETING_URL)) != false) {//可能从来都不存在或者过期啦{
                // client_id与uid绑定
                Gateway::bindUid($client_id, $user_email);
                // client_id与group_id绑定
                Gateway::joinGroup($client_id, $group_id);

            }


        }
    }
} catch (Exception $exception) {
    printResult(FAILURE, $exception->getMessage(), -1);

}

