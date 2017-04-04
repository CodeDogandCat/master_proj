package cn.edu.hfut.lilei.shareboard.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.lzy.okgo.OkGo;

import java.io.File;
import java.io.IOException;

import cn.carbs.android.avatarimageview.library.AvatarImageView;
import cn.edu.hfut.lilei.shareboard.R;
import cn.edu.hfut.lilei.shareboard.callback.JsonCallback;
import cn.edu.hfut.lilei.shareboard.listener.PermissionListener;
import cn.edu.hfut.lilei.shareboard.models.CommonJson;
import cn.edu.hfut.lilei.shareboard.models.RegisterJson;
import cn.edu.hfut.lilei.shareboard.utils.FileUtil;
import cn.edu.hfut.lilei.shareboard.utils.ImageUtil;
import cn.edu.hfut.lilei.shareboard.utils.NetworkUtil;
import cn.edu.hfut.lilei.shareboard.utils.PermissionsUtil;
import cn.edu.hfut.lilei.shareboard.utils.SettingUtil;
import cn.edu.hfut.lilei.shareboard.utils.SharedPrefUtil;
import cn.edu.hfut.lilei.shareboard.utils.StringUtil;
import cn.edu.hfut.lilei.shareboard.widget.customdialog.AlterHeadDialog;
import cn.edu.hfut.lilei.shareboard.widget.customdialog.LodingDialog;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import okhttp3.Call;
import okhttp3.Response;

import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.loding;
import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.showLog;
import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.showToast;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.ALBUM_REQUEST_CODE;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.CAMERA_REQUEST_CODE;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.CROP_REQUEST_CODE;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.IMG_PATH_FOR_CAMERA;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.IMG_PATH_FOR_CROP;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.NET_DISCONNECT;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.SUCCESS;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.URL_AVATAR;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.URL_SAVE_USR_INFO;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.WRONG_FORMAT_INPUT_NO1;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.WRONG_FORMAT_INPUT_NO2;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.WRONG_FORMAT_INPUT_NO3;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_user_avatar;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_user_email;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_user_family_name;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_user_given_name;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_user_login_password;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_avatar;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_family_name;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_given_name;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_token;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_user_email;


public class SetUserInfoActivity extends SwipeBackActivity {
    //控件
    private LinearLayout mLlSetAvatar;
    private AvatarImageView mPhoto;
    private EditText mEtFamilyName;
    private EditText mEtGivenName;
    private EditText mEtPassword;
    private Button mBtnComplete;
    private LodingDialog.Builder mlodingDialog;

    //数据
    private Uri cropUri;
    private File picture;
    private String avatarPath = "空";
    private String baseDir = "";


    //上下文参数
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_userinfo);
        init();


    }

    private void init() {
        mContext = this;
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            getWindow().setStatusBarColor(getResources().getColor(R.color.my_deepyellow));
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

        if (FileUtil.isExternalStorageWritable()) {
            baseDir = mContext.getExternalFilesDir("")
                    .getAbsolutePath();
        } else {
            baseDir = mContext.getFilesDir()
                    .getAbsolutePath();
        }
        mPhoto = (AvatarImageView) this.findViewById(R.id.img_set_user_info_avatar);
        mLlSetAvatar = (LinearLayout) findViewById(R.id.ll_set_user_info_avatar);
        mBtnComplete = (Button) findViewById(R.id.btn_setuserinfo_complete);
        mEtFamilyName = (EditText) findViewById(R.id.et_setuserinfo_family_name);
        mEtGivenName = (EditText) findViewById(R.id.et_setuserinfo_given_name);
        mEtPassword = (EditText) findViewById(R.id.et_setuserinfo_password);

        mLlSetAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestCamera();
            }
        });
        mBtnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mlodingDialog = loding(mContext, R.string.saving);
                final String familyName = mEtFamilyName.getText()
                        .toString()
                        .trim();
                final String givenName = mEtGivenName.getText()
                        .toString()
                        .trim();
                final String password = mEtPassword.getText()
                        .toString()
                        .trim();
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
                         * 2.检查用户输入格式
                         */

                        if (!StringUtil.isValidName(familyName)) {
                            //姓格式不对
                            return WRONG_FORMAT_INPUT_NO1;
                        }

                        if (!StringUtil.isValidName(givenName)) {
                            //名格式不对
                            return WRONG_FORMAT_INPUT_NO2;
                        }
                        if (!StringUtil.isValidPassword(password)) {
                            //密码格式不对
                            return WRONG_FORMAT_INPUT_NO3;
                        }
                        /**
                         * 3.构造参数
                         */
//                        showLog("加密前的密码：" + password);
                        String passEncrypted = StringUtil.getMD5(password);
                        if (passEncrypted == null) {
                            return -1;
                        }
