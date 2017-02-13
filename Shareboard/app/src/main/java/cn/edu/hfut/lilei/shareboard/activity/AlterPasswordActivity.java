package cn.edu.hfut.lilei.shareboard.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import cn.edu.hfut.lilei.shareboard.R;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;


public class AlterPasswordActivity extends SwipeBackActivity {
    private Button mBtnSave;
    private SwipeBackLayout mSwipeBackLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_myinfo_alterpassword);
        init();


    }

    private void init() {
        mBtnSave = (Button) findViewById(R.id.btn_alterpassword_save);
        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(AlterPasswordActivity.this, "保存", Toast.LENGTH_SHORT).show();
            }
        });
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
    }
}
