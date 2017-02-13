package cn.edu.hfut.lilei.shareboard.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import cn.edu.hfut.lilei.shareboard.R;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;


public class ResetPasswordActivity extends SwipeBackActivity {
    private ImageView mImgEmail;
    private ImageView mImgPassword;
    private EditText mEtEmail;
    private EditText mEtPassword;
    private LinearLayout mLlBottomlineofemail;
    private LinearLayout mLlBottomlineofpass;
    private Button mBtnLogin;
    private SwipeBackLayout mSwipeBackLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();




    }

    private void init() {
        mSwipeBackLayout = getSwipeBackLayout();
        mSwipeBackLayout.setShadow(getResources().getDrawable(R.drawable.shadow), SwipeBackLayout.EDGE_LEFT);
        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
        mSwipeBackLayout.addSwipeListener(new SwipeBackLayout.SwipeListener() {
            @Override
            public void onScrollStateChange(int state, float scrollPercent) {

            }

            @Override
            public void onEdgeTouch(int edgeFlag) {
            }

            @Override
            public void onScrollOverThreshold() {
            }
        });
        mEtEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus) {

                    // 获得焦点
                    mLlBottomlineofemail.setBackgroundColor(getResources().getColor(R.color.my_yellow));
                    mImgEmail.setImageDrawable(getResources().getDrawable(R.drawable.ic_yellow_34));
                } else {

                    // 失去焦点
                    mLlBottomlineofemail.setBackgroundColor(getResources().getColor(R.color.my_lightgray));
                    mImgEmail.setImageDrawable(getResources().getDrawable(R.drawable.ic_white_34));
                }

            }


        });
    }
}
