<?php
session_start();
header("Content-type: text/html; charset=utf-8");
//error_reporting(0);


try {
    require_once $_SERVER['DOCUMENT_ROOT'] . '/controller/conn/settings.php';
    require_once 'Register.php';
    require_once $_SERVER['DOCUMENT_ROOT'] . '/util/UrlUtil.php';
    require_once $_SERVER['DOCUMENT_ROOT'] . '/util/FileUtil.php';
    require_once $_SERVER['DOCUMENT_ROOT'] . '/util/EncryptUtil.php';
    require_once $_SERVER['DOCUMENT_ROOT'] . '/model/User.php';
    require_once $_SERVER['DOCUMENT_ROOT'] . '/controller/conn/Session.php';
    $data = array("token" => "", "avatar" => "");
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
//        $user->setToken(EncryptUtil::hash($user->getEmail() . $user->getPassword() . $user->getRegisterTime(), $user->getRegisterTime()));
        $user->setToken(EncryptUtil::hash($user->getEmail() . $user->getPassword() . $user->getRegisterTime(), (new DateTime())->format('Y-m-d H:i:s')));
        $register = new Register($user);
        $avatarFileName = '';
        if (isset($_FILES[post_user_avatar])) {
            $create_folder_path = $_SERVER['DOCUMENT_ROOT'] . '/assets/avatar';
            createFolder($create_folder_path);
            $target_path = $create_folder_path . "/" . $user->getEmail() . '.' . $dt->format('Y_m_d_H_i_s') . '.' . FileUtil::get_extension(basename($_FILES [post_user_avatar] ['name']));
            if (move_uploaded_file($_FILES [post_user_avatar] ['tmp_name'], $target_path) == false) {
                printResult(UPLOAD_AVATAR_ERROR, '上传头像失败', $data);
            } else {
                $avatarFileName = substr($target_path, strrpos($target_path, '/'));
                $user->setAvatar($avatarFileName);//保存头像文件名
            }
        } else {
            $user->setAvatar("空");
        }


        if ($register->saveUser()) {
            //把token 放到session中
            Session::set(SESSION_TOKEN, $user->getToken(), 2592000);//30天过期
            //返回token
            $data['token'] = $user->getToken();
            $data['avatar'] = $user->getAvatar();
            printResult(SUCCESS, '注册成功', $data);

        } else {
            //删除上传的头像文件
            FileUtil::delFile($target_path);
            printResult(SAVE_USER_ERROR, '注册失败', $data);
        }

    } else {
        printResult(NO_PARAMS_RECEIVE, '服务器未收到参数', $data);
    }
} catch (Exception $exception) {
    printResult(FAILURE, $exception->getMessage(), $data);

}

