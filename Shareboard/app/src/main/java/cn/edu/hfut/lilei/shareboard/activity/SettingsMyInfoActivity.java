package cn.edu.hfut.lilei.shareboard.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lzy.okgo.OkGo;

import java.io.File;
import java.io.IOException;

import cn.carbs.android.avatarimageview.library.AvatarImageView;
import cn.edu.hfut.lilei.shareboard.R;
import cn.edu.hfut.lilei.shareboard.callback.JsonCallback;
import cn.edu.hfut.lilei.shareboard.listener.PermissionListener;
import cn.edu.hfut.lilei.shareboard.listener.TouchListener;
import cn.edu.hfut.lilei.shareboard.JsonEnity.RegisterJson;
import cn.edu.hfut.lilei.shareboard.utils.FileUtil;
import cn.edu.hfut.lilei.shareboard.utils.ImageUtil;
import cn.edu.hfut.lilei.shareboard.utils.NetworkUtil;
import cn.edu.hfut.lilei.shareboard.utils.PermissionsUtil;
import cn.edu.hfut.lilei.shareboard.utils.SettingUtil;
import cn.edu.hfut.lilei.shareboard.utils.SharedPrefUtil;
import cn.edu.hfut.lilei.shareboard.widget.customdialog.AddContactDialog;
import cn.edu.hfut.lilei.shareboard.widget.customdialog.AlterHeadDialog;
import cn.edu.hfut.lilei.shareboard.widget.customdialog.LodingDialog;
import cn.edu.hfut.lilei.shareboard.widget.customdialog.NameInputDialog;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import okhttp3.Call;
import okhttp3.Response;

import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.loding;
import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.showLog;
import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.showToast;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.CAMERA_REQUEST_CODE;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.CROP_REQUEST_CODE;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.IMG_PATH_FOR_CAMERA;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.IMG_PATH_FOR_CROP;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.NET_DISCONNECT;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.SUCCESS;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.URL_AVATAR;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.URL_UPDATE_SETTINGS;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_need_feature;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_token;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_user_avatar;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_user_email;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_avatar;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_family_name;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_given_name;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_token;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_user_email;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.update_avatar;


public class SettingsMyInfoActivity extends SwipeBackActivity {
    //控件
    private LinearLayout mLlAccount, mLlName, mLlLoginpassword, mLlLogout;
    private TextView mTvFamilyNameHint, mTvGivenNameHint;
    private TextView mTvName, mTvAvatar, mTvLoginPwd;
    private AvatarImageView mPhoto;
    private LodingDialog.Builder mlodingDialog;
    private ImageView next1, next2, next3;
    //数据
    private Uri cropUri;
    private boolean shouldCallUpdate = false;
    private String baseDir = "";
    private File srcFile, targetFile;

