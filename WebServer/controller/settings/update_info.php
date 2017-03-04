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
    //token 拦截 检验
    require_once $_SERVER['DOCUMENT_ROOT'] . '/controller/conn/tokenInterceptor.php';

    if (isset($_REQUEST[post_need_feature])) {
        if ($_REQUEST[post_need_feature] == update_avatar) {
            //更新头像

            if (isset($_FILES[post_user_avatar]) &&
                isset($_REQUEST[post_user_email])
            ) {

                $user = new User($_REQUEST[post_user_email]);
                $create_folder_path = $_SERVER['DOCUMENT_ROOT'] . '/assets/avatar';
                createFolder($create_folder_path);
                $target_path = $create_folder_path . "/" . $user->getEmail() . '.' . FileUtil::get_extension(basename($_FILES [post_user_avatar] ['name']));
                if (move_uploaded_file($_FILES [post_user_avatar] ['tmp_name'], $target_path) == false) {

                    printResult(UPDATE_AVATAR_ERROR, '更新头像失败', -1);
                } else {

                    printResult(SUCCESS, '更新头像成功', -1);
                }

            } else {
                printResult(NO_PARAMS_RECEIVE, '服务器未收到参数', -1);
            }


        } elseif ($_REQUEST[post_need_feature] == update_name) {
            //更新姓名
            if (isset($_REQUEST[post_user_email]) &&
                isset($_REQUEST[post_user_family_name]) &&
                isset($_REQUEST[post_user_given_name])
            ) {
                $user = new User($_REQUEST[post_user_email]);
                $user->setFamilyName($_REQUEST[post_user_family_name]);
                $user->setGivenName($_REQUEST[post_user_given_name]);
                $update = new Update($user);
                if ($update->updateName()) {
                    printResult(SUCCESS, '更新姓名成功', -1);
                } else {
                    printResult(UPDATE_NAME_ERROR, '更新姓名失败', -1);
                }
            } else {
                printResult(NO_PARAMS_RECEIVE, '服务器未收到参数', -1);
            }

        } elseif ($_REQUEST[post_need_feature] == update_password) {
            //更新密码
            if (isset($_REQUEST[post_user_email]) &&
                isset($_REQUEST[post_user_login_password_old]) &&
                isset($_REQUEST[post_user_login_password_new])
            ) {
                $user = new User($_REQUEST[post_user_email]);
                $oldPwd = $_REQUEST[post_user_login_password_old];
                $newPwd = $_REQUEST[post_user_login_password_new];
                $update = new Update($user);
                if ($update->updatePassword($oldPwd, $newPwd)) {
                    printResult(SUCCESS, '更新密码成功', -1);
                } else {
                    printResult(UPDATE_PASSWORD_ERROR, '更新密码失败', -1);
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

