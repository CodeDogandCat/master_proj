package cn.edu.hfut.lilei.shareboard.utils;


import android.os.Environment;

import java.io.File;

import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.showLog;

public class FileUtil {
    /**
     * Checks if external storage is available for read and write
     *
     * @return
     */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     * Checks if external storage is available to at least read
     *
     * @return
     */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     *
     */
    public static void createSystemDir() {
        File destDir = new File("");
        if (!destDir.exists()) {
            if (!destDir.mkdirs()) {
                showLog("创建文件夹失败");
            }
        }

    }

    /**
     * 判断文件是否存在
     *
     * @param strFile
     * @return
     */
    public static boolean fileIsExists(String strFile) {
        try {
            File f = new File(strFile);
            if (!f.exists()) {
                return false;
            }

        } catch (Exception e) {
            return false;
        }

        return true;
    }


}
