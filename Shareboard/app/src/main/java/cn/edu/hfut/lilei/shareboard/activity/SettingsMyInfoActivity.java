package cn.edu.hfut.lilei.shareboard.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.edu.hfut.lilei.shareboard.R;
import cn.edu.hfut.lilei.shareboard.view.NameInputDialog;


public class SettingsMyInfoActivity extends Activity {
    private Button mBtnComplete;
    private LinearLayout mLlAccount, mLlName, mLlLoginpassword, mLlLogout;
    private TextView mTvFamilyNameHint, mTvGivenNameHint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_myinfo);
        init();


    }

    private void init() {
        mTvFamilyNameHint = (TextView) findViewById(R.id.tv_settingmyinfo_familyname);
        mTvGivenNameHint = (TextView) findViewById(R.id.tv_settingmyinfo_givenname);

        mLlName = (LinearLayout) findViewById(R.id.ll_settingmyinfo_name);
        mLlName.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new NameInputDialog.Builder(SettingsMyInfoActivity.this)
                                .setTitle(getString(R.string.please_input_your_name))
                                .setFamilyName(mTvFamilyNameHint.getText().toString())
                                .setGivenName(mTvGivenNameHint.getText().toString())
                                .setPositiveButton(SettingsMyInfoActivity.this.getString(R.string.cancel), null)
                                .setNegativeButton(SettingsMyInfoActivity.this.getString(R.string.confirm), null)
                                .show();


                    }
                }
        );
        mLlLoginpassword = (LinearLayout) findViewById(R.id.ll_settingmyinfo_loginpassword);
        mLlLoginpassword.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(SettingsMyInfoActivity.this, AlterPasswordActivity.class);
                        startActivity(intent);

                    }
                }
        );
        //        mBtnComplete= (Button) findViewById(R.id.btn_setuserinfo_complete);
//        mBtnComplete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                Intent intent = new Intent();
////                intent.setClass(SetUserInfoActivity.this, ResetPasswordActivity.class);
////                startActivity(intent);
//            }
//        });
    }
}
