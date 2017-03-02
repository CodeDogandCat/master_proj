<?php
session_start();
header("Content-type: text/html; charset=utf-8");
//error_reporting(0);


try {
    require_once $_SERVER['DOCUMENT_ROOT'] . '/controller/conn/settings.php';
    require_once 'Register.php';
    require_once $_SERVER['DOCUMENT_ROOT'] . '/util/UrlUtil.php';
    require_once $_SERVER['DOCUMENT_ROOT'] . '/util/EncryptUtil.php';
    require_once $_SERVER['DOCUMENT_ROOT'] . '/model/User.php';
    require_once $_SERVER['DOCUMENT_ROOT'] . '/controller/conn/Session.php';

    if (isset($_REQUEST[post_user_email]) &&
        isset($_REQUEST[post_user_family_name]) &&
        isset($_REQUEST[post_user_given_name]) &&
        isset($_REQUEST[post_user_login_password])
    ) {
        $user = new User($_REQUEST[post_user_email]);
        $user->setFamilyName($_REQUEST[post_user_family_name]);
        $user->setGivenName($_REQUEST[post_user_given_name]);
        //获取当前时间
        $dt = new DateTime();
        $user->setRegisterTime($dt->format('Y-m-d H:i:s'));
        $user->setLoginRecentTime($dt->format('Y-m-d H:i:s'));
        //密码加密
        $user->setPassword(EncryptUtil::hash($_REQUEST[post_user_login_password], $_REQUEST[post_user_email]));
        //token构造
        $user->setToken(EncryptUtil::hash($user->getEmail() . $user->getPassword() . $user->getRegisterTime(), $user->getRegisterTime()));
        $register = new Register($user);

        if ($register->saveUser()) {
            //把token 放到session中
            Session::set(SESSION_TOKEN, $user->getToken(), 2592000);//30天过期
            //返回token
            printResult(SUCCESS, $user->getToken(), -1);

        } else {
            printResult(SAVE_USER_ERROR, '注册失败', -1);
        }

    } else {
        printResult(NO_PARAMS_RECEIVE, '服务器未收到参数', -1);
    }
} catch (Exception $exception) {
    printResult(FAILURE, $exception->getMessage(), -1);

}

