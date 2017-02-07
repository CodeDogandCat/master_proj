package cn.edu.hfut.lilei.shareboard.view;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.edu.hfut.lilei.shareboard.R;

public class NameInputDialog extends Dialog {


    public NameInputDialog(Context context) {
        super(context);
    }

    public NameInputDialog(Context context, int themeId) {
        super(context, themeId);
    }

    public static class Builder {
        private Context mContext;
        private String mTitle;
        private String mFamilyName;
        private String mGivenName;
        private String mPositiveButtonText, mNegativeButtonText;

        private OnClickListener mPositiveButtonClickListener,
                mNegativeButtonClickListener;


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

        public Builder setFamilyName(int resId) {
            mFamilyName = (String) mContext.getText(resId);
            return this;
        }

        public Builder setFamilyName(String familyName) {
            mFamilyName = familyName;
            return this;
        }

        public Builder setGivenName(int resId) {
            mGivenName = (String) mContext.getText(resId);
            return this;
        }

        public Builder setGivenName(String givenName) {
            mGivenName = givenName;
            return this;
        }


        public Builder setPositiveButton(int positiveButtonTextId,
                                         OnClickListener listener) {
            mPositiveButtonText = (String) mContext
                    .getText(positiveButtonTextId);
            mPositiveButtonClickListener = listener;
            return this;
        }

        public Builder setPositiveButton(String positiveButtonText,
                                         OnClickListener listener) {
            mPositiveButtonText = positiveButtonText;
            mPositiveButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(int negativeButtonTextId,
                                         OnClickListener listener) {
            mNegativeButtonText = (String) mContext
                    .getText(negativeButtonTextId);
            mNegativeButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(String negativeButtonText,
                                         OnClickListener listener) {
            mNegativeButtonText = negativeButtonText;
            mNegativeButtonClickListener = listener;
            return this;
        }

        public NameInputDialog create() {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.dialog_settingmyinfo_name, null);
            final NameInputDialog customAlertDialog = new NameInputDialog(
                    mContext, R.style.CustomAlertDialog);
            customAlertDialog.addContentView(view, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            TextView tvAlertTitle = (TextView) view
                    .findViewById(R.id.tv_dialog_settingmyinfo_title);
            tvAlertTitle.setText(mTitle);

            EditText etDialogFamilyName = (EditText) view
                    .findViewById(R.id.et_dialog_settingmyinfo_familyname);
            etDialogFamilyName.setText(mFamilyName);

            EditText etDialogGivenName = (EditText) view
                    .findViewById(R.id.et_dialog_settingmyinfo_givenname);
            etDialogGivenName.setText(mGivenName);
            Button btnPositive = (Button) view
                    .findViewById(R.id.btn_dialog_settingmyinfo_positive);
            if (!TextUtils.isEmpty(mPositiveButtonText)) {
                btnPositive.setText(mPositiveButtonText);
                if (mPositiveButtonClickListener != null) {
                    btnPositive.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mPositiveButtonClickListener.onClick(
                                    customAlertDialog, BUTTON_POSITIVE);
                        }
                    });
                }
            } else {
                btnPositive.setVisibility(View.GONE);
            }
            Button btnNegative = (Button) view
                    .findViewById(R.id.btn_dialog_settingmyinfo_negative);
            if (!TextUtils.isEmpty(mNegativeButtonText)) {
                btnNegative.setText(mNegativeButtonText);
                if (mNegativeButtonClickListener != null) {
                    btnNegative.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mNegativeButtonClickListener.onClick(
                                    customAlertDialog, BUTTON_NEGATIVE);
                        }
                    });
                } else {
                    btnNegative.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            customAlertDialog.dismiss();
                        }
                    });
                }
            } else {
                btnNegative.setVisibility(View.GONE);
            }
            if (View.VISIBLE == btnPositive.getVisibility()
                    && View.GONE == btnNegative.getVisibility()) {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) btnPositive
                        .getLayoutParams();
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                btnPositive.setLayoutParams(layoutParams);
            }
            return customAlertDialog;
        }

        public NameInputDialog show() {
            NameInputDialog dialog = create();
            dialog.show();
            return dialog;
        }
    }

}