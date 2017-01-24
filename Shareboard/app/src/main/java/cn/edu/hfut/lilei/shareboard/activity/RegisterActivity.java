package cn.edu.hfut.lilei.shareboard.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import cn.edu.hfut.lilei.shareboard.R;


public class RegisterActivity extends Activity {
    private EditText mEtEmail;
    private Button mBtnNextstep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mBtnNextstep= (Button) findViewById(R.id.btn_register_nextstep);
        mBtnNextstep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(RegisterActivity.this, SetUserInfoActivity.class);
                startActivity(intent);
            }
        });



    }
}
