package cn.edu.hfut.lilei.shareboard.widget.customdialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.edu.hfut.lilei.shareboard.R;
import cn.edu.hfut.lilei.shareboard.utils.StringUtil;

import static cn.edu.hfut.lilei.shareboard.R.id.tvAlertDialogMessage;
import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.loding;
import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.showToast;

public class    UrlInputDialog extends Dialog {


    public UrlInputDialog(Context context) {
        super(context);
    }

    public UrlInputDialog(Context context, int themeId) {
        super(context, themeId);
    }

    public static class Builder {
        private Context mContext;
        private String mTitle, mMessage, mHint;
        private String mPositiveButtonText, mNegativeButtonText;
        private WebView mWvShareWeb;
        private LodingDialog.Builder mlodingDialog;
        private IUrlInput mCallback;

        private OnClickListener mPositiveButtonClickListener,
                mNegativeButtonClickListener;

        public interface IUrlInput {
            void openWebSuccess();

            void openWebError();
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

        public Builder setCallBack(IUrlInput Callback) {
            mCallback = Callback;
            return this;
        }

        public Builder setShareWeb(WebView shareWeb) {
            mWvShareWeb = shareWeb;
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

        @SuppressLint("SetJavaScriptEnabled")
        public UrlInputDialog create() {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.dialog_common_input, null);
            final UrlInputDialog addContactDialog = new UrlInputDialog(
                    mContext, R.style.CustomAlertDialog);
            addContactDialog.addContentView(view, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            TextView tvAlertTitle = (TextView) view
                    .findViewById(R.id.tvAlertDialogTitle);
            tvAlertTitle.setText(mTitle);
            final EditText etMessage = (EditText) view
                    .findViewById(tvAlertDialogMessage);


            if (!TextUtils.isEmpty(mHint)) {
                etMessage.setVisibility(View.VISIBLE);
                etMessage.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
                etMessage.setText(R.string.http);
                etMessage.setHint(mHint);
            }
            /**
             * 设置webview
             */
            WebSettings webSettings = mWvShareWeb.getSettings();
            webSettings.setSaveFormData(false);
            webSettings.setJavaScriptEnabled(true);
            //下面2行用来支持缩放
            webSettings.setSupportZoom(true);
            webSettings.setBuiltInZoomControls(true);
            //隐藏缩放控制条
            webSettings.setDisplayZoomControls(false);
            webSettings.setUseWideViewPort(true);
            webSettings.setLoadWithOverviewMode(true);
            webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
            webSettings.setAppCacheEnabled(false);
            webSettings.setDefaultTextEncodingName("utf-8");
            WebView.setWebContentsDebuggingEnabled(true);

            mWvShareWeb.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
            mWvShareWeb.setWebViewClient(new WebViewClient() {
                int times = 0;

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    // TODO Auto-generated method stub
                    return false;
                }


                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    if (mlodingDialog != null) {
                        mlodingDialog.cancle();
                    }
                    if (times == 0) {
                        //调用回调
                        mCallback.openWebSuccess();
                    }
                    times = 1;

                }

                @Override
                public void onReceivedError(WebView view, int errorCode,
                                            String description,
                                            String failingUrl) {
                    super.onReceivedError(view, errorCode, description, failingUrl);
                    if (times == 0) {
                        //调用回调
                        mCallback.openWebError();
                    }
                    times = 1;


                }
            });

            Button btnPositive = (Button) view
                    .findViewById(R.id.btnAlertDialogPositive);
            if (!TextUtils.isEmpty(mPositiveButtonText)) {
                btnPositive.setText(mPositiveButtonText);
                if (mPositiveButtonClickListener == null) {
                    btnPositive.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //检查url合法性
                            String url = etMessage.getText()
                                    .toString();
                            if (StringUtil.isValidUrl(url)) {
                                addContactDialog.dismiss();
                                mlodingDialog = loding(mContext, R.string.loding);
                                mWvShareWeb.loadUrl(url);
                            } else {
                                showToast(mContext, mContext.getString(R.string.invalid_web_url));
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

        public UrlInputDialog show() {
            UrlInputDialog dialog = create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            return dialog;
        }
    }

}