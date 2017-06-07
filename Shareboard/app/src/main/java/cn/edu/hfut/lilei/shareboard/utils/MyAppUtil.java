package cn.edu.hfut.lilei.shareboard.utils;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Process;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.lzy.okgo.OkGo;

import java.io.File;
import java.util.List;

import cn.edu.hfut.lilei.shareboard.JsonEnity.CommonJson;
import cn.edu.hfut.lilei.shareboard.R;
import cn.edu.hfut.lilei.shareboard.callback.JsonCallback;
import cn.edu.hfut.lilei.shareboard.model.AppInfo;
import cn.edu.hfut.lilei.shareboard.widget.customdialog.InviteChooserDialog;
import cn.edu.hfut.lilei.shareboard.widget.customdialog.LodingDialog;
import okhttp3.Call;
import okhttp3.Response;

import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.NET_DISCONNECT;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.SUCCESS;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.TIMEZONE;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.URL_UPLOAD_LOG;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.log;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_log_file;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_need_feature;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_token;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_user_email;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_token;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_user_email;
import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;


public class MyAppUtil {
    /**
     * 根据应用包名获取应用名
     *
     * @param context
     * @param packageName 应用包名
     * @return
     */
    public static String getApplicationNameByPackageName(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        String Name;
        try {
            Name = pm.getApplicationLabel(
                    pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA))
                    .toString();
        } catch (PackageManager.NameNotFoundException e) {
            Name = "";
        }
        return Name;
    }

    /**
     * 复制内容到剪贴板
     *
     * @param context
     * @param content 文本内容
     */
    public static void copy(Context context, String content) {
        // 得到剪贴板管理器
        ClipboardManager cmb =
                (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(content.trim());
        showToast(context, context.getString(R.string.alreday_copy_to_sheet));
    }

    /**
     * 发送短信
     *
     * @param context
     * @param smsBody 短信内容
     */
    public static void sendSMS(Context context, String smsBody)

    {

        Uri smsToUri = Uri.parse("smsto:");

        Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);

        intent.putExtra("sms_body", smsBody);

        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(
                    Intent.createChooser(intent, context.getString(R.string.choose_sms)));
        }

    }

    /**
     * 发送邮件
     *
     * @param context
     * @param subject 主题
     * @param Body    正文
     */
    public static void sendMail(Context context, String subject, String Body)

    {
        Uri mailToUri = Uri.parse("mailto:");
        Intent intent = new Intent(Intent.ACTION_SENDTO, mailToUri);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject); // 主题
        intent.putExtra(Intent.EXTRA_TEXT, Body); // 正文
        context.startActivity(
                Intent.createChooser(intent, context.getString(R.string.choose_mail)));

    }

    /**
     * 吐司通知（短时间）
     *
     * @param context
     * @param msg     文本
     */
    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT)
                .show();
    }

    public static void showToast(Context context, int strid) {
        Toast.makeText(context, context.getResources()
                .getString(strid), Toast.LENGTH_SHORT)
                .show();
    }

    /**
     * 打印log
     *
     * @param msg
     */
    public static void showLog(String msg) {
        Log.i(TAG, msg);
    }


    /**
     * 删除指定 id 的事件
     *
     * @param context
     * @param eventid
     */
    public static void delCalendarEvent(Context context, long eventid) {

        ContentResolver cr = context.getContentResolver();
        ContentValues updateValues = new ContentValues();
        Uri deleteUri = null;
        deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventid);
        cr.delete(deleteUri, null, null);

    }

    /**
     * 更新指定 id 的事件
     *
     * @param context
     * @param eventid
     */
    public static void updateCalendarEvent(Context context, long eventid, long startMillis,
                                           long endMillis, String title, String description
    ) {

        ContentResolver cr = context.getContentResolver();
        ContentValues updateValues = new ContentValues();
        Uri updateUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventid);
        updateValues.put(CalendarContract.Events.DTSTART, startMillis);
        updateValues.put(CalendarContract.Events.DTEND, endMillis);
        updateValues.put(CalendarContract.Events.TITLE,
                String.format(context.getResources()
                        .getString(R.string.invite_title), title));
        updateValues.put(CalendarContract.Events.DESCRIPTION,
                description);
        cr.update(updateUri, updateValues, null, null);

    }

    /**
     * 查看指定日期的日历事件提醒
     *
     * @param context
     * @param timeMillis 指定日期
     */
    public static void viewCalendarEvent(Context context, long timeMillis) {

        Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
        builder.appendPath("time");
        ContentUris.appendId(builder, timeMillis);
        Intent intent = new Intent(Intent.ACTION_VIEW)
                .setData(builder.build());
        context.startActivity(intent);

    }


    /**
     * 添加事件（提醒）系统日历
     *
     * @param context
     * @param startMillis 开始时间
     * @param endMillis   结束时间
     * @param title       标题
     * @param location    地点
     * @param description 备注
     * @param timezone    时区
     * @return 事件ID
     */
    public static long insertCalendarEvent(Context context, long startMillis, long endMillis,
                                           String title, @Nullable String location, String
                                                   description,
                                           @Nullable String
                                                   timezone) {
        long calID = 1;

        //添加日历事件
        ContentResolver cr = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, startMillis);
        values.put(CalendarContract.Events.DTEND, endMillis);
        values.put(CalendarContract.Events.TITLE,
                String.format(context.getResources()
                        .getString(R.string.invite_title), title));
        values.put(CalendarContract.Events.EVENT_LOCATION,
                context.getString(R.string.meeting_location));
        values.put(CalendarContract.Events.DESCRIPTION,
                description);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, TIMEZONE);//时区
        values.put(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);
        values.put(CalendarContract.Events.CALENDAR_ID, calID);

        try {
            Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
            long mEventID = Long.parseLong(uri.getLastPathSegment());
            // 添加 提前15分钟提醒
            ContentValues values2 = new ContentValues();
            values2.put(CalendarContract.Reminders.MINUTES, 15);
            values2.put(CalendarContract.Reminders.EVENT_ID, mEventID);
            values2.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
            context.getContentResolver()
                    .insert(CalendarContract.Reminders.CONTENT_URI, values2);
//            showToast(context, context.getString(R.string.meeting_event_already_add));
            return mEventID;
        } catch (SecurityException e) {
            return -1;
        }


    }

    /**
     * 选择邀请方式
     */
    public static void invite(Context context, String title, String description,
                              List<AppInfo> listAppInfo, int type, String meeting_url, String
                                      meeting_pwd) {

//        final String subject =
//                String.format(context.getResources()
//                        .getString(R.string.invite_title), title);
        final String content = description;

        final InviteChooserDialog.Builder dialog =
                new InviteChooserDialog.Builder(context, type);

        dialog.setTitle(context.getString(R.string.choose_invite_type));
        dialog.setSubject(title);
        dialog.setContent(content);
        dialog.setData(listAppInfo);
        dialog.setMeetingUrl(meeting_url);
        dialog.setMeetingPwd(meeting_pwd);
        dialog.show();


    }

    /**
     * 加载中
     */
    public static LodingDialog.Builder loding(Context context, int strid) {

        LodingDialog.Builder dialog = new LodingDialog.Builder(context);
        dialog.setTitle(context.getResources()
                .getString(strid));
        dialog.show();
        return dialog;


    }

    /**
     * 应用是否在后台运行
     *
     * @param context
     * @return
     */
    public static boolean isApplicationInBackground(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskList = am.getRunningTasks(1);
        if (taskList != null && !taskList.isEmpty()) {
            ComponentName topActivity = taskList.get(0).topActivity;
            if (topActivity != null && !topActivity.getPackageName()
                    .equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * get the package information of the context
     *
     * @param context
     * @return
     */
    public static PackageInfo getPackageInfo(Context context) {
        PackageInfo info = null;
        try {
            info = context.getPackageManager()
                    .getPackageInfo(
                            context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace(System.err);
        }
        if (info == null)
            info = new PackageInfo();
        return info;
    }


    /**
     * 获取版本名称
     */
    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            // ---get the package tvMeetingId---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return versionName;
    }

    /**
     * 获取版本号
     */
    public static int getAppVersionCode(Context context) {
        int versioncode = -1;
        try {
            // ---get the package tvMeetingId---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versioncode = pi.versionCode;
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return versioncode;
    }


    public static void changeBtnDisable(Button btn) {
        btn.setClickable(
                false);//按钮不可点击
        btn.setBackgroundResource(R.drawable.bg_identify_code_press);
    }

    public static void changeBtnClickable(final Button btn, final int drawable) {
        new Handler().postDelayed(new Runnable() {
            public void run() {

                btn.setClickable(true);//按钮可点击
                btn.setBackgroundResource(drawable);
            }
        }, 2000);
    }


    public static String getsaveDirectory(Context context) {


        if (Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED)) {
            String rootDir = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() +
                    "/DCIM/小喵白板/";

            File file = new File(rootDir);
            if (!file.exists()) {
                if (!file.mkdirs()) {
                    return null;
                }
            }

            return rootDir;
        } else {
            String baseDir = "";
            if (FileUtil.isExternalStorageWritable()) {
                baseDir = context.getExternalFilesDir("")
                        .getAbsolutePath();
            } else {
                baseDir = context.getFilesDir()
                        .getAbsolutePath();
            }
            File dir = new File(baseDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            return baseDir;
        }
    }


    /**
     * 强制更新
     *
     * @param context
     * @param appName
     * @param downUrl
     * @param updateinfo
     */
    public static void forceUpdate(final Context context, final String appName,
                                   final String downUrl,
                                   final String updateinfo) {
        AlertDialog.Builder mDialog = new AlertDialog.Builder(context);
        mDialog.setTitle(appName + "又更新咯！");
        mDialog.setMessage(updateinfo);
        mDialog.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!canDownloadState(context)) {
                    showDownloadSetting(context);
                    return;
                }
                //   DownLoadApk.download(MainActivity.this,downUrl,updateinfo,appName);
                AppInnerDownLoder.downLoadApk(context, downUrl, appName);
            }
        })
                .setCancelable(false)
                .create()
                .show();
    }

    /**
     * 正常更新
     *
     * @param context
     * @param appName
     * @param downUrl
     * @param updateinfo
     */
    public static void normalUpdate(final Context context, final String appName,
                                    final String downUrl,
                                    final String updateinfo) {
        AlertDialog.Builder mDialog = new AlertDialog.Builder(context);
        mDialog.setTitle(appName + "又更新咯！");
        mDialog.setMessage(updateinfo);
        mDialog.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!canDownloadState(context)) {
                    showDownloadSetting(context);
                    return;
                }
                // AppInnerDownLoder.downLoadApk(MainActivity.this,downUrl,appName);
                DownLoadApk.download(context, downUrl, updateinfo, appName);
            }
        })
                .setNegativeButton("暂不更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .create()
                .show();
    }

    /**
     * 上传错误log
     */
    public static void uploadLog(final Context mContext, final String path) {

        new AlertDialog.Builder(mContext).setTitle(R.string.program_error)
                .setMessage(R.string.whether_upload_file)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        final File logFile = new File(path);
                        final LodingDialog.Builder mlodingDialog =
                                loding(mContext, R.string.sending);

                        new AsyncTask<Void, Void, Integer>() {

                            @Override
                            protected Integer doInBackground(Void... voids) {
                                /**
                                 * 1.检查网络状态并提醒
                                 */
                                if (!NetworkUtil.isNetworkConnected(mContext)) {
                                    //网络连接不可用
                                    return NET_DISCONNECT;
                                }
                                String token = (String) SharedPrefUtil.getInstance()
                                        .getData(share_token, "空");

                                //如果没有token,跳转到登录界面
                                if (token.equals("空")) {
                                    return -2;
                                }
                                String email = (String) SharedPrefUtil.getInstance()
                                        .getData(share_user_email, "空");

                                //如果没有email
                                if (token.equals("空")) {
                                    return -2;
                                }

                                /**
                                 * 3.发送密码数据
                                 */
                                OkGo.post(URL_UPLOAD_LOG)
                                        .tag(this)
                                        .isMultipart(true)
                                        .params(post_need_feature, log)
                                        .params(post_token, token)
                                        .params(post_user_email, email)
                                        .params(post_log_file, logFile)
                                        .execute(new JsonCallback<CommonJson>() {
                                                     @Override
                                                     public void onSuccess(CommonJson o, Call call,
                                                                           Response response) {
                                                         if (o.getCode() == SUCCESS) {

                                                             mlodingDialog.cancle();
                                                             showToast(mContext, mContext.getString(
                                                                     R.string.error_log_upload_success));

                                                         } else {
                                                             //提示所有错误
                                                             mlodingDialog.cancle();
                                                         }
                                                         // 退出程序
                                                         Process.killProcess(Process.myPid());
                                                         System.exit(1);

                                                     }

                                                     @Override
                                                     public void onError(Call call,
                                                                         Response response,
                                                                         Exception e) {
                                                         super.onError(call, response, e);
                                                         mlodingDialog.cancle();
                                                         // 退出程序
                                                         Process.killProcess(Process.myPid());
                                                         System.exit(1);
                                                     }
                                                 }
                                        );


                                return -1;

                            }

                            @Override
                            protected void onPostExecute(Integer integer) {
                                super.onPostExecute(integer);
                                mlodingDialog.cancle();
                                switch (integer) {
                                    case NET_DISCONNECT:
                                        //弹出对话框，让用户开启网络
                                        NetworkUtil.setNetworkMethod(mContext);
                                        break;
                                    default:
                                        showLog("%%%%%%%%%%%%%%%%%%%%%%7");
                                        // 退出程序
                                        Process.killProcess(Process.myPid());
                                        System.exit(1);
                                }
                            }
                        }.execute();

                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .create()
                .show();
    }

    /**
     * 无需跟新
     *
     * @param context
     */
    public static void noneUpdate(Context context) {
        AlertDialog.Builder mDialog = new AlertDialog.Builder(context);
        mDialog.setTitle("版本更新")
                .setMessage("当前已是最新版本无需更新")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .create()
                .show();
    }

    public static void showDownloadSetting(Context mContext) {
        String packageName = "com.android.providers.downloads";
        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + packageName));
        if (intentAvailable(intent, mContext)) {
            mContext.startActivity(intent);
        }
    }

    public static boolean intentAvailable(Intent intent, Context mContext) {
        PackageManager packageManager = mContext.getPackageManager();
        List list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }


    public static boolean canDownloadState(Context mContext) {
        try {
            int state = mContext.getPackageManager()
                    .getApplicationEnabledSetting("com.android.providers.downloads");

            if (state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                    || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER
                    || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED) {
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    /**
     * 判断某个界面是否在前台
     *
     * @param context   Context
     * @param className 界面的类名
     * @return 是否在前台显示
     */
    public static boolean isForeground(Context context, String className) {
        if (context == null || TextUtils.isEmpty(className))
            return false;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
            if (className.equals(cpn.getClassName()))
                return true;
        }
        return false;
    }
}

