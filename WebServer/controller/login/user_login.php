<?php
session_start();
header("Content-type: text/html; charset=utf-8");
//error_reporting(0);


/**
 * 这里只涉及用户首次登录，或注销后登录，所以已经清除了客户端本地token,不需要拦截token
 */
try {
    require_once $_SERVER['DOCUMENT_ROOT'] . '/controller/conn/settings.php';
    require_once 'Login.php';
    require_once $_SERVER['DOCUMENT_ROOT'] . '/util/UrlUtil.php';
    require_once $_SERVER['DOCUMENT_ROOT'] . '/util/EncryptUtil.php';
    require_once $_SERVER['DOCUMENT_ROOT'] . '/model/User.php';
    require_once $_SERVER['DOCUMENT_ROOT'] . '/controller/conn/Session.php';
    $data = array("token" => "", "familyName" => "", "givenName" => "", "avatar" => "");
    if (isset($_REQUEST[post_user_email]) &&
        isset($_REQUEST[post_user_login_password])
    ) {
        $user = new User($_REQUEST[post_user_email]);

        //密码加密
        $user->setPassword(EncryptUtil::hash($_REQUEST[post_user_login_password], $_REQUEST[post_user_email]));

        $login = new Login($user);

        if (($tmp = $login->checkUser()) != false) {
            $data = $tmp;
            //把token放到session中
            Session::set(SESSION_TOKEN, $data['token'], 2592000);//30天过期
            //返回token
            printResult(SUCCESS, '登陆成功', $data);

        } else {
            printResult(LOGIN_ERROR, '用户名或密码错误', $data);
        }

    } else {
        printResult(NO_PARAMS_RECEIVE, '服务器未收到参数', $data);
    }
} catch (Exception $exception) {
    printResult(FAILURE, $exception->getMessage(), $data);

}




