package cn.edu.hfut.lilei.shareboard.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import cn.edu.hfut.lilei.shareboard.R;


public class AlterPasswordActivity extends Activity {
    private Button mBtnSave;

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
    }
}
