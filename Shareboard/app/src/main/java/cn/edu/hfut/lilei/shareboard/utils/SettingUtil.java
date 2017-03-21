package cn.edu.hfut.lilei.shareboard.utils;


public class SettingUtil {
    //server url 参数
    //核对邮箱是否已经注册，发送验证码
    public static final String URL_SEND_VERIFY_CODE =
            "http://118.89.102.238/controller/register/send_verify_code.php";
    //保存用户注册信息
    public static final String URL_SAVE_USR_INFO =
            "http://118.89.102.238/controller/register/save_user_info.php";
    //用户注销后登录
    public static final String URL_LOGIN =
            "http://118.89.102.238/controller/login/user_login.php";
    //token拦截器
    public static final String URL_CHECK_TOKEN =
            "http://118.89.102.238/controller/conn/tokenInterceptor.php";
    //头像
    public static final String URL_AVATAR =
            "http://118.89.102.238/assets/avatar";
    //更新
    public static final String URL_UPDATE_SETTINGS =
            "http://118.89.102.238/controller/settings/update_info.php";
    //主持会议(安排)
    public static final String URL_HOST_MEETING =
            "http://118.89.102.238/controller/meeting/host_meeting.php";
    //进入会议
    public static final String URL_ENTER_MEETING =
            "http://118.89.102.238/controller/meeting/enter_meeting.php";
    //开会地址
    public static final String URL_MEETING =
            "http://118.89.102.238/view/index.php";
    public static final String URL_TEST =
            "http://118.89.102.238/view/test.html";

    //post name 参数
    public static final String post_check_verify_code = "300100";
    public static final String post_user_email = "300101";
    public static final String post_user_family_name = "300102";
    public static final String post_user_given_name = "300103";
    public static final String post_user_login_password = "300104";
    public static final String post_user_client_key = "300105";
    public static final String post_token = "300106";
    public static final String post_user_avatar = "300107";
    public static final String post_need_feature = "300108";//具体请求什么功能
    public static final String update_avatar = "300109";//请求更新头像
    public static final String update_name = "300110";//请求更新姓名
    public static final String update_password = "300111";//请求更新密码
    public static final String post_user_login_password_old = "300112";//旧密码
    public static final String post_user_login_password_new = "300113";//新密码
    public static final String post_to_user_email = "300114";//消息的目标用户
    public static final String post_to_meeting_url = "300115";//消息的目标会议群组
    public static final String post_meeting_message = "300116";//聊天消息
    public static final String post_board_data = "300117";//白板内容
    public static final String post_meeting_url = "300118";//会议url
    public static final String post_meeting_theme = "300119";//会议主题
    public static final String post_meeting_host_user_id = "300120";//会议主持人id
    public static final String post_meeting_is_drawable = "300121";//参加者默认可画
    public static final String post_meeting_is_talkable = "300122";//参加者默认可聊
    public static final String post_meeting_is_add_to_calendar = "300124";//会议是否添加到日历提醒
    public static final String post_meeting_start_time = "300125";//会议开始时间
    public static final String post_meeting_end_time = "300126";//会议结束时间
    public static final String post_meeting_password = "300127";//会议密码
    public static final String post_meeting_status = "300128";//会议状态
    public static final String post_is_enter_meeting = "300129";//是否进入会议
    public static final String post_meeting_check_in_type = "300130";//进会类型（参加/主持）
    public static final String post_meeting_id = "300131";//会议ID
    public static final String post_meeting_page = "300132";//请求的会议列表的页码
    public static final String post_meeting_event_id = "300133";//日历事件ID
    public static final String post_meeting_desc = "300134";//会议描述


    //sharepreference name 参数
    public static final String share_user_email = "400101";
    public static final String share_token = "400102";
    public static final String share_avatar = "400103";
    public static final String share_family_name = "400104";
    public static final String share_meeting_theme = "400105";
    public static final String share_meeting_is_drawable = "400106";
    public static final String share_meeting_is_talkable = "400107";
    public static final String share_meeting_is_add_to_calendar = "400108";
    public static final String share_meeting_start_time = "400109";
    public static final String share_meeting_end_time = "400110";
    public static final String share_meeting_password = "400111";
    public static final String share_meeting_id = "400112";
    public static final String share_meeting_url = "400113";
    public static final String share_given_name = "400114";


    //系统调试参数
    public static final int FAILURE = 0; // 失败
    public static final int SUCCESS = 100; // 成功
    public static final int OFFLINE = 2; // 如果支持离线，进入离线模式
    public static final int EMPTY_INPUT = 101; // 输入为空
    public static final int WRONG_FORMAT_INPUT = 102; // 输入格式不正确
    public static final int WRONG_FORMAT_INPUT_NO1 = 103; // 输入格式不正确
    public static final int WRONG_FORMAT_INPUT_NO2 = 104; // 输入格式不正确
    public static final int WRONG_FORMAT_INPUT_NO3 = 105; // 输入格式不正确
    public static final int WRONG_FORMAT_INPUT_NO4 = 106; // 输入格式不正确
    public static final int WRONG_FORMAT_INPUT_NO5 = 107; // 输入格式不正确
    public static final int NO_TOKEN_FOUND = 108; // 本地没有找到可用token
    public static final int NET_DISCONNECT = 503; // 未联网
    public static final int SHOW_TIME_MIN = 800;//最短跳转时间
    public static final String TAG = "shareboard";//调试TAG
    public static final String PACKAGE_NAME = "cn.edu.hfut.lilei.shareboard";//包名
    public static final String INSTALLED = "hasInstalled";//已经安装过
    public static final int COMMON_CHECK_IN = 1;//与会人加会
    public static final int HOST_CHECK_IN = 2;//主持人加会
//    public static final String SYSTEM_DIR = "/data/data/" + PACKAGE_NAME + " /tvMeetingId";//系统文件夹

    //权限请求参数
    public static final int PERMISSION_REQUEST_CODE = 10000;//权限请求代码
    public static final int ALBUM_REQUEST_CODE = 1;//相册请求代码
    public static final int CAMERA_REQUEST_CODE = 2;//相机请求代码
    public static final int CROP_REQUEST_CODE = 4;//裁剪请求代码
    public static final String PERMISSION_REQUEST_TITLE = "帮助";//权限请求提示框标题
    public static final String PERMISSION_REQUEST_CONTENT =
            "当前应用缺少必要权限。\n \n 请点击 \"设置\"-\"权限\"-打开所需权限。";//权限请求提示框内容
    public static final String PERMISSION_REQUEST_CANCEL = "取消";//权限请求提示框-取消
    public static final String PERMISSION_REQUEST_ENSURE = "设置";//权限请求提示框-允许

    //文件路径参数
    public static final String IMG_PATH_FOR_CAMERA = "shareboard_image.jpeg";
    public static final String IMG_PATH_FOR_CROP = "shareboard_image.jpeg";

    //字符串常量
    public static final String TIMEZONE = "GTM+8";
}
