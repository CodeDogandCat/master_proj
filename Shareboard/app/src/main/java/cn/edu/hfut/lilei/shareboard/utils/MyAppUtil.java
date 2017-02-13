package cn.edu.hfut.lilei.shareboard.utils;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.Toast;

import cn.edu.hfut.lilei.shareboard.R;


public class MyAppUtil {
    public static String getApplicationNameByPackageName(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        String Name;
        try {
            Name = pm.getApplicationLabel(pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA)).toString();
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
        ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
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
            context.startActivity(Intent.createChooser(intent, context.getString(R.string.choose_sms)));
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
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.choose_mail)));

    }

    //吐司通知
    public static void showToast(String msg, Context context) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
