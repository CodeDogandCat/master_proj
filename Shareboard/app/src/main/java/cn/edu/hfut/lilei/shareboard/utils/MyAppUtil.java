package cn.edu.hfut.lilei.shareboard.utils;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.widget.Toast;

import java.io.File;

import cn.edu.hfut.lilei.shareboard.R;

import static cn.edu.hfut.lilei.shareboard.data.Config.ALBUM_REQUEST_CODE;
import static cn.edu.hfut.lilei.shareboard.data.Config.CAMERA_REQUEST_CODE;
import static cn.edu.hfut.lilei.shareboard.data.Config.CROP_REQUEST_CODE;
import static cn.edu.hfut.lilei.shareboard.data.Config.IMG_PATH_FOR_CAMERA;


public class MyAppUtil {
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
     * 实现文本复制功能
     * add by wangqianzhou
     *
     * @param content
     */
    public static void copy(String content, Context context) {
        // 得到剪贴板管理器
        ClipboardManager cmb =
                (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(content.trim());
        showToast(context.getString(R.string.alreday_copy_to_sheet), context);
    }

    /**
     * 发送短信
     *
     * @param smsBody
     */

    public static void sendSMS(String smsBody, Context context)

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
     * @param Body
     */

    public static void sendMail(String subject, String Body, Context context)

    {
        Uri mailToUri = Uri.parse("mailto:");
        Intent intent = new Intent(Intent.ACTION_SENDTO, mailToUri);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject); // 主题
        intent.putExtra(Intent.EXTRA_TEXT, Body); // 正文
        context.startActivity(
                Intent.createChooser(intent, context.getString(R.string.choose_mail)));

    }

    //吐司通知
    public static void showToast(String msg, Context context) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT)
                .show();
    }

    //各种尺寸转化
    public static float convertPixelsToDp(Context context, float px) {
        DisplayMetrics metrics = Resources.getSystem()
                .getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return Math.round(dp);
    }

    public static float convertDpToPixel(Context context, float dp) {
        DisplayMetrics metrics = Resources.getSystem()
                .getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }

    public static int convertDpToPx(Context context, int dp) {
        return Math.round(dp * (context.getResources()
                .getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT));

    }

    public static int convertPxToDp(Context context, int px) {
        return Math.round(px / (Resources.getSystem()
                .getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static float dpFromPx(Context context, float px) {
        return px / context.getResources()
                .getDisplayMetrics().density;
    }

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


        Intent intent = new Intent("com.android.camera.action.CROP");//调用Android系统自带的一个图片剪裁页面,
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

    public static void startGallery(Activity activity) {


        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        activity.startActivityForResult(intent, ALBUM_REQUEST_CODE);

    }

    public static void startCamera(Activity activity) {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.
                getExternalStorageDirectory(), IMG_PATH_FOR_CAMERA)));
        activity.startActivityForResult(intent, CAMERA_REQUEST_CODE);

    }

}
