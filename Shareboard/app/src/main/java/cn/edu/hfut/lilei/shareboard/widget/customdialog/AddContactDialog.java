package cn.edu.hfut.lilei.shareboard.widget.customdialog;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lzy.okgo.OkGo;

import java.util.ArrayList;

import cn.edu.hfut.lilei.shareboard.R;
import cn.edu.hfut.lilei.shareboard.callback.JsonCallback;
import cn.edu.hfut.lilei.shareboard.models.CommonJson;
import cn.edu.hfut.lilei.shareboard.utils.NetworkUtil;
import cn.edu.hfut.lilei.shareboard.utils.SettingUtil;
import cn.edu.hfut.lilei.shareboard.utils.SharedPrefUtil;
import cn.edu.hfut.lilei.shareboard.utils.StringUtil;
import okhttp3.Call;
import okhttp3.Response;

import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.loding;
import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.showToast;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.NET_DISCONNECT;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.SUCCESS;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.URL_FRIEND;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_message_data;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_need_feature;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_to_user_email;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_token;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_user_email;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_token;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_user_email;

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
        private SpannableString mtitle;
        private LodingDialog.Builder mlodingDialog;

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

        public Builder setTitle(SpannableString title) {
            mtitle = title;
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

            if (mtitle == null) {

                tvAlertTitle.setText(mTitle);
            } else {
                tvAlertTitle.setText(mtitle);

            }
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

                            addContactDialog.dismiss();
                        }
                    });
                } else {
                    btnPositive.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //1.检验 输入框的数据合法性
                            final String toEmail = tvAlertDialogMessage.getText()
                                    .toString();
                            if (!StringUtil.isEmail(toEmail)) {
                                showToast(mContext, mContext.getString(R.string
                                        .can_not_recognize_email));
                                return;
                            } else {
                                String myEmail = (String) SharedPrefUtil.getInstance()
                                        .getData(SettingUtil
                                                .share_user_email, "");
                                if (myEmail.equals("")) {
                                    showToast(mContext, mContext.getString(R.string
                                            .please_relogin));
                                    return;
                                }
                                if (toEmail.equals(myEmail)) {
                                    showToast(mContext, mContext.getString(R.string
                                            .can_not_make_friend_yourself));
                                    return;
                                }

                                //2.发送请求
                                mlodingDialog = loding(mContext, R.string.sending);
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
                                         * 2.获取会议设置
                                         */
                                        ArrayList<String> keyList = new ArrayList<>();
                                        ArrayList<String> valueList = new ArrayList<>();
                                        keyList.add(share_token);
                                        keyList.add(share_user_email);

                                        valueList = SharedPrefUtil.getInstance()
                                                .getStringDatas(keyList);
                                        if (valueList == null) {
                                            return -2;
                                        }

                                        /**
                                         * 3.发送
                                         */
                                        OkGo.post(URL_FRIEND)
                                                .tag(this)
                                                .params(post_need_feature, "requestAddFriend")
                                                .params(post_token, valueList.get(0))
                                                .params(post_user_email, valueList.get(1))
                                                .params(post_to_user_email, toEmail)
                                                .params(post_message_data,
                                                        "lalala")

                                                .execute(new JsonCallback<CommonJson>() {
                                                             @Override
                                                             public void onSuccess(CommonJson o, Call call,
                                                                                   Response response) {
                                                                 if (o.getCode() == SUCCESS) {

                                                                     showToast(mContext, "请求已发送");
                                                                     mlodingDialog.cancle();
                                                                     addContactDialog.dismiss();

                                                                 } else {
                                                                     //提示所有错误
                                                                     mlodingDialog.cancle();
                                                                     showToast(mContext, o.getMsg());
                                                                     addContactDialog.dismiss();
                                                                 }

                                                             }

                                                             @Override
                                                             public void onError(Call call,
                                                                                 Response response,
                                                                                 Exception e) {
                                                                 super.onError(call, response, e);
                                                                 mlodingDialog.cancle();
                                                                 addContactDialog.dismiss();
                                                                 showToast(mContext, R.string.system_error);
                                                             }
                                                         }
                                                );


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
                                            case -1:
                                                break;
                                            case -2:
                                                addContactDialog.dismiss();
                                                showToast(mContext, R.string.please_relogin);
                                                break;
                                            default:
                                                break;
                                        }
                                    }
                                }.execute();
                            }
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