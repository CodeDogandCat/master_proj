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

    $data = array(

        "appname" => "",
        "serverVersion" => "",
        "serverFlag" => -1,
        "lastForce" => -1,
        "updateurl" => "",
        "upgradeinfo" => ""

    );
    if (isset($_REQUEST[post_user_email])
    ) {
        $user = new User($_REQUEST[post_user_email]);

        $update = new Update($user);

        if (($result = $update->getUpgrade()) != false) {

            $data = $result;
            printResult(SUCCESS, '获取更新信息成功', $data);
        } else {
            printResult(UPGRADE_ERROR, '获取更新信息失败', $data);
        }

    } else {
        printResult(NO_PARAMS_RECEIVE, '服务器未收到参数', $data);
    }


} catch (Exception $exception) {
    printResult(FAILURE, $exception->getMessage(), $data);

}

