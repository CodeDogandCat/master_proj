package cn.edu.hfut.lilei.shareboard.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import cn.edu.hfut.lilei.shareboard.R;


public class ResetPasswordActivity extends Activity {
    private ImageView mImgEmail;
    private ImageView mImgPassword;
    private EditText mEtEmail;
    private EditText mEtPassword;
    private LinearLayout mLlBottomlineofemail;
    private LinearLayout mLlBottomlineofpass;
    private Button mBtnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        mEtEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus) {

                    // 获得焦点
                    mLlBottomlineofemail.setBackgroundColor(getResources().getColor(R.color.yellow));
                    mImgEmail.setImageDrawable(getResources().getDrawable(R.drawable.ic_yellow_34));
                } else {

                    // 失去焦点
                    mLlBottomlineofemail.setBackgroundColor(getResources().getColor(R.color.lightgray));
                    mImgEmail.setImageDrawable(getResources().getDrawable(R.drawable.ic_white_34));
                }

            }


        });

    }
}
