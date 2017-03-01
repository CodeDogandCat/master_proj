package cn.edu.hfut.lilei.shareboard.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import cn.edu.hfut.lilei.shareboard.R;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;


public class ArrangeOrHostMeetingActivity extends SwipeBackActivity {
    //控件
    private LinearLayout mLlArrangeMeeting;
    private EditText mEtEmail;
    private Button mBtnComplete;
    //数据

    //上下文参数
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arrange_host_meeting);
        init();


    }

    private void init() {
        mContext = this;
        mLlArrangeMeeting = (LinearLayout) findViewById(R.id.ll_arrange_meeting);
        mLlArrangeMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(ArrangeOrHostMeetingActivity.this, ArrangeMeetingActivity.class);
                startActivity(intent);
            }
        });
        SwipeBackLayout mSwipeBackLayout = getSwipeBackLayout();
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
    }

    @Override
    protected void onResume() {
        super.onResume();

//        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);


    }

    private void vibrate(long duration) {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {
                0, duration
        };
        vibrator.vibrate(pattern, -1);
    }

}
