package cn.edu.hfut.lilei.shareboard.widget.customdialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.edu.hfut.lilei.shareboard.R;
import cn.edu.hfut.lilei.shareboard.adapter.ApplicationInfoAdapter;
import cn.edu.hfut.lilei.shareboard.enity.AppInfo;
import cn.edu.hfut.lilei.shareboard.utils.MyAppUtil;


public class InviteChooserDialog extends Dialog {


    public InviteChooserDialog(Context context) {
        super(context);
    }

    public InviteChooserDialog(Context context, int themeId) {
        super(context, themeId);
    }


    public static class Builder {
        private Context mContext;
        private String mTitle;
        private List<AppInfo> mlistAppInfo = null;
        private int flag = -1;
        private InviteChooserDialog dialog = null;
        private String subject, content;


        public Builder setSubject(String subject) {
            this.subject = subject;
            return this;
        }

        public Builder setContent(String content) {
            this.content = content;
            return this;
        }

        public Builder setData(List<AppInfo> mlistAppInfo) {
            this.mlistAppInfo = mlistAppInfo;
            return this;
        }


        public Builder(Context context) {
            mContext = context;
        }

        public Builder setTitle(int resId) {
            mTitle = (String) mContext.getText(resId);
            return this;
        }

        public Builder setTitle(String title) {
            mTitle = title;
            return this;
        }

        // 获得所有启动Activity的信息，类似于Launch界面
        public void queryAppInfo() {
            mlistAppInfo = new ArrayList<AppInfo>();
            flag = 0;
            PackageManager pm = mContext.getPackageManager(); // 获得PackageManager对象

            //获取短信应用
            Uri smsToUri = Uri.parse("smsto:");
            Intent mainIntent = new Intent(Intent.ACTION_SENDTO, smsToUri);

            // 通过查询，获得所有ResolveInfo对象.
            List<ResolveInfo> resolveInfos = new ArrayList<>();
            if (pm.queryIntentActivities(mainIntent, PackageManager.MATCH_DEFAULT_ONLY)
                    .size() != 0) {
                resolveInfos.add(
                        pm.queryIntentActivities(mainIntent, PackageManager.MATCH_DEFAULT_ONLY)
                                .get(0));
                flag += 1;
            }
            //获取邮件应用
            Uri mailToUri = Uri.parse("mailto:");
            mainIntent = new Intent(Intent.ACTION_SENDTO, mailToUri);
            // 通过查询，获得所有ResolveInfo对象.
            if (pm.queryIntentActivities(mainIntent, PackageManager.MATCH_DEFAULT_ONLY)
                    .size() != 0) {
                resolveInfos.add(
                        pm.queryIntentActivities(mainIntent, PackageManager.MATCH_DEFAULT_ONLY)
                                .get(0));
                flag += 3;
            }

            // 调用系统排序 ， 根据name排序
            // 该排序很重要，否则只能显示系统应用，而不能列出第三方应用程序
//            Collections.sort(resolveInfos, new ResolveInfo.DisplayNameComparator(pm));
            if (mlistAppInfo != null) {
                mlistAppInfo.clear();
                for (ResolveInfo reInfo : resolveInfos) {
                    String activityName = reInfo.activityInfo.name; // 获得该应用程序的启动Activity的name
                    String pkgName = reInfo.activityInfo.packageName; // 获得应用程序的包名
                    String appLabel = (String) reInfo.loadLabel(pm); // 获得应用程序的Label
                    Drawable icon = reInfo.loadIcon(pm); // 获得应用程序图标
                    // 为应用程序的启动Activity 准备Intent
//                    Intent launchIntent = new Intent();
//                    launchIntent.setData(Uri.parse("mailto:"));
//                    launchIntent.putExtra(Intent.EXTRA_SUBJECT, "报到");
//                    launchIntent.putExtra(Intent.EXTRA_TEXT, "我来上班啦");
//                    launchIntent.setType("text/plain");
//                    launchIntent.putExtra("sms_body", "你好");
//
//                    launchIntent.setComponent(new ComponentName(pkgName,
//                            activityName));
                    // 创建一个AppInfo对象，并赋值
                    AppInfo appInfo = new AppInfo();
                    appInfo.setAppLabel(appLabel);
                    appInfo.setPkgName(pkgName);
                    appInfo.setAppName(
                            MyAppUtil.getApplicationNameByPackageName(
                                    mContext, pkgName));
                    appInfo.setAppIcon(icon);
//                    appInfo.setIntent(launchIntent);
                    mlistAppInfo.add(appInfo); // 添加至列表中
//                    System.out.println(appLabel + " activityName---" + activityName
//                            + " pkgName---" + pkgName);
                }
                // 创建一个AppInfo对象，用来 复制到剪贴板
                AppInfo appInfo = new AppInfo();
                appInfo.setAppLabel("");
                appInfo.setPkgName("");
                appInfo.setAppName("复制到剪贴板");
                appInfo.setAppIcon(mContext.getResources()
                        .getDrawable(R.drawable.copy));
                appInfo.setIntent(null);
                mlistAppInfo.add(appInfo); // 添加至列表中
            }

        }

