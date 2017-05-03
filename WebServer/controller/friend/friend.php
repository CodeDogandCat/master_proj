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
    "email" => "", "familyName" => "",
    "givenName" => "", "avatar" => ""

);
/**
 * 2.判断参数
 */
if (isset($_REQUEST[post_need_feature]) &&
    isset($_REQUEST[post_user_email]) &&
    isset($_REQUEST[post_to_user_email]) &&
    isset($_REQUEST[post_message_data])
) {

    if ($_REQUEST[post_user_email] == $_REQUEST[post_to_user_email]) {
        printResult(FRIEND_SELF_ERROR, '不能和自己加好友', $data);
    }

    $feature = $_REQUEST[post_need_feature];
    $user1 = new User($_REQUEST[post_user_email]);
    $user2 = new User($_REQUEST[post_to_user_email]);
    $user1_login = new Login($user1);
    $user1_info = $user1_login->checkUserByEmail();
    if ($user1_info == false) {
        printResult(NOT_EXIST_USER_ERROR, '请重新登录尝试', $data);
    }
    $user2_login = new Login($user2);
    $user2_info = $user2_login->checkUserByEmail();
    if ($user2_info == false) {
        printResult(NOT_EXIST_USER_ERROR, '不存在该用户', $data);
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

    // 回复状态：1：未回复 2：拒绝 3：同意 4.好友关系已经删除
    switch ($feature) {

        case 'requestAddFriend':
            /**
             * 请求加好友
             */
            $friendOp = new FriendOp($user1, $user2, $_REQUEST[post_message_data]);
            //1.当前确定 是不是好友好友关系
            if ($friendOp->isFriendNow()) {
                printResult(ALREADY_FRIEND_ERROR, '你和他已经是好友了', $data);
            }

            //2.申请加为好友
            if (($result = $friendOp->requestAddFriend($tag)) != false) {

                $data = array(
                    "email" => $user2->getEmail(), "familyName" => $user2->getFamilyName(),
                    "givenName" => $user2->getGivenName(), "avatar" => $user2->getAvatar()

                );
//                echo '$user1->getAvatar()' . $user1->getAvatar();
//                echo '$user2->getAvatar()' . $user2->getAvatar();

                printResult(SUCCESS, '请求发送成功', $data);

            } else {

                printResult(REQUEST_ADD_FRIEND_ERROR, '请求添加好友失败', $data);
            }
            break;


        case 'acceptFriend':
            /**
             * 请求加好友
             */
            $friendOp = new FriendOp($user1, $user2, $_REQUEST[post_message_data]);

            if (($result = $friendOp->acceptAddFriend($tag)) != false) {

                printResult(SUCCESS, '已经通知对方', $data);

            } else {

                printResult(ACCEPT_FRIEND_ERROR, '同意添加好友失败', $data);
            }
            break;


        case 'rejectFriend':

            /**
             * 请求加好友
             */
            $friendOp = new FriendOp($user1, $user2, $_REQUEST[post_message_data]);

            if (($result = $friendOp->rejectAddFriend($tag)) != false) {

                printResult(SUCCESS, '已经通知对方', $data);

            } else {

                printResult(ACCEPT_FRIEND_ERROR, '拒绝添加好友失败', $data);
            }
            break;


        case 'deleteFriend':
            /**
             * 删除好友
             */
            $friendOp = new FriendOp($user1, $user2, $_REQUEST[post_message_data]);
            //1.当前确定 是不是好友好友关系
            if ($friendOp->isFriendNow()) {
                if ($friendOp->requestDelFriend()) {
                    printResult(SUCCESS, '删除联系人成功', $data);
                } else {
                    printResult(REQUEST_DEL_FRIEND_ERROR, '请求删除联系人失败', $data);
                }

            } else {
                printResult(SUCCESS, '移除该联系人成功', $data);
            }

            break;

    }
} else {
    printResult(NO_PARAMS_RECEIVE, '服务器未收到参数', $data);
}