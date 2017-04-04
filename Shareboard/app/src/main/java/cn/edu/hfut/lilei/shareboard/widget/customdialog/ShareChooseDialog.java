package cn.edu.hfut.lilei.shareboard.widget.customdialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
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
import cn.edu.hfut.lilei.shareboard.utils.ImageUtil;


public class ShareChooseDialog extends Dialog {


    public ShareChooseDialog(Context context) {
        super(context);
    }

    public ShareChooseDialog(Context context, int themeId) {
        super(context, themeId);
    }


    public static class Builder {
        private Context mContext;
        private String mTitle;
        private List<AppInfo> mlistAppInfo = null;
        private int flag = -1;
        private ShareChooseDialog dialog = null;
        private Uri cropUri;
        private IShareWebPage mCallback;

        public interface IShareWebPage {
            void shareWebPage();
        }

        public Builder setShareWebPageCallback(IShareWebPage callback) {
            mCallback = callback;
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
            if (mlistAppInfo != null) {
                mlistAppInfo.clear();

                //相册
                AppInfo appInfo2 = new AppInfo();
                appInfo2.setAppName(mContext.getString(R.string.pic));
                appInfo2.setAppIcon(mContext.getResources()
                        .getDrawable(R.drawable.gallery));
                mlistAppInfo.add(appInfo2); // 添加至列表中

                //网页
                AppInfo appInfo = new AppInfo();
                appInfo.setAppName(mContext.getString(R.string.web_page));
                appInfo.setAppIcon(mContext.getResources()
                        .getDrawable(R.drawable.explore));
                mlistAppInfo.add(appInfo); // 添加至列表中
            }
        }


        public ShareChooseDialog create() {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.dialog_alter_head, null);

            final ShareChooseDialog inviteChooserDialog = new ShareChooseDialog(
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
            ListView listContent = (ListView) view.findViewById(R.id.lv_dialog_alter_head);
            listContent.setAdapter(browseAppAdapter);


            listContent.setOnItemClickListener(
                    new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position,
                                                long l) {
                            switch (position) {

                                case 0:
                                    dialog.dismiss();
                                    ImageUtil.startGallery((Activity) mContext);
                                    break;
                                case 1:
                                    dialog.dismiss();
                                    mCallback.shareWebPage();

                            }

                        }
                    });
            return inviteChooserDialog;
        }

        public ShareChooseDialog show() {
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