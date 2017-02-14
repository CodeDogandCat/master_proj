package cn.edu.hfut.lilei.shareboard.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import cn.carbs.android.avatarimageview.library.AvatarImageView;
import cn.edu.hfut.lilei.shareboard.R;
import cn.edu.hfut.lilei.shareboard.utils.MyAppUtil;
import cn.edu.hfut.lilei.shareboard.view.AlterHeadDialog;
import cn.edu.hfut.lilei.shareboard.view.NameInputDialog;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

import static cn.edu.hfut.lilei.shareboard.data.Config.ALBUM_REQUEST_CODE;
import static cn.edu.hfut.lilei.shareboard.data.Config.CAMERA_REQUEST_CODE;
import static cn.edu.hfut.lilei.shareboard.data.Config.CROP_REQUEST_CODE;
import static cn.edu.hfut.lilei.shareboard.data.Config.IMG_PATH_FOR_CROP;
import static cn.edu.hfut.lilei.shareboard.data.Config.PERMISSIONS_REQUEST;


public class SettingsMyInfoActivity extends SwipeBackActivity {
    private LinearLayout mLlAccount, mLlName, mLlLoginpassword, mLlLogout;
    private TextView mTvFamilyNameHint, mTvGivenNameHint;
    private AvatarImageView mPhoto;
    private SwipeBackLayout mSwipeBackLayout;
    private static final String TAG = "MyActivity";
    private Uri cropUri;
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_myinfo);
        init();


    }

    private void getPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                (checkSelfPermission(Manifest.permission.CAMERA) !=
                        PackageManager.PERMISSION_GRANTED ||
                        checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                                PackageManager.PERMISSION_GRANTED ||
                        checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) !=
                                PackageManager.PERMISSION_GRANTED)) {
            requestPermissions(new String[]{Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                init();
            }
        }
    }

    private void init() {

        context = this;
        mSwipeBackLayout = getSwipeBackLayout();
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
        mPhoto = (AvatarImageView) this.findViewById(R.id.img_settingmyinfo_photo);
        mPhoto.setTextAndColor("磊", R.color.skyblue);


        mTvFamilyNameHint = (TextView) findViewById(R.id.tv_settingmyinfo_familyname);
        mTvGivenNameHint = (TextView) findViewById(R.id.tv_settingmyinfo_givenname);

        mLlAccount = (LinearLayout) findViewById(R.id.ll_settingmyinfo_account);
        mLlAccount.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getPermission();
                        //构造一个目标URI
                        File cropImage = new File(Environment.getExternalStorageDirectory(),
                                IMG_PATH_FOR_CROP);
                        try {
                            if (cropImage.exists()) {
                                cropImage.delete();
                            }
                            cropImage.createNewFile();
                            cropUri = Uri.fromFile(cropImage);
                            new AlterHeadDialog.Builder(SettingsMyInfoActivity.this)
                                    .setTitle(getString(R.string.alter_head))
                                    .show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
        );
        mLlName = (LinearLayout) findViewById(R.id.ll_settingmyinfo_name);
        mLlName.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new NameInputDialog.Builder(SettingsMyInfoActivity.this)
                                .setTitle(getString(R.string.please_input_your_name))
                                .setFamilyName(mTvFamilyNameHint.getText()
                                        .toString())
                                .setGivenName(mTvGivenNameHint.getText()
                                        .toString())
                                .setPositiveButton(
                                        SettingsMyInfoActivity.this.getString(R.string.cancel),
                                        null)
                                .setNegativeButton(
                                        SettingsMyInfoActivity.this.getString(R.string.confirm),
                                        null)
                                .show();


                    }
                }
        );
        mLlLoginpassword = (LinearLayout) findViewById(R.id.ll_settingmyinfo_loginpassword);
        mLlLoginpassword.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(SettingsMyInfoActivity.this, AlterPasswordActivity.class);
                        startActivity(intent);

                    }
                }
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK)
            return;

        switch (requestCode) {
            case ALBUM_REQUEST_CODE:
                Log.i(TAG, "ALBUM_resultcode" + resultCode);
                Log.i(TAG, "相册，开始裁剪");
                Log.i(TAG, "相册 [ " + data + " ]");
                if (data == null) {
                    return;
                }
                MyAppUtil.startCrop(this, data.getData(), cropUri,120,120);
                break;
            case CAMERA_REQUEST_CODE:
                Log.i(TAG, "相机, 开始裁剪");
                File picture = new File(Environment.getExternalStorageDirectory()
                        + "/temp.jpg");//拍照后保存的路径
                MyAppUtil.startCrop(this, Uri.fromFile(picture), cropUri,120,120);
                break;
            case CROP_REQUEST_CODE:
                Log.i(TAG, "相册裁剪成功");
                //小米手机返回为空intent{}，所以不能用这种方法取得，用目标路径的uri取得
                Log.i(TAG, "裁剪以后 [ " + data + " ]");

                Bitmap photo = null;
                try {
                    photo = BitmapFactory.decodeStream(this.getContentResolver()
                            .openInputStream(cropUri));
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    photo.compress(Bitmap.CompressFormat.JPEG, 50, stream);// (0-100)压缩文件
                    //此处可以把Bitmap保存到sd卡中，具体看：http://www.cnblogs.com/linjiqin/archive/2011/12/28/2304940.html
                    mPhoto.setImageBitmap(photo); //把图片显示在ImageView控件上
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }
}
