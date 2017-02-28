<?php
session_start();
header("Content-type: text/html; charset=utf-8");
//error_reporting(0);

require_once $_SERVER['DOCUMENT_ROOT'] . '/controller/conn/settings.php';
require_once 'Register.php';
require_once $_SERVER['DOCUMENT_ROOT'] . '/util/UrlUtil.php';
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
    $user->setPassword($_REQUEST[post_user_login_password]);
    //获取当前时间
    $dt = new DateTime();
    $user->setRegisterTime($dt->format('Y-m-d H:i:s'));
    $register = new Register($user);

    if ($register->saveUser()) {
        printResult(SUCCESS, '注册成功', -1);

    } else {
        printResult(SAVE_USER_ERROR, '注册失败', -1);
    }

} else {
    printResult(NO_PARAMS_RECEIVE, '服务器未收到参数', -1);
}


