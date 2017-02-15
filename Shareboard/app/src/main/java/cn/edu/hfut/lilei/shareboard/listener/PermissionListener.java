package cn.edu.hfut.lilei.shareboard.listener;

import android.support.annotation.NonNull;


public interface PermissionListener {

    /**
     * 通过授权
     * @param permissions
     */
    void permissionGranted(@NonNull String[] permissions);

    /**
     * 拒绝授权
     * @param permissions
     */
    void permissionDenied(@NonNull String[] permissions);
}
