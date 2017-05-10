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
define('SESSION_EMAIL', 'user_email');
define('SESSION_MEETING_URL', 'meeting_url');
define('SESSION_USER_AND_MEETING_ID', 'user_and_meeting_id');
define('SESSION_MEETING_ID', 'meeting_id');
define('SESSION_HOST_EMAIL', 'meeting_host_email');
define('ADMIN_EMAIL', '2662083658@qq.com');

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

define('post_to_user_email', '300114');//消息的目标用户
define('post_to_meeting_url', '300115');//消息的目标会议群组
define('post_message_data', '300116');//通知消息

define('post_board_data', '300117');//白板内容
define('post_meeting_url', '300118');//会议url
define('post_meeting_theme', '300119');//会议主题
define('post_meeting_host_user_id', '300120');//会议主持人id
define('post_meeting_is_drawable', '300121');//参加者默认可画
define('post_meeting_is_talkable', '300122');//参加者默认可聊
define('post_meeting_is_add_to_calendar', '300124');//会议是否添加到日历提醒
define('post_meeting_start_time', '300125');//会议开始时间
define('post_meeting_end_time', '300126');//会议结束时间
define('post_meeting_password', '300127');//会议密码
define('post_meeting_status', '300128');//会议状态
define('post_is_enter_meeting', '300129');//是否进入会议
define('post_meeting_check_in_type', '300130');//进会类型（参加/主持）
define('post_meeting_id', '300131');//会议ID
define('post_meeting_page', '300132');//请求的会议列表的页码
define('post_meeting_event_id', '300133');//日历事件ID
define('post_meeting_desc', '300134');//会议描述
define('post_meeting_host_email', '300135');//会议主持人email
define('post_chat_data', '300136');//聊天文件
define('reset_password', '300137');//重置登录密码
define('feedback', '300138');//用户反馈


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
define("HOST_MEETING_ERROR", 4015);//开会失败
define("ADD_MEETING_ERROR", 4016);//加会失败
define("ARRANGE_MEETING_ERROR", 4017);//安排会议失败
define("DELETE_MEETING_ERROR", 4018);//删除会议失败
define("GET_PAGES_ERROR", 4019);//获取页数失败
define("GET_MEETING_LIST_ERROR", 4020);//获取会议列表失败
define("BINDING_ERROR", 4021);//绑定失败
define("LOCK_MEETING_ERROR", 4022);//锁定会议失败
define("UNLOCK_MEETING_ERROR", 4023);//解锁会议失败
define("GET_MEETING_MEMBERS_ERROR", 4024);//获取参与者失败
define("SEND_CHAT_FILE_ERROR", 4025);//发送聊天文件失败


define("REQUEST_ADD_FRIEND_ERROR", 4025);//请求添加好友失败
define("ALREADY_FRIEND_ERROR", 4026);//请求添加好友失败 :已经是好友
define("NOT_EXIST_USER_ERROR", 4027);//请求添加好友失败 :不存在这个用户
define("FRIEND_SELF_ERROR", 4028);//请求添加好友失败 :想和自己加好友


define("REQUEST_DEL_FRIEND_ERROR", 4029);//请求删除好友失败
define("ACCEPT_FRIEND_ERROR", 4030);//同意添加好友失败
define("GET_ALL_FRIEND_ERROR", 4031);//获取好友列表失败
define("INVITE_FRIEND_ERROR", 4032);//邀请联系人加会失败
define("FEEDBACK_ERROR", 4033);//反馈失败
define("UPGRADE_ERROR", 4034);//获取更新信息失败

/**
 * 数据库连接信息
 */

define('DB_HOST', 'localhost');
define('DB_USER', 'lilei');
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

/**
 * @param $code
 */
function printResultNotExit($code, $msg, $data)
{
    $obj = null;
    $obj->code = $code;
    $obj->msg = $msg;
    $obj->data = $data;
    echo json_encode($obj, JSON_UNESCAPED_UNICODE);
}


function createFolder($path)
{
    if (!file_exists($path)) {
        createFolder(dirname($path));
        return mkdir($path, 0777);
    }
}
