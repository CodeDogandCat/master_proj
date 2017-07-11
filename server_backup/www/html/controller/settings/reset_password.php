<?php
session_start();
header("Content-type: text/html; charset=utf-8");
//error_reporting(0);


try {
    require_once $_SERVER['DOCUMENT_ROOT'] . '/controller/conn/settings.php';
    require_once $_SERVER['DOCUMENT_ROOT'] . '/util/UrlUtil.php';
    require_once $_SERVER['DOCUMENT_ROOT'] . '/util/FileUtil.php';
    require_once $_SERVER['DOCUMENT_ROOT'] . '/util/EncryptUtil.php';
    require_once $_SERVER['DOCUMENT_ROOT'] . '/model/User.php';
    require_once $_SERVER['DOCUMENT_ROOT'] . '/controller/conn/Session.php';
    require_once $_SERVER['DOCUMENT_ROOT'] . '/controller/settings/Update.php';

    $data = array("token" => "", "avatar" => "");
    if (isset($_REQUEST[post_need_feature])) {
        if ($_REQUEST[post_need_feature] == reset_password) {
            //重置密码
            if (isset($_REQUEST[post_user_email]) &&
                isset($_REQUEST[post_user_login_password_new])
            ) {
                $user = new User($_REQUEST[post_user_email]);
                $newPwd = $_REQUEST[post_user_login_password_new];
                $update = new Update($user);
                if ($update->resetPassword($newPwd)) {
                    printResult(SUCCESS, '重置密码成功,请重新登录', $data);
                } else {
                    printResult(UPDATE_PASSWORD_ERROR, '重置密码失败', $data);
                }
            } else {
                printResult(NO_PARAMS_RECEIVE, '服务器未收到参数', $data);
            }
        }


    } else {
        printResult(NO_PARAMS_RECEIVE, '服务器未收到参数', $data);
    }
} catch (Exception $exception) {
    printResult(FAILURE, $exception->getMessage(), $data);

}

