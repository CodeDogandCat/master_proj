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

    //如果是发送白板内容
    if (isset($_REQUEST[post_board_data])) {

        $data = $_REQUEST[post_board_data];


        if (isset($_REQUEST[post_to_meeting_url])) {

            $group = $_REQUEST[post_to_meeting_url];
            // 向任意群组的网站页面发送数据
            Gateway::sendToGroup($group, $data);

        } elseif (isset($_REQUEST[post_to_user_email])) {

            $uid = $_REQUEST[post_to_user_email];
            // 向任意uid的网站页面发送数据
            Gateway::sendToUid($uid, $data);

        }
    }


} catch (Exception $exception) {
    printResult(FAILURE, $exception->getMessage(), -1);

}
