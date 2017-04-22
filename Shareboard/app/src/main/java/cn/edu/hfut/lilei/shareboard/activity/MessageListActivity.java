package cn.edu.hfut.lilei.shareboard.activity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import cn.edu.hfut.lilei.shareboard.R;
import cn.edu.hfut.lilei.shareboard.adapter.MsgAdapter;
import cn.edu.hfut.lilei.shareboard.greendao.entity.Msg;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;


public class MessageListActivity extends SwipeBackActivity {
    //控件
    private static final String TAG = "shareboard";
    private RecyclerView mRv;
    private MsgAdapter mAdapter;
    private LinearLayoutManager mManager;
    private List<Msg> mDatas = new ArrayList<>();


    //上下文参数
    private Context mContext;
    private ImageView mBtnBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);
        init();


    }


    /**
     * 初始化
     */
    private void init() {
        mContext = this;
        mBtnBack = (ImageView) findViewById(R.id.img_message_list_goback);
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

        mAdapter = new MsgAdapter(this, mDatas);
        mRv.setAdapter(mAdapter);


    }


    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {

            }
        }
    };


}