    //上下文参数
    private Context mContext;
    private ImageView mBtnBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_myinfo);
        showLog("create...");
        shouldCallUpdate = false;
        init();


    }

    private void update() {
        showLog("update ..................");
        /**
         * 重新读取缓存，更新姓名
         */
        mTvFamilyNameHint.setText((String) SharedPrefUtil.getInstance()
                .getData(share_family_name,
                        "未设置"));
        mTvGivenNameHint.setText((String) SharedPrefUtil.getInstance()
                .getData(share_given_name,
                        "未设置"));
        mPhoto.setTextAndColor((String) SharedPrefUtil.getInstance()
                .getData(share_given_name,
                        "未设置"), R.color.skyblue);
        ImageUtil.loadMyAvatar(mContext, mPhoto);
    }

    /**
     * 获取权限
     */
    private void requestCamera() {
        PermissionsUtil.TipInfo tip =
                new PermissionsUtil.TipInfo(null,
                        getString(R.string.should_get_this_to_alter_head), null,
                        null);

        if (PermissionsUtil.hasPermission(this, Manifest.permission.CAMERA) && PermissionsUtil
                .hasPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
                PermissionsUtil.hasPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            createAlterHeadDialog();

        } else {
            PermissionsUtil.requestPermission(this, new PermissionListener() {
                @Override
                public void permissionGranted(@NonNull String[] permissions) {
                    createAlterHeadDialog();
                }

                @Override
                public void permissionDenied(@NonNull String[] permissions) {
                }
            }, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE}, true, tip);
        }
    }

    private void init() {

        mContext = this;
        mBtnBack = (ImageView) findViewById(R.id.img_setmyinfo_goback);
        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.my_deepyellow));
        }
        if (FileUtil.isExternalStorageWritable()) {
            baseDir = mContext.getExternalFilesDir("")
                    .getAbsolutePath();
        } else {
            baseDir = mContext.getFilesDir()
                    .getAbsolutePath();
        }
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
        mPhoto = (AvatarImageView) this.findViewById(R.id.img_settingmyinfo_photo);
        next1 = (ImageView) this.findViewById(R.id.img_settingmyinfo_next1);
        next2 = (ImageView) this.findViewById(R.id.img_settingmyinfo_next2);
        next3 = (ImageView) this.findViewById(R.id.img_settingmyinfo_next3);
        mTvFamilyNameHint = (TextView) findViewById(R.id.tv_settingmyinfo_familyname);
        mTvGivenNameHint = (TextView) findViewById(R.id.tv_settingmyinfo_givenname);
        mTvAvatar = (TextView) findViewById(R.id.tv_settingmyinfo_avatar);
        mTvName = (TextView) findViewById(R.id.tv_settingmyinfo_name);
        mTvLoginPwd = (TextView) findViewById(R.id.tv_settingmyinfo_login_pwd);


        update();

        mLlAccount = (LinearLayout) findViewById(R.id.ll_settingmyinfo_account);
        mLlAccount.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        requestCamera();
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
                                .setTextView(mTvFamilyNameHint, mTvGivenNameHint)
                                .setPositiveButton(
                                        SettingsMyInfoActivity.this.getString(R.string.confirm),
                                        null)
                                .setNegativeButton(
                                        SettingsMyInfoActivity.this.getString(R.string.cancel),
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
        mLlLogout = (LinearLayout) findViewById(R.id.ll_settingmyinfo_logout);

        mLlLogout.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View view) {

                                             new AddContactDialog.Builder(mContext)
                                                     .setTitle(getString(R.string.logout_confirm))
                                                     .setPositiveButton(
                                                             getString(R.string.yes),
                                                             new DialogInterface.OnClickListener() {
                                                                 @Override
                                                                 public void onClick(DialogInterface dialogInterface, int i) {
                                                                     if (!SharedPrefUtil.getInstance()
                                                                             .deleteData(share_token)) {
                                                                         showToast(mContext, R.string.logout_failed);
                                                                     } else {
                                                                         Intent intent = new Intent();
                                                                         intent.setClass(SettingsMyInfoActivity.this,
                                                                                 LoginActivity.class);
                                                                         startActivity(intent);
                                                                         finish();
                                                                     }
                                                                 }
                                                             })
                                                     .setNegativeButton(
                                                             getString(R.string.no),
                                                             null)
                                                     .show();
                                         }
                                     }
        );


        new TouchListener.Builder(mContext).setLinearLayout(mLlAccount)
                .setTextView1(mTvAvatar)
                .setImageView(next1)
                .create();

        new TouchListener.Builder(mContext).setLinearLayout(mLlName)
                .setTextView1(mTvName)
                .setImageView(next2)
                .create();

        new TouchListener.Builder(mContext).setLinearLayout(mLlLoginpassword)
                .setTextView1(mTvLoginPwd)
                .setImageView(next3)
                .create();

        new TouchListener.Builder(mContext).setLinearLayout(mLlLogout)
                .create();


    }

    /**
     * 构造更改头像弹出窗口
     */
    private void createAlterHeadDialog() {
        //构造一个目标URI

        String baseDir = "";
        if (FileUtil.isExternalStorageWritable()) {
            baseDir = this.getExternalFilesDir("")
                    .getAbsolutePath() + "/shareboard/";
        } else {
            baseDir = this.getFilesDir()
                    .getAbsolutePath() + "/shareboard/";
        }

        File file = new File(baseDir,
                "image");//拍照后保存的路径

        if (!file.exists()) {
            file.mkdir();
        }
        File cropImage = new File(file.getAbsolutePath(),
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

    /**
     * 得到返回结果状态码，显示保存头像图片
     *
     * @param requestCode
     * @param resultCode  结果状态
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK)
            return;

        switch (requestCode) {
            case SettingUtil.ALBUM_REQUEST_CODE:
                Log.i(SettingUtil.TAG, "ALBUM_resultcode" + resultCode);
                Log.i(SettingUtil.TAG, "相册，开始裁剪");
                Log.i(SettingUtil.TAG, "相册 [ " + data + " ]");
                if (data == null) {
                    return;
                }
                ImageUtil.startCrop(this, data.getData(), cropUri, 200, 200);
                break;
            case CAMERA_REQUEST_CODE:
                Log.i(SettingUtil.TAG, "相机, 开始裁剪");
                File picture = new File(baseDir,
                        IMG_PATH_FOR_CAMERA);//拍照后保存的路径
                ImageUtil.startCrop(this, Uri.fromFile(picture),
                        cropUri, 200, 200);
                break;
            case CROP_REQUEST_CODE:
                Log.i(SettingUtil.TAG, "相册裁剪成功");
                //小米手机返回为空intent{}，所以不能用这种方法取得，用目标路径的uri取得
                Log.i(SettingUtil.TAG, "裁剪以后 [ " + data + " ]");
                mlodingDialog = loding(mContext, R.string.saving);

                final String avatarPath =
                        ImageUtil.getImageAbsolutePath19(mContext, cropUri);

                srcFile = new File(avatarPath);
                targetFile = new File(avatarPath.substring(0, avatarPath.lastIndexOf(".")
                ) + "compressed.jpeg");

                ImageUtil.compressImage(srcFile, targetFile, null, false);

//                final File avatarFile = new File(avatarPath);

                if (targetFile.length() > 1024 * 1024 * 6)// 6M  照片最大限制
                {
                    showToast(mContext, getString(R.string.image_too_large));
                } else {


                    new AsyncTask<Void, Void, Integer>() {

                        @Override
                        protected Integer doInBackground(Void... voids) {
                            /**
                             * 1.检查网络状态并提醒
                             */
                            if (!NetworkUtil.isNetworkConnected(mContext)) {
                                //网络连接不可用
                                return NET_DISCONNECT;
                            }
                            /**
                             * 2.构造参数
                             */


                            final String email = (String) SharedPrefUtil.getInstance()
                                    .getData(share_user_email, "空");
                            if (email.equals("空")) {
                                return -2;
                            }
                            final String token = (String) SharedPrefUtil.getInstance()
                                    .getData(share_token, "空");
                            if (token.equals("空")) {
                                return -2;
                            }
                            /**
                             *3.上传
                             */
                            showLog("ready...");
                            OkGo.post(URL_UPDATE_SETTINGS)
                                    .tag(this)
                                    .isMultipart(true)
                                    .params(post_need_feature, update_avatar)
                                    .params(post_user_email, email)
                                    .params(post_token, token)
                                    .params(post_user_avatar, targetFile)
                                    .execute(new JsonCallback<RegisterJson>() {
                                        @Override
                                        public void onSuccess(RegisterJson o, Call call,
                                                              Response response) {
                                            if (o.getCode() == SUCCESS) {
                                                /**
                                                 * 4.更新成功,显示
                                                 */
                                                targetFile.delete();
                                                SharedPrefUtil.getInstance()
                                                        .saveData(share_avatar, URL_AVATAR + o
                                                                .getData()
                                                                .getAvatar());
                                                mlodingDialog.cancle();
                                                update();
                                                showToast(mContext, o.getMsg());


                                            } else {
                                                targetFile.delete();
                                                mlodingDialog.cancle();
                                                //提示所有错误
                                                showLog(o.getMsg());
                                                showToast(mContext, o.getMsg());
                                            }
                                        }

                                        @Override
                                        public void onError(Call call, Response response,
                                                            Exception e) {
                                            super.onError(call, response, e);
                                            targetFile.delete();
                                            mlodingDialog.cancle();
                                            showToast(mContext, R.string.system_error);
                                        }
                                    });

                            return -1;
                        }

                        @Override
                        protected void onPostExecute(Integer integer) {
                            super.onPostExecute(integer);

                            targetFile.delete();
                            mlodingDialog.cancle();
                            switch (integer) {
                                case NET_DISCONNECT:
                                    //弹出对话框，让用户开启网络
                                    NetworkUtil.setNetworkMethod(mContext);
                                    break;
                                case -1:
                                    break;
                                case -2:
                                    showToast(mContext, R.string.please_relogin);
                                    break;

                                default:
//                                showToast(mContext, R.string.system_error);
                                    break;
                            }
                        }
//                try {
//                    photo = BitmapFactory.decodeStream(this.getContentResolver()
//                            .openInputStream(cropUri));
//                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                    photo.compress(Bitmap.CompressFormat.JPEG, 50, stream);// (0-100)压缩文件
//                    mPhoto.setImageBitmap(photo); //把图片显示在ImageView控件上


//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
                    }.execute();
                }
            default:
                break;
        }
    }
}
