package cn.edu.hfut.lilei.shareboard.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import cn.edu.hfut.lilei.shareboard.R;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;


public class JoinMeetingActivity extends SwipeBackActivity {
    //控件
    private EditText mEtMeetingNumber;
    //    private TextView mTvExchangeType;
    private EditText mEtMeetingPassword;
    private Button mBtnJoinMeeting;
    //数据
    private Boolean mJoinByNumber = true;
    //上下文参数
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_meeting);
        init();
    }

    private void init() {
        mContext = this;
        SwipeBackLayout mSwipeBackLayout = getSwipeBackLayout();
        mSwipeBackLayout.setShadow(getResources().getDrawable(R.drawable.shadow),
                SwipeBackLayout.EDGE_LEFT);
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
        mBtnJoinMeeting = (Button) findViewById(R.id.btn_join_meeting);
//        mTvExchangeType = (TextView) findViewById(R.id.tv_exchange_jointype);
        mEtMeetingNumber = (EditText) findViewById(R.id.et_meeting_number);
        mEtMeetingPassword = (EditText) findViewById(R.id.et_meeting_password);
        mBtnJoinMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(JoinMeetingActivity.this, "加入会议", Toast.LENGTH_SHORT)
                        .show();
                Intent intent = new Intent();
                intent.setClass(JoinMeetingActivity.this, MeetingActivity.class);
                startActivity(intent);
            }
        });
//        mTvExchangeType.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (mJoinByNumber) {
//                    mEtMeetingNumber.setHint(R.string.meeting_name);
//                    mTvExchangeType.setText(R.string.join_meeting_by_meeting_number);
//                    mJoinByNumber = false;
//                } else {
//                    mEtMeetingNumber.setHint(R.string.meeting_number);
//                    mTvExchangeType.setText(R.string.join_meeting_by_meeting_name);
//                    mJoinByNumber = true;
//                }
//
//            }
//        });
    }
}
