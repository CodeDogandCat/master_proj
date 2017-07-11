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
    //token 拦截 检验
    require_once $_SERVER['DOCUMENT_ROOT'] . '/controller/conn/tokenInterceptor.php';

    if (isset($_REQUEST[post_need_feature])) {
        if ($_REQUEST[post_need_feature] == feedback) {

            if (isset($_REQUEST[post_user_email]) &&
                isset($_REQUEST[post_message_data])
            ) {
                $user = new User($_REQUEST[post_user_email]);
                $content = $_REQUEST[post_message_data];

                $update = new Update($user);
                if ($update->feedback($content)) {
                    printResult(SUCCESS, '发送反馈成功', -1);
                } else {
                    printResult(FEEDBACK_ERROR, '发送反馈失败', -1);
                }

            } else {
                printResult(NO_PARAMS_RECEIVE, '服务器未收到参数', -1);
            }
        }


    } else {
        printResult(NO_PARAMS_RECEIVE, '服务器未收到参数', -1);
    }
} catch (Exception $exception) {
    printResult(FAILURE, $exception->getMessage(), -1);

}

