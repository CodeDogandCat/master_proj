package cn.edu.hfut.lilei.shareboard.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import cn.edu.hfut.lilei.shareboard.R;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;


public class SetUserInfoActivity extends SwipeBackActivity {
    private EditText mEtEmail;
    private Button mBtnComplete;
    private SwipeBackLayout mSwipeBackLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_userinfo);
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
        mBtnComplete= (Button) findViewById(R.id.btn_setuserinfo_complete);
        mBtnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent();
//                intent.setClass(SetUserInfoActivity.this, ResetPasswordActivity.class);
//                startActivity(intent);
            }
        });

    }
}
