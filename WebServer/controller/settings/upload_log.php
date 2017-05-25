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
        if ($_REQUEST[post_need_feature] == log) {

            if (isset($_REQUEST[post_user_email]) &&
                isset($_FILES[post_log_file])
            ) {

                $dt = new DateTime();
                $create_folder_path = $_SERVER['DOCUMENT_ROOT'] . '/assets/log';
                createFolder($create_folder_path);
                $user = new User($_REQUEST[post_user_email]);
                $target_path = $create_folder_path . "/" . $user->getEmail() . '.' . $dt->format('Y_m_d_H_i_s') . '.' . FileUtil::get_extension(basename($_FILES [post_log_file] ['name']));

                if (move_uploaded_file($_FILES [post_log_file] ['tmp_name'], $target_path) == false) {

                    printResult(UPLOAD_LOG_ERROR, '上传错误报告失败', $data);
                } else {

                    $update = new Update($user);
                    if ($update->sendlog($target_path)) {
                        printResult(SUCCESS, '错误报告发送给管理员成功', -1);
                    } else {
                        printResult(UPLOAD_LOG_ERROR, '错误报告发送给管理员失败', -1);
                    }

                }
            } else {
                printResult(NO_PARAMS_RECEIVE, '服务器未收到参数', -1);
            }
        }


    } else {
        printResult(NO_PARAMS_RECEIVE, '服务器未收到参数', -1);
    }
} catch
(Exception $exception) {
    printResult(FAILURE, $exception->getMessage(), -1);

}

