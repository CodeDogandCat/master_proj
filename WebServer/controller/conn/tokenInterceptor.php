<?php
session_start();
header("Content-type: text/html; charset=utf-8");
try {

    require_once $_SERVER['DOCUMENT_ROOT'] . '/controller/conn/settings.php';
    require_once $_SERVER['DOCUMENT_ROOT'] . '/model/User.php';
    require_once $_SERVER['DOCUMENT_ROOT'] . '/controller/login/Login.php';
    if (isset($_REQUEST[post_token])) {
        //用一个空的user构造一个login对象
        $login = new Login($user);
        if (($login->checkToken($_REQUEST[post_token])) == false) {
            printResult(ACCESS_VOLATION, '未授权访问', -1);
            exit(0);
        } else {
            //再次把token放到session中，这样如果用户频繁使用就不会过期了
            Session::set(SESSION_TOKEN, $_REQUEST[post_token], 2592000);//30天过期
            printResult(SUCCESS, '授权访问', -1);

        }
    } else {
        printResult(ACCESS_VOLATION, '未授权访问', -1);
        exit(0);
    }
} catch (Exception $exception) {
    printResult(FAILURE, $exception->getMessage(), -1);

}



