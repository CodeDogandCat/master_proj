package cn.edu.hfut.lilei.shareboard.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.edu.hfut.lilei.shareboard.R;
import cn.edu.hfut.lilei.shareboard.listener.TouchListener;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

import static cn.edu.hfut.lilei.shareboard.R.id.ll_about_private;


public class SettingsAboutActivity extends SwipeBackActivity implements View.OnClickListener {
    //控件
    private LinearLayout mLlVersion, mLlFeedback, mLlRecommend, mLlPrivate;
    private TextView mTvVersion, mTvFeedBack, mTvRecommend, mTvPrivate, mTvCurrentVersion;
    private ImageView next1, next2, next3, next4;
    //数据
    //上下文参数
    private Context mContext;
    private ImageView mBtnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_about);
        init();

    }


    /**
     * 初始化
     */
    private void init() {
        mContext = this;
        mBtnBack = (ImageView) findViewById(R.id.img_about_goback);
        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.my_deepyellow));
        }
        //右滑返回
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
        mLlVersion = (LinearLayout) findViewById(R.id.ll_about_version_update);
        mLlFeedback = (LinearLayout) findViewById(R.id.ll_about_feedback);
        mLlRecommend = (LinearLayout) findViewById(R.id.ll_about_recommend);
        mLlPrivate = (LinearLayout) findViewById(ll_about_private);

        mTvVersion = (TextView) findViewById(R.id.tv_about_version);
        mTvCurrentVersion = (TextView) findViewById(R.id.tv_about_current_version);
        mTvFeedBack = (TextView) findViewById(R.id.tv_about_feedback);
        mTvRecommend = (TextView) findViewById(R.id.tv_about_recommend);
        mTvPrivate = (TextView) findViewById(R.id.tv_about_private);

        next1 = (ImageView) findViewById(R.id.img_about_next1);
        next2 = (ImageView) findViewById(R.id.img_about_next2);
        next3 = (ImageView) findViewById(R.id.img_about_next3);
        next4 = (ImageView) findViewById(R.id.img_about_next4);

        mLlVersion.setOnClickListener(this);
        mLlFeedback.setOnClickListener(this);
        mLlRecommend.setOnClickListener(this);
        mLlPrivate.setOnClickListener(this);


        new TouchListener.Builder(mContext).setLinearLayout(mLlVersion)
                .setTextView1(mTvVersion)
                .setImageView(next1)
                .create();
        new TouchListener.Builder(mContext).setLinearLayout(mLlFeedback)
                .setTextView1(mTvFeedBack)
                .setImageView(next2)
                .create();
        new TouchListener.Builder(mContext).setLinearLayout(mLlRecommend)
                .setTextView1(mTvRecommend)
                .setImageView(next3)
                .create();
        new TouchListener.Builder(mContext).setLinearLayout(mLlPrivate)
                .setTextView1(mTvPrivate)
                .setImageView(next4)
                .create();

    }


    /**
     * 设置监听器
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_about_private:
                startActivity(new Intent(mContext, PrivacyStrategyActivity.class));
                break;
        }
    }


}
