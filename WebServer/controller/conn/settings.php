<?php
error_reporting(0);
/**
 * 系统信息
 */

define('SYS_NAME', '小喵白板');
/**
 * session name 信息
 */

define('SESSION_VERIFY_CODE', 'verifyCode');
define('SESSION_TOKEN', 'token');

/**
 * request name 信息
 */

define('post_check_verify_code', '300100');
define('post_user_email', '300101');
define('post_user_family_name', '300102');
define('post_user_given_name', '300103');
define('post_user_login_password', '300104');
define('post_user_client_key', '300105');
define('post_token', '300106');
define('post_user_avatar', '300107');
define('post_need_feature', '300108');//具体请求什么功能
define('update_avatar', '300109');//请求更新头像
define('update_name', '300110');//请求更新姓名
define('update_password', '300111');//请求更新密码
define('post_user_login_password_old', '300112');//旧密码
define('post_user_login_password_new', '300113');//新密码


/**
 * 状态参数
 */

define("FAILURE", 0);//失败
define("SUCCESS", 100);//成功
define("NO_PARAMS_RECEIVE", 4001);//没接受到参数
define("USER_EXISTS", 4002);//存在相同用户
define("DATABASE_CONN_FAILED", 4003);//数据库连接失败
define("SEND_VERIFY_CODE_FAILED", 4004);//发送验证码失败
define("VERIFY_CODE_EXPIRE", 4005);//验证码超时
define("VERIFY_CODE_NOT_MATCH", 4006);//验证码错误
define("SAVE_USER_ERROR", 4007);//保存用户信息出错
define("LOGIN_ERROR", 4008);//登录错误
define("ACCESS_VOLATION", 4009);//未授权访问
define("DATABASE_OPERATE_FAILED", 4010);//数据库操作失败
define("UPLOAD_AVATAR_ERROR", 4011);//上传头像失败
define("UPDATE_AVATAR_ERROR", 4012);//更新头像失败
define("UPDATE_NAME_ERROR", 4013);//更新姓名失败
define("UPDATE_PASSWORD_ERROR", 4014);//更新密码失败

/**
 * 数据库连接信息
 */

define('DB_HOST', 'localhost');
define('DB_USER', 'root');
define('DB_PWD', 'lilei123');
define('DB_NAME', 'board');
define('DBMS', 'mysql');

/**
 * SMTP信息
 */

define('SMTP_HOST', 'smtp.qq.com');
define('SMTP_AUTH ', true);
define('SMTP_USERNAME', '2662083658@qq.com');
define('SMTP_PASSWORD', 'ggkbkuemsiowdihc');
define('SMTP_PORT', 587);


/**
 * @param $code
 */
function printResult($code, $msg, $data)
{
    $obj = null;
    $obj->code = $code;
    $obj->msg = $msg;
    $obj->data = $data;
    echo json_encode($obj, JSON_UNESCAPED_UNICODE);
    exit(0);//停止脚本
}


function createFolder($path)
{
    if (!file_exists($path)) {
        createFolder(dirname($path));
        return mkdir($path, 0777);
    }
}