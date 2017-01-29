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

public class CustomAlertDialog extends Dialog {


    public CustomAlertDialog(Context context) {
        super(context);
    }

    public CustomAlertDialog(Context context, int themeId) {
        super(context, themeId);
    }

    public static class Builder {
        private Context mContext;
        private String mTitle, mMessage;
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

        public Builder setMessage(int resId) {
            mMessage = (String) mContext.getText(resId);
            return this;
        }

        public Builder setMessage(String message) {
            mMessage = message;
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

        public CustomAlertDialog create() {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.dialog_contacts_add, null);
            final CustomAlertDialog customAlertDialog = new CustomAlertDialog(
                    mContext, R.style.CustomAlertDialog);
            customAlertDialog.addContentView(view, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            TextView tvAlertTitle = (TextView) view
                    .findViewById(R.id.tvAlertDialogTitle);
            tvAlertTitle.setText(mTitle);

//            if (!TextUtils.isEmpty(mMessage)) {
            EditText tvAlertDialogMessage = (EditText) view
                    .findViewById(R.id.tvAlertDialogMessage);
            tvAlertDialogMessage.setVisibility(View.VISIBLE);
//                View vMessageLine = (View) view.findViewById(R.id.vMessageLine);
//                vMessageLine.setVisibility(View.VISIBLE);
//            }

            Button btnPositive = (Button) view
                    .findViewById(R.id.btnAlertDialogPositive);
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
                    .findViewById(R.id.btnAlertDialogNegative);
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

        public CustomAlertDialog show() {
            CustomAlertDialog dialog = create();
            dialog.show();
            return dialog;
        }
    }

}