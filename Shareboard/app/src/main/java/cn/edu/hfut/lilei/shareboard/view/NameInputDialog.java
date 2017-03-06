package cn.edu.hfut.lilei.shareboard.view;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lzy.okgo.OkGo;

import cn.edu.hfut.lilei.shareboard.R;
import cn.edu.hfut.lilei.shareboard.callback.JsonCallback;
import cn.edu.hfut.lilei.shareboard.models.Register;
import cn.edu.hfut.lilei.shareboard.utils.NetworkUtil;
import cn.edu.hfut.lilei.shareboard.utils.SharedPrefUtil;
import okhttp3.Call;
import okhttp3.Response;

import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.loding;
import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.showLog;
import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.showToast;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.NET_DISCONNECT;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.SUCCESS;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.URL_UPDATE_SETTINGS;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.WRONG_FORMAT_INPUT_NO1;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.WRONG_FORMAT_INPUT_NO2;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.WRONG_FORMAT_INPUT_NO3;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_need_feature;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_token;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_user_email;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_user_family_name;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_user_given_name;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_family_name;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_given_name;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_token;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_user_email;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.update_name;
import static cn.edu.hfut.lilei.shareboard.utils.StringUtil.isValidName;

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
        private TextView mTvGivenName;
        private TextView mTvFamilyName;
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

        public Builder setTextView(TextView familyName, TextView givenName) {
            mTvFamilyName = familyName;
            mTvGivenName = givenName;
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
            final TextView tvAlertTitle = (TextView) view
                    .findViewById(R.id.tv_dialog_settingmyinfo_title);
            final Button btnPositive = (Button) view
                    .findViewById(R.id.btn_dialog_settingmyinfo_positive);

            final Button btnNegative = (Button) view
                    .findViewById(R.id.btn_dialog_settingmyinfo_negative);
            final EditText etDialogFamilyName = (EditText) view
                    .findViewById(R.id.et_dialog_settingmyinfo_familyname);


            customAlertDialog.addContentView(view, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            tvAlertTitle.setText(mTitle);
            etDialogFamilyName.setText(mFamilyName);
            etDialogFamilyName.addTextChangedListener(new TextWatcher() {
                                                          @Override
                                                          public void beforeTextChanged(
                                                                  CharSequence s, int start,
                                                                  int count, int after) {
                                                          }

                                                          @Override
                                                          public void onTextChanged(
                                                                  CharSequence s, int start,
                                                                  int before, int count) {
                                                              if (!"".equals(
                                                                      s.toString())) {
                                                                  btnPositive.setClickable(
                                                                          true);//按钮可点击
                                                                  btnPositive.setBackgroundResource(R
                                                                          .drawable.btn_yellow);
                                                                  //颜色变亮，提示用户能够点按
                                                              } else {
                                                                  btnPositive.setClickable(
                                                                          false);//按钮不可点击
                                                                  btnPositive.setBackgroundResource(R
                                                                          .drawable.bg_identify_code_press);
                                                              }
                                                          }

                                                          @Override
                                                          public void afterTextChanged(
                                                                  Editable s) {
                                                              setFamilyName(s.toString()
                                                                      .trim());
                                                          }
                                                      }

            );

            EditText etDialogGivenName = (EditText) view
                    .findViewById(R.id.et_dialog_settingmyinfo_givenname);
            etDialogGivenName.setText(mGivenName);
            etDialogGivenName.addTextChangedListener(new TextWatcher() {
                                                         @Override
                                                         public void beforeTextChanged(
                                                                 CharSequence s, int start,
                                                                 int count, int after) {
                                                         }

                                                         @Override
                                                         public void onTextChanged(
                                                                 CharSequence s, int start,
                                                                 int before, int count) {
                                                             if (!"".equals(s.toString())) {
                                                                 btnPositive.setClickable(
                                                                         true);//按钮可点击
                                                                 btnPositive.setBackgroundResource(R
                                                                         .drawable.btn_yellow);
                                                             } else {
                                                                 btnPositive.setClickable(
                                                                         false);//按钮不可点击
                                                                 btnPositive.setBackgroundResource(R
                                                                         .drawable.bg_identify_code_press);
                                                             }
                                                         }

                                                         @Override
                                                         public void afterTextChanged(
                                                                 Editable s) {
                                                             setGivenName(s.toString()
                                                                     .trim());
                                                         }
                                                     }

            );

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
                                    String token = (String) SharedPrefUtil.getInstance()
                                            .getData(share_token, "空");
                                    if (token.equals("空")) {
                                        return -2;
                                    }
                                    String email = (String) SharedPrefUtil.getInstance()
                                            .getData(share_user_email, "空");

                                    //如果没有email
                                    if (token.equals("空")) {
                                        return -2;
                                    }
                                    final String familyName = getFamilyName().trim();
                                    final String givenName = getGivenName().trim();
                                    String oldFamilyName = (String) SharedPrefUtil.getInstance()
                                            .getData(share_family_name, "空");
                                    String oldGivenName = (String) SharedPrefUtil.getInstance()
                                            .getData(share_given_name, "空");
                                    if (oldFamilyName.equals("空") || oldGivenName.equals("空")) {
                                        return -2;
                                    }

                                    if (!isValidName(familyName)) {
                                        return WRONG_FORMAT_INPUT_NO1;
                                    }
                                    if (!isValidName(givenName)) {
                                        return WRONG_FORMAT_INPUT_NO2;
                                    }
                                    if (oldFamilyName.equals(familyName) &&
                                            oldGivenName.equals(givenName)) {
                                        return WRONG_FORMAT_INPUT_NO3;
                                    }

                                    /**
                                     * 3.上传数据
                                     */

                                    OkGo.post(URL_UPDATE_SETTINGS)
                                            .tag(this)
                                            .params(post_need_feature, update_name)
                                            .params(post_token, token)
                                            .params(post_user_email, email)
                                            .params(post_user_family_name, familyName)
                                            .params(post_user_given_name, givenName)
                                            .execute(new JsonCallback<Register>() {
                                                @Override
                                                public void onSuccess(Register o, Call call,
                                                                      Response response) {
                                                    if (o.getCode() == SUCCESS) {
                                                        /**
                                                         * 4.更改成功,缓存姓,名
                                                         */
                                                        SharedPrefUtil.getInstance()
                                                                .saveData(share_family_name,
                                                                        familyName);
                                                        SharedPrefUtil.getInstance()
                                                                .saveData(share_given_name,
                                                                        givenName);
                                                        //更新UI
                                                        mTvFamilyName.setText(familyName);
                                                        mTvGivenName.setText(givenName);

                                                        mlodingDialog.cancle();
                                                        /**
                                                         * 5.对话框消失,更新姓名
                                                         */
                                                        customAlertDialog.dismiss();


                                                    } else {
                                                        //提示所有错误
                                                        mlodingDialog.cancle();
                                                        showLog(o.getMsg());
                                                        showToast(mContext, o.getMsg());
                                                    }
                                                }

                                                @Override
                                                public void onError(Call call, Response response,
                                                                    Exception e) {
                                                    super.onError(call, response, e);
                                                    mlodingDialog.cancle();
                                                    showToast(mContext, R.string.system_error);
                                                }
                                            });


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
                                            showToast(mContext,
                                                    R.string.can_not_recognize_family_name);
                                            break;
                                        case WRONG_FORMAT_INPUT_NO2:
                                            showToast(mContext,
                                                    R.string.can_not_recognize_given_name);
                                            break;
                                        case WRONG_FORMAT_INPUT_NO3:
                                            showToast(mContext, R.string.no_change_alter);
                                            break;
                                        case -1:
                                            break;
                                        case -2:
                                            showToast(mContext, R.string.please_relogin);
                                            break;

                                        default:
//                                            showToast(mContext, R.string.system_error);
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
                RelativeLayout.LayoutParams layoutParams =
                        (RelativeLayout.LayoutParams) btnPositive
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