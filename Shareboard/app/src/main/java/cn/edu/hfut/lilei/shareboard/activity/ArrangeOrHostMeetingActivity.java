package cn.edu.hfut.lilei.shareboard.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import cn.edu.hfut.lilei.shareboard.R;


public class ArrangeOrHostMeetingActivity extends Activity {
    private EditText mEtEmail;
    private Button mBtnComplete;
    private LinearLayout mLlArrangeMeeting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arrange_host_meeting);
        init();
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

    private void init() {
        mLlArrangeMeeting = (LinearLayout) findViewById(R.id.ll_arrange_meeting);
        mLlArrangeMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(ArrangeOrHostMeetingActivity.this, ArrangeMeetingActivity.class);
                startActivity(intent);
            }
        });

    }
}
