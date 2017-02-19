package cn.edu.hfut.lilei.shareboard.utils;


public class SettingUtil {
    //系统调试参数
    public static final int FAILURE = 0; // 失败
    public static final int SUCCESS = 1; // 成功
    public static final int OFFLINE = 2; // 如果支持离线，进入离线模式
    public static final int SHOW_TIME_MIN = 800;//最短跳转时间
    public static final String TAG = "shareboard";//调试TAG

    //权限请求参数
    public static final int PERMISSION_REQUEST_CODE = 100;//权限请求代码
    public static final int ALBUM_REQUEST_CODE = 1;//相册请求代码
    public static final int CAMERA_REQUEST_CODE = 2;//相机请求代码
    public static final int CROP_REQUEST_CODE = 4;//裁剪请求代码
    public static final String PERMISSION_REQUEST_TITLE = "帮助";//权限请求提示框标题
    public static final String PERMISSION_REQUEST_CONTENT =
            "当前应用缺少必要权限。\n \n 请点击 \"设置\"-\"权限\"-打开所需权限。";//权限请求提示框内容
    public static final String PERMISSION_REQUEST_CANCEL = "取消";//权限请求提示框-取消
    public static final String PERMISSION_REQUEST_ENSURE = "设置";//权限请求提示框-允许

    //文件路径参数
    public static final String IMG_PATH_FOR_CAMERA = "temp.jpg";
    public static final String IMG_PATH_FOR_CROP = "crop_image.jpg";

    //字符串常量
    public static final String TIMEZONE = "GTM+8";
}
