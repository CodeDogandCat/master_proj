<?php
session_start();
header("Content-type: text/html; charset=utf-8");
//error_reporting(0);
try {

    require_once $_SERVER['DOCUMENT_ROOT'] . '/controller/conn/settings.php';
    require_once 'Register.php';
    require_once $_SERVER['DOCUMENT_ROOT'] . '/util/UrlUtil.php';
    require_once $_SERVER['DOCUMENT_ROOT'] . '/model/User.php';
    require_once $_SERVER['DOCUMENT_ROOT'] . '/controller/conn/Session.php';

//echo UrlUtil::getUrlParam('user_email', '');

//验证 验证码
    if (isset($_REQUEST[post_check_verify_code])) {
        $tmpCode = $_REQUEST[post_check_verify_code];
        $code = -1;
        if (($code = Session::get(SESSION_VERIFY_CODE)) == false) {
            //如果验证码过期
            printResult(VERIFY_CODE_EXPIRE, '验证码已经过期', -1);

        } else {
            //验证码没过期，比较验证码
            if ($code == $tmpCode) {
                printResult(SUCCESS, '验证码正确', -1);

            } else {
                printResult(VERIFY_CODE_NOT_MATCH, '验证码错误', -1);

            }
        }


    } else {
        //发送验证码
        if (isset($_REQUEST[post_user_email])) {

            $user = new User($_REQUEST[post_user_email]);
            $register = new Register($user);


            //如果用户已经存在
            if ($register->checkIfExists()) {
                printResult(USER_EXISTS, '该邮箱已经注册', -1);

            } else {
                //通过邮件发送验证码失败
                $code = -1;
                if (($code = $register->sendVerifyCode()) == false) {
                    printResult(SEND_VERIFY_CODE_FAILED, '发送验证码失败', -1);

                } else {
                    Session::set(SESSION_VERIFY_CODE, $code, 180);//180秒 验证码失效
                    printResult(SUCCESS, '验证码已经发送', $code);
                }

            }
        } else {
            printResult(NO_PARAMS_RECEIVE, '服务器未收到参数', -1);
        }
    }

} catch (Exception $exception) {
    printResult(FAILURE, $exception->getMessage(), -1);

}