//                        showLog("加密后的密码：" + passEncrypted);

                        String postEmail = (String) SharedPrefUtil.getInstance()
                                .getData(share_user_email, "空");
                        if (postEmail.equals("空")) {
                            return -2;
                        }
                        /**
                         * 4.上传
                         */
                        File avatarFile = new File(avatarPath);
                        if (!avatarPath.equals("空")) {


                            OkGo.post(URL_SAVE_USR_INFO)
                                    .tag(this)
                                    .isMultipart(true)
                                    .params(post_user_email, postEmail)
                                    .params(post_user_family_name, familyName)
                                    .params(post_user_given_name, givenName)
                                    .params(post_user_login_password, passEncrypted)
                                    .params(post_user_avatar, avatarFile)
                                    .execute(new JsonCallback<RegisterJson>() {
                                        @Override
                                        public void onSuccess(RegisterJson o, Call call,
                                                              Response response) {
                                            if (o.getCode() == SUCCESS) {
                                                /**
                                                 * 5.注册成功,缓存token ,姓,名
                                                 */
                                                SharedPrefUtil.getInstance()
                                                        .saveData(share_token, o.getData()
                                                                .getToken());
                                                SharedPrefUtil.getInstance()
                                                        .saveData(share_family_name, familyName);
                                                SharedPrefUtil.getInstance()
                                                        .saveData(share_given_name, givenName);
                                                SharedPrefUtil.getInstance()
                                                        .saveData(share_avatar, URL_AVATAR + o
                                                                .getData()
                                                                .getAvatar());
                                                mlodingDialog.cancle();
                                                /**
                                                 * 6.跳转
                                                 */
                                                Intent intent = new Intent();
                                                intent.setClass(SetUserInfoActivity.this,
                                                        MainActivity.class);
                                                startActivity(intent);
                                                finish();


                                            } else {
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
                                            mlodingDialog.cancle();
                                            showToast(mContext, R.string.system_error);
                                        }
                                    });
                        } else {
                            OkGo.post(URL_SAVE_USR_INFO)
                                    .tag(this)
                                    .params(post_user_email, postEmail)
                                    .params(post_user_family_name, familyName)
                                    .params(post_user_given_name, givenName)
                                    .params(post_user_login_password, passEncrypted)
                                    .execute(new JsonCallback<CommonJson>() {
                                        @Override
                                        public void onSuccess(CommonJson o, Call call,
                                                              Response response) {
                                            if (o.getCode() == SUCCESS) {
                                                /**
                                                 * 5.注册成功,缓存token ,姓,名
                                                 */
                                                SharedPrefUtil.getInstance()
                                                        .saveData(share_token, o.getMsg());
                                                SharedPrefUtil.getInstance()
                                                        .saveData(share_family_name, familyName);
                                                SharedPrefUtil.getInstance()
                                                        .saveData(share_given_name, givenName);

                                                mlodingDialog.cancle();
                                                /**
                                                 * 6.跳转
                                                 */
                                                Intent intent = new Intent();
                                                intent.setClass(SetUserInfoActivity.this,
                                                        MainActivity.class);
                                                startActivity(intent);
                                                finish();


                                            } else {
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
                                            mlodingDialog.cancle();
                                            showToast(mContext, R.string.system_error);
                                        }
                                    });
                        }

                        return -1;

                    }

                    @Override
                    protected void onPostExecute(Integer integer) {
                        super.onPostExecute(integer);
                        mlodingDialog.cancle();
                        switch (integer) {
                            case NET_DISCONNECT:
                                //弹出对话框，让用户开启网络
                                NetworkUtil.setNetworkMethod(mContext);
                                break;
                            case WRONG_FORMAT_INPUT_NO1:
                                //提示姓格式不对
                                showToast(mContext, R.string.can_not_recognize_family_name);
                                break;
                            case WRONG_FORMAT_INPUT_NO2:
                                //提示名格式不对
                                showToast(mContext, R.string.can_not_recognize_given_name);
                                break;
                            case WRONG_FORMAT_INPUT_NO3:
                                //提示登录密码格式不对
                                showToast(mContext, R.string.can_not_recognize_login_password);
                                break;
                            case -1:
                                break;
                            case -2:
                                showToast(mContext, R.string.please_relogin);
                                break;

                            default:
                                showToast(mContext, R.string.system_error);
                                break;
                        }
                    }
                }.execute();

            }
        });

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

    /**
     * 构造更改头像弹出窗口
     */
    private void createAlterHeadDialog() {


        //构造一个目标URI
        File cropImage = new File(baseDir,
                IMG_PATH_FOR_CROP);

        try {
            if (cropImage.exists()) {
                cropImage.delete();
            }
            cropImage.createNewFile();
            cropUri = Uri.fromFile(cropImage);
            new AlterHeadDialog.Builder(SetUserInfoActivity.this)
                    .setTitle(getString(R.string.choose_head))
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
            case ALBUM_REQUEST_CODE:
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
                picture = new File(baseDir, IMG_PATH_FOR_CAMERA);//拍照后保存的路径
                ImageUtil.startCrop(this, Uri.fromFile(picture),
                        cropUri, 200, 200);
                break;
            case CROP_REQUEST_CODE:
                Log.i(SettingUtil.TAG, "相册裁剪成功");
                //小米手机返回为空intent{}，所以不能用这种方法取得，用目标路径的uri取得
                Log.i(SettingUtil.TAG, "裁剪以后 [ " + data + " ]");

                Bitmap photo = null;
                try {

                    avatarPath = ImageUtil.getImageAbsolutePath19(mContext,
                            cropUri);
                    showLog(avatarPath);
                    ImageUtil.loadAvatarNoCache(mContext, cropUri, mPhoto);


                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }
}
