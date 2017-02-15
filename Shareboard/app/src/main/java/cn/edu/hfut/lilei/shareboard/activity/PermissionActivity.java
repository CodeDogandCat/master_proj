package cn.edu.hfut.lilei.shareboard.activity;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;

import java.io.Serializable;

import cn.edu.hfut.lilei.shareboard.listener.PermissionListener;
import cn.edu.hfut.lilei.shareboard.utils.PermissionsUtil;

import static cn.edu.hfut.lilei.shareboard.data.Config.PERMISSION_REQUEST_CANCEL;
import static cn.edu.hfut.lilei.shareboard.data.Config.PERMISSION_REQUEST_CODE;
import static cn.edu.hfut.lilei.shareboard.data.Config.PERMISSION_REQUEST_CONTENT;
import static cn.edu.hfut.lilei.shareboard.data.Config.PERMISSION_REQUEST_ENSURE;
import static cn.edu.hfut.lilei.shareboard.data.Config.PERMISSION_REQUEST_TITLE;

public class PermissionActivity extends Activity {

    //数据
    private boolean isRequireCheck;
    private boolean showTip;
    private String key;
    private String[] pers;
    private PermissionsUtil.TipInfo tipInfo;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent() == null || !getIntent().hasExtra("permission")) {
            finish();
            return;
        }

        isRequireCheck = true;
        pers = getIntent().getStringArrayExtra("permission");
        key = getIntent().getStringExtra("key");
        showTip = getIntent().getBooleanExtra("showTip", true);
        Serializable ser = getIntent().getSerializableExtra("tip");

        if (ser == null) {
            tipInfo = new PermissionsUtil.TipInfo(PERMISSION_REQUEST_TITLE,
                    PERMISSION_REQUEST_CONTENT, PERMISSION_REQUEST_CANCEL,
                    PERMISSION_REQUEST_ENSURE);
        } else {
            tipInfo = (PermissionsUtil.TipInfo) ser;
        }

    }


    /**
     * 请求权限兼容低版本
     *
     * @param permissions
     */
    private void requestPermissions(String... permissions) {
        ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
    }

    /**
     * 全部权限均已获取
     */
    private void allPermissionsGranted() {
        PermissionListener listener = PermissionsUtil.fetchListener(key);
        if (listener != null) {
            listener.permissionGranted(pers);
        }
        finish();
    }

    /**
     * 用户权限处理,
     * 如果全部获取, 则直接过.
     * 如果权限缺失, 则提示Dialog.
     *
     * @param requestCode  请求码
     * @param permissions  权限
     * @param grantResults 结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE &&
                PermissionsUtil.allPermissionsGranted(grantResults)) {
            allPermissionsGranted();
        } else
            if (showTip) {
                showMissingPermissionDialog();
            } else { //不需要提示用户
                denyPermit();
            }
    }

    /**
     * 显示缺失权限提示
     */
    private void showMissingPermissionDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(PermissionActivity.this);

        builder.setTitle(
                TextUtils.isEmpty(tipInfo.title) ? PERMISSION_REQUEST_TITLE : tipInfo.title);
        builder.setMessage(
                TextUtils.isEmpty(tipInfo.content) ? PERMISSION_REQUEST_CONTENT : tipInfo.content);

        builder.setNegativeButton(
                TextUtils.isEmpty(tipInfo.cancel) ? PERMISSION_REQUEST_CANCEL : tipInfo.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        denyPermit();
                    }
                });

        builder.setPositiveButton(
                TextUtils.isEmpty(tipInfo.ensure) ? PERMISSION_REQUEST_ENSURE : tipInfo.ensure,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startAppSettings();
                    }
                });

        builder.setCancelable(false);
        builder.show();

    }

    /**
     * 启动应用的设置
     */
    private void startAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    /**
     * 拒绝权限
     */
    private void denyPermit() {
        PermissionListener listener = PermissionsUtil.fetchListener(key);
        if (listener != null) {
            listener.permissionDenied(pers);
        }
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isRequireCheck) {
            if (PermissionsUtil.allPermissionGranted(this, pers)) {
                allPermissionsGranted();
            } else {
                requestPermissions(pers); // 请求权限,回调时会触发onResume
                isRequireCheck = false;
            }
        } else {
            isRequireCheck = true;
        }
    }

    protected void onDestroy() {
        super.onDestroy();
    }

}

