package cn.edu.hfut.lilei.shareboard.view;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.edu.hfut.lilei.shareboard.R;
import cn.edu.hfut.lilei.shareboard.utils.NetworkUtil;
import cn.edu.hfut.lilei.shareboard.utils.SharedPrefUtil;
import cn.edu.hfut.lilei.shareboard.utils.StringUtil;

import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.loding;
import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.showToast;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.NET_DISCONNECT;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.WRONG_FORMAT_INPUT_NO1;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.WRONG_FORMAT_INPUT_NO2;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.WRONG_FORMAT_INPUT_NO3;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_token;

public class NameInputDialog extends Dialog {


    public NameInputDialog(Context context) {
        super(context);
    }

    public NameInputDialog(Context context, int themeId) {
        super(context, themeId);
    }

    public static class Builder {
        private LodingDialog.Builder mlodingDialog;
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

        public String getTitle() {
            return mTitle;
        }

        public String getFamilyName() {
            return mFamilyName;
        }

        public String getGivenName() {
            return mGivenName;
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
                } else {
                    btnPositive.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mlodingDialog = loding(mContext, R.string.saving);

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
                                    /**
                                     * 2.检查姓名格式
                                     */
                                    final String familyName = getFamilyName().trim();
                                    final String givenName = getFamilyName().trim();
                                    if (!StringUtil.isValidName(familyName)) {
                                        //姓格式不对
                                        return WRONG_FORMAT_INPUT_NO1;
                                    }

                                    if (!StringUtil.isValidName(givenName)) {
                                        //名格式不对
                                        return WRONG_FORMAT_INPUT_NO2;
                                    }
                                    String token = (String) SharedPrefUtil.getInstance()
                                            .getData
                                                    (share_token, "空");
                                    if (token.equals("空")) {
                                        return -1;
                                    }
                                    /**
                                     * 3.上传数据
                                     */

//                                    OkGo.post(URL_SAVE_USR_INFO)
//                                            .tag(this)
//                                            .params(post_token, token)
//                                            .params(post_user_family_name, familyName)
//                                            .params(post_user_given_name, givenName)
//                                            .execute(new JsonCallback<Common>() {
//                                                @Override
//                                                public void onSuccess(Common o, Call call,
//                                                                      Response response) {
//                                                    if (o.getCode() == SUCCESS) {
//                                                        /**
//                                                         * 4.更改成功,缓存姓,名
//                                                         */
//                                                        SharedPrefUtil.getInstance()
//                                                                .saveData(share_family_name,
//                                                                        familyName);
//                                                        SharedPrefUtil.getInstance()
//                                                                .saveData(share_given_name,
//                                                                        givenName);
//                                                        mlodingDialog.cancle();
//                                                        /**
//                                                         * 5.对话框消失,更新姓名
//                                                         */
//                                                        customAlertDialog.dismiss();
//
//
//                                                    } else {
//                                                        //提示所有错误
//                                                        mlodingDialog.cancle();
//                                                        showLog(o.getMsg());
//                                                        showToast(mContext, o.getMsg());
//                                                    }
//                                                }
//
//                                                @Override
//                                                public void onError(Call call, Response response,
//                                                                    Exception e) {
//                                                    super.onError(call, response, e);
//                                                    mlodingDialog.cancle();
//                                                    showToast(mContext, R.string.system_error);
//                                                }
//                                            });


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
                                        case WRONG_FORMAT_INPUT_NO1:
                                            //提示姓格式不对
                                            showToast(mContext,
                                                    R.string.can_not_recognize_family_name);
                                            break;
                                        case WRONG_FORMAT_INPUT_NO2:
                                            //提示名格式不对
                                            showToast(mContext,
                                                    R.string.can_not_recognize_given_name);
                                            break;
                                        case WRONG_FORMAT_INPUT_NO3:
                                            //提示登录密码格式不对
                                            showToast(mContext,
                                                    R.string.can_not_recognize_login_password);
                                            break;
                                        case -1:
                                            break;

                                        default:
                                            showToast(mContext, R.string.system_error);
                                            break;
                                    }
                                }
                            }.execute();


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