        /**
         * 获取短信类和邮件类的应用信息（名称和图标），并添加一条“复制到剪贴板”
         */
        public InviteChooserDialog create() {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.dialog_invite_chooser, null);

            final InviteChooserDialog inviteChooserDialog = new InviteChooserDialog(
                    mContext, R.style.CustomAlertDialog);
            inviteChooserDialog.addContentView(view, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            TextView tvAlertTitle = (TextView) view
                    .findViewById(R.id.tvAlertDialogTitle);
            tvAlertTitle.setText(mTitle);
            queryAppInfo();

            ApplicationInfoAdapter browseAppAdapter = new ApplicationInfoAdapter(
                    mContext, mlistAppInfo);
            ListView listContent = (ListView) view.findViewById(R.id.lv_dialog_invite_chooser);
            listContent.setAdapter(browseAppAdapter);
            listContent.setOnItemClickListener(
                    new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position,
                                                long l) {
                            switch (flag) {
                                case 0:
                                    dialog.dismiss();
                                    MyAppUtil.copy(mContext,
                                            content);
                                    break;
                                case 1:
                                    switch (position) {
                                        case 0:
                                            dialog.dismiss();
                                            MyAppUtil.sendSMS
                                                    (mContext, content);
                                            break;
                                        case 1:
                                            dialog.dismiss();
                                            MyAppUtil.copy(mContext, content);
                                            break;
                                    }
                                    break;
                                case 3:
                                    switch (position) {
                                        case 0:
                                            dialog.dismiss();
                                            MyAppUtil
                                                    .sendMail(mContext, subject, content);
                                            break;
                                        case 1:
                                            dialog.dismiss();
                                            MyAppUtil.copy(
                                                    mContext, content);
                                            break;

                                    }
                                    break;
                                case 4:
                                    switch (position) {
                                        case 0:
                                            dialog.dismiss();
                                            MyAppUtil.sendSMS(
                                                    mContext, content);
                                            break;
                                        case 1:
                                            dialog.dismiss();
                                            MyAppUtil.sendMail(
                                                    mContext, subject, content);
                                            break;
                                        case 2:
                                            dialog.dismiss();
                                            MyAppUtil.copy(
                                                    mContext, content);
                                            break;

                                    }
                                    break;
                            }
                        }
                    });
            return inviteChooserDialog;
        }

        public InviteChooserDialog show() {
            dialog = create();
            dialog.show();
            return dialog;
        }

        public void cancle() {
            dialog.dismiss();
        }

        // 构造一个AppInfo对象 ，并赋值
        private AppInfo getAppInfo(PackageManager pm, ApplicationInfo app) {
            AppInfo appInfo = new AppInfo();
            appInfo.setAppLabel((String) app.loadLabel(pm));
            appInfo.setAppIcon(app.loadIcon(pm));
            appInfo.setPkgName(app.packageName);
            return appInfo;
        }


    }

}