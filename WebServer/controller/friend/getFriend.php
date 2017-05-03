<?php
session_start();
header("Content-type: text/html; charset=utf-8");

require_once $_SERVER['DOCUMENT_ROOT'] . '/controller/conn/settings.php';
require_once $_SERVER['DOCUMENT_ROOT'] . '/model/User.php';
require_once $_SERVER['DOCUMENT_ROOT'] . '/controller/conn/Session.php';
require_once $_SERVER['DOCUMENT_ROOT'] . '/controller/friend/FriendOp.php';
require_once $_SERVER['DOCUMENT_ROOT'] . '/controller/login/Login.php';
/**
 * 1.拦截token
 */
require_once $_SERVER['DOCUMENT_ROOT'] . '/controller/conn/tokenInterceptor.php';
$data = array(
    array(
        "email" => "", "familyName" => "",
        "givenName" => "", "avatar" => ""

    )
);
/**
 * 2.判断参数
 */
if (
isset($_REQUEST[post_user_email])
) {

    $user1 = new User($_REQUEST[post_user_email]);
    $user1_login = new Login($user1);
    $user1_info = $user1_login->checkUserByEmail();
    if ($user1_info == false) {
        printResult(NOT_EXIST_USER_ERROR, '请重新登录尝试', $data);
    }


    $user1->setId($user1_info['id']);
    $user1->setFamilyName($user1_info['familyName']);
    $user1->setGivenName($user1_info['givenName']);
    $user1->setAvatar($user1_info['avatar']);

    /**
     * 获取好友列表
     */
    $friendOp = new FriendOp($user1, null, $_REQUEST[post_message_data]);
    if (($result = $friendOp->getAllFriend()) != false) {
        printResult(SUCCESS, '获取联系人列表', $result);
    } else {
        printResult(GET_ALL_FRIEND_ERROR, '获取联系人列表失败', $data);
    }

} else {
    printResult(NO_PARAMS_RECEIVE, '服务器未收到参数', $data);
}