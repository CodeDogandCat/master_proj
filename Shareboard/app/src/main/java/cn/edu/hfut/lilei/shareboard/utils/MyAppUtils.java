package cn.edu.hfut.lilei.shareboard.utils;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Environment;
import android.provider.CalendarContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import cn.edu.hfut.lilei.shareboard.R;
import cn.edu.hfut.lilei.shareboard.data.AppInfo;
import cn.edu.hfut.lilei.shareboard.view.InviteChooserDialog;
import cn.edu.hfut.lilei.shareboard.view.LodingDialog;

import static cn.edu.hfut.lilei.shareboard.data.Config.ALBUM_REQUEST_CODE;
import static cn.edu.hfut.lilei.shareboard.data.Config.CAMERA_REQUEST_CODE;
import static cn.edu.hfut.lilei.shareboard.data.Config.CROP_REQUEST_CODE;
import static cn.edu.hfut.lilei.shareboard.data.Config.IMG_PATH_FOR_CAMERA;
import static cn.edu.hfut.lilei.shareboard.data.Config.TIMEZONE;


public class MyAppUtils {
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

    /**
     * pixel转dp
     *
     * @param context
     * @param px
     * @return
     */
    public static float convertPixelsToDp(Context context, float px) {
        DisplayMetrics metrics = Resources.getSystem()
                .getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return Math.round(dp);
    }

    /**
     * dp转pixel
     *
     * @param context
     * @param dp
     * @return
     */
    public static float convertDpToPixel(Context context, float dp) {
        DisplayMetrics metrics = Resources.getSystem()
                .getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }

    /**
     * dp转px
     *
     * @param context
     * @param dp
     * @return
     */
    public static int convertDpToPx(Context context, int dp) {
        return Math.round(dp * (context.getResources()
                .getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT));

    }

    /**
     * px转dp
     *
     * @param context
     * @param px
     * @return
     */
    public static int convertPxToDp(Context context, int px) {
        return Math.round(px / (Resources.getSystem()
                .getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    /**
     * px转dp
     *
     * @param context
     * @param px
     * @return
     */
    public static float dpFromPx(Context context, float px) {
        return px / context.getResources()
                .getDisplayMetrics().density;
    }

    /**
     * dp转px
     *
     * @param context
     * @param dp
     * @return
     */
    public static float pxFromDp(Context context, float dp) {
        return dp * context.getResources()
                .getDisplayMetrics().density;
    }

    /**
     * 开始裁剪
     *
     * @param uri
     */
    public static void startCrop(Activity activity, Uri uri, Uri cropUri, int width, int
            height) {

        //调用Android系统自带的一个图片剪裁页面
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");//进行修剪
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", width);
        intent.putExtra("outputY", height);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cropUri);
        activity.startActivityForResult(intent, CROP_REQUEST_CODE);

    }

    /**
     * 打开相册选择图片
     *
     * @param activity
     */
    public static void startGallery(Activity activity) {


        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        activity.startActivityForResult(intent, ALBUM_REQUEST_CODE);

    }

    /**
     * 打开相机拍照
     *
     * @param activity
     */
    public static void startCamera(Activity activity) {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.
                getExternalStorageDirectory(), IMG_PATH_FOR_CAMERA)));
        activity.startActivityForResult(intent, CAMERA_REQUEST_CODE);

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
            showToast(context, context.getString(R.string.meeting_event_already_add));
            return mEventID;
        } catch (SecurityException e) {
            return -1;
        }


    }

    /**
     * 选择邀请方式
     */
    public static void invite(Context context, String title, String description,
                              List<AppInfo> listAppInfo) {

        final String subject =
                String.format(context.getResources()
                        .getString(R.string.invite_title), title);
        final String content = description;

        final InviteChooserDialog.Builder dialog =
                new InviteChooserDialog.Builder(context);

        dialog.setTitle(context.getString(R.string.choose_invite_type));
        dialog.setSubject(subject);
        dialog.setContent(content);
        dialog.setData(listAppInfo);
        dialog.show();


    }

    /**
     * 加载中
     */
    public static LodingDialog.Builder loding(Context context, String title) {

        LodingDialog.Builder dialog = new LodingDialog.Builder(context);
        dialog.setTitle(title);
        dialog.show();
        return dialog;


    }
}

