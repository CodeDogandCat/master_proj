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

    /**
     * 1.拦截token
     */
    require_once $_SERVER['DOCUMENT_ROOT'] . '/controller/conn/tokenInterceptor.php';

    if (isset($_REQUEST[post_user_email]) &&
        isset($_REQUEST[post_meeting_url]) &&
        isset($_REQUEST[post_need_feature]) &&
        isset($_FILES[post_chat_data])
    ) {
        $user = new User($_REQUEST[post_user_email]);
        //获取当前时间
        $dt = new DateTime();

        //命名格式 会议号_email_time.xxx
        $FileName = '';
        $create_folder_path = $_SERVER['DOCUMENT_ROOT'] . '/assets/chat/' . $_REQUEST[post_need_feature];
        createFolder($create_folder_path);
        $ext = "";
        if ($_REQUEST[post_need_feature] == 'image') {
            $ext = 'jpeg';
        } else if ($_REQUEST[post_need_feature] == 'voice') {
            $ext = 'amr';
        } else {
            $ext = FileUtil::get_extension(basename($_FILES [post_chat_data] ['name']));
        }


        $target_path = $create_folder_path . "/" . $_REQUEST[post_meeting_url] . '_' . $_REQUEST[post_user_email] . '.' . $dt->format('Y_m_d_H_i_s') . '.' . $ext;

        if (move_uploaded_file($_FILES [post_chat_data] ['tmp_name'], $target_path) == false) {
            printResult(SEND_CHAT_FILE_ERROR, '文件上传失败', -1);
        } else {
            $FileName = substr($target_path, strrpos($target_path, '/'));
            printResult(SUCCESS, $FileName, -1);
        }

    } else {
        printResult(NO_PARAMS_RECEIVE, '服务器未收到参数', -1);
    }
} catch
(Exception $exception) {
    printResult(FAILURE, $exception->getMessage(), -1);


}

