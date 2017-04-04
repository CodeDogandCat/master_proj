package cn.edu.hfut.lilei.shareboard.widget.customdialog;

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

public class AddContactDialog extends Dialog {


    public AddContactDialog(Context context) {
        super(context);
    }

    public AddContactDialog(Context context, int themeId) {
        super(context, themeId);
    }

    public static class Builder {
        private Context mContext;
        private String mTitle, mMessage, mHint;
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

        public String getmMessage() {
            return mMessage;
        }

        public Builder setHint(int resId) {
            mHint = (String) mContext.getText(resId);
            return this;
        }

        public Builder setHint(String hint) {
            mHint = hint;
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

        public AddContactDialog create() {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.dialog_common_input, null);
            final AddContactDialog addContactDialog = new AddContactDialog(
                    mContext, R.style.CustomAlertDialog);
            addContactDialog.addContentView(view, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            TextView tvAlertTitle = (TextView) view
                    .findViewById(R.id.tvAlertDialogTitle);
            tvAlertTitle.setText(mTitle);
            final EditText tvAlertDialogMessage = (EditText) view
                    .findViewById(R.id.tvAlertDialogMessage);

            if (!TextUtils.isEmpty(mHint)) {
                tvAlertDialogMessage.setVisibility(View.VISIBLE);
                tvAlertDialogMessage.setHint(mHint);
            }

            Button btnPositive = (Button) view
                    .findViewById(R.id.btnAlertDialogPositive);
            if (!TextUtils.isEmpty(mPositiveButtonText)) {
                btnPositive.setText(mPositiveButtonText);
                if (mPositiveButtonClickListener != null) {
                    btnPositive.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mPositiveButtonClickListener.onClick(
                                    addContactDialog, BUTTON_POSITIVE);
                        }
                    });
                } else {
                    btnPositive.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            addContactDialog.dismiss();
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
                                    addContactDialog, BUTTON_NEGATIVE);
                        }
                    });
                } else {
                    btnNegative.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            addContactDialog.dismiss();
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
            return addContactDialog;
        }

        public AddContactDialog show() {
            AddContactDialog dialog = create();
            dialog.show();
            return dialog;
        }
    }

}