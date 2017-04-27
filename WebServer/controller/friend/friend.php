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
/**
 * 2.判断参数
 */
if (isset($_REQUEST[post_need_feature]) &&
    isset($_REQUEST[post_user_email]) &&
    isset($_REQUEST[post_to_user_email]) &&
    isset($_REQUEST[post_message_data])
) {

    if ($_REQUEST[post_user_email] == $_REQUEST[post_to_user_email]) {
        printResult(FRIEND_SELF_ERROR, '不能和自己加好友', -1);
    }

    $feature = $_REQUEST[post_need_feature];
    $user1 = new User($_REQUEST[post_user_email]);
    $user2 = new User($_REQUEST[post_to_user_email]);
    $user1_login = new Login($user1);
    $user1_info = $user1_login->checkUserByEmail();
    if ($user1_info == false) {
        printResult(NOT_EXIST_USER_ERROR, '请重新登录尝试', -1);
    }
    $user2_login = new Login($user2);
    $user2_info = $user2_login->checkUserByEmail();
    if ($user2_info == false) {
        printResult(NOT_EXIST_USER_ERROR, '不存在该用户', -1);
    }

    $tag = $_REQUEST[post_message_data];

    $user1->setId($user1_info['id']);
    $user1->setFamilyName($user1_info['familyName']);
    $user1->setGivenName($user1_info['givenName']);
    $user1->setAvatar($user1_info['avatar']);
    $user2->setId($user2_info['id']);
    $user2->setFamilyName($user2_info['familyName']);
    $user2->setGivenName($user2_info['givenName']);
    $user2->setAvatar($user2_info['avatar']);


    switch ($feature) {

        case 'requestAddFriend':
            /**
             * 请求加好友
             */
            $friendOp = new FriendOp($user1, $user2, $_REQUEST[post_message_data]);
            //1.当前确定 是不是好友好友关系
            if ($friendOp->isFriendNow()) {
                printResult(ALREADY_FRIEND_ERROR, '你和他已经是好友了', -1);
            }

            //2.申请加为好友
            if (($result = $friendOp->requestAddFriend($tag)) != false) {

                $data = array(
                    "email" => $user2->getEmail(), "familyName" => $user2->getFamilyName(),
                    "givenName" => $user2->getGivenName(), "avatar" => $user2->getAvatar()

                );

                printResult(SUCCESS, '请求发送成功', $data);

            } else {
                $data = array(
                    "email" => "", "familyName" => "",
                    "givenName" => "", "avatar" => ""

                );

                printResult(REQUEST_ADD_FRIEND_ERROR, '请求添加好友失败', $data);
            }
            break;


        case 'acceptFriend':
            break;

        case 'rejectFriend':

            break;


        case 'deleteFriend':
            /**
             * 删除好友
             */
            $friendOp = new FriendOp($user1, $user2, $_REQUEST[post_message_data]);
            //1.当前确定 是不是好友好友关系
            if ($friendOp->isFriendNow()) {
                if ($friendOp->requestDelFriend()) {
                    printResult(SUCCESS, '删除联系人成功', -1);
                } else {
                    printResult(REQUEST_DEL_FRIEND_ERROR, '请求删除联系人失败', -1);
                }

            } else {
                printResult(SUCCESS, '移除该联系人成功', -1);
            }

            break;


    }
} else {
    printResult(NO_PARAMS_RECEIVE, '服务器未收到参数', -1);
}