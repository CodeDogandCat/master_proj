package cn.edu.hfut.lilei.shareboard.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import cn.edu.hfut.lilei.shareboard.R;
import cn.edu.hfut.lilei.shareboard.utils.ImageUtil;


public class LoginActivity extends Activity {
    //控件
    private LinearLayout mLlBottomlineofemail;
    private LinearLayout mLlBottomlineofpass;
    private ImageView mImgEmail;
    private ImageView mImgPassword;
    private EditText mEtEmail;
    private EditText mEtPassword;
    private Button mBtnLogin;
    private Button mBtnRegisteraccount;
    private Button mBtnResetpassword;
    //数据

    //上下文参数
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();

    }

    private void init() {
        mContext = this;
        mImgEmail = (ImageView) findViewById(R.id.img_login_email);
        mImgPassword = (ImageView) findViewById(R.id.img_login_password);
        mEtEmail = (EditText) findViewById(R.id.et_login_email);
        mEtPassword = (EditText) findViewById(R.id.et_login_password);
        mLlBottomlineofemail = (LinearLayout) findViewById(R.id.ll_login_bottomlineofemail);
        mLlBottomlineofpass = (LinearLayout) findViewById(R.id.ll_login_bottomlineofpass);
        mBtnRegisteraccount = (Button) findViewById(R.id.btn_login_registeraccount);
        mBtnResetpassword = (Button) findViewById(R.id.btn_login_resetpassword);

        mEtEmail.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus) {

                    // 获得焦点
                    mLlBottomlineofemail.setBackgroundColor(
                            getResources().getColor(R.color.my_yellow));
                    ImageUtil.load(mContext, R.drawable.ic_yellow_34, mImgEmail);
                } else {

                    // 失去焦点
                    mLlBottomlineofemail.setBackgroundColor(
                            getResources().getColor(R.color.my_lightgray));
                    ImageUtil.load(mContext, R.drawable.ic_white_34, mImgEmail);
                }

            }


        });
        mEtPassword.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus) {

                    // 获得焦点
                    mLlBottomlineofpass.setBackgroundColor(
                            getResources().getColor(R.color.my_yellow));
                    ImageUtil.load(mContext, R.drawable.ic_yellow_32, mImgPassword);
                } else {

                    // 失去焦点
                    mLlBottomlineofpass.setBackgroundColor(
                            getResources().getColor(R.color.my_lightgray));
                    ImageUtil.load(mContext, R.drawable.ic_white_32, mImgPassword);
                }

            }


        });
        mBtnRegisteraccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        mBtnResetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, ResetPasswordActivity.class);
                startActivity(intent);
            }
        });

    }
}
