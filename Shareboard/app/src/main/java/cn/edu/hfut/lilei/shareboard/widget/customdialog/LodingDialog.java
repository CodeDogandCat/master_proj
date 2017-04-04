package cn.edu.hfut.lilei.shareboard.widget.customdialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.edu.hfut.lilei.shareboard.R;


public class LodingDialog extends Dialog {


    public LodingDialog(Context context) {
        super(context);
    }

    public LodingDialog(Context context, int themeId) {
        super(context, themeId);
    }


    public static class Builder {
        private Context mContext;
        private String mTitle;
        private LodingDialog dialog = null;


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


        /**
         * 创建
         */
        public LodingDialog create() {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.dialog_loding, null);

            LodingDialog inviteChooserDialog = new LodingDialog(
                    mContext, R.style.CustomAlertDialog);
            inviteChooserDialog.addContentView(view, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            TextView tvAlertTitle = (TextView) view
                    .findViewById(R.id.tvAlertDialogTitle);
            tvAlertTitle.setText(mTitle);

            return inviteChooserDialog;
        }

        /**
         * 显示
         *
         * @return
         */

        public LodingDialog show() {
            dialog = create();
            dialog.show();
            return dialog;
        }

        /**
         * 消失
         */
        public void cancle() {
            dialog.dismiss();
        }


    }

}