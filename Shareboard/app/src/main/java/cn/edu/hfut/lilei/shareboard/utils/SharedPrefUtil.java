package cn.edu.hfut.lilei.shareboard.utils;

import android.content.Context;
import android.content.SharedPreferences;

import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.PACKAGE_NAME;


public class SharedPrefUtil {
    private static final String FILE_NAME = PACKAGE_NAME;
    private static SharedPreferences mSharedPreferences;// 单例
    private static SharedPrefUtil instance;// 单例

    private SharedPrefUtil(Context context) {
        mSharedPreferences = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
    }

    /**
     * 初始化单例
     *
     * @param context
     */
    public static synchronized void init(Context context) {
        if (instance == null) {
            instance = new SharedPrefUtil(context);
        }
    }

    /**
     * 获取单例
     *
     * @return
     */
    public static SharedPrefUtil getInstance() {
        if (instance == null) {
            throw new RuntimeException("class should init!");
        }
        return instance;
    }

    /**
     * 保存数据
     *
     * @param key
     * @param data
     */
    public void saveData(String key, Object data) {
        String type = data.getClass()
                .getSimpleName();

        SharedPreferences.Editor editor = mSharedPreferences.edit();

        if ("Integer".equals(type)) {
            editor.putInt(key, (Integer) data);
        } else
            if ("Boolean".equals(type)) {
                editor.putBoolean(key, (Boolean) data);
            } else
                if ("String".equals(type)) {
                    editor.putString(key, (String) data);
                } else
                    if ("Float".equals(type)) {
                        editor.putFloat(key, (Float) data);
                    } else
                        if ("Long".equals(type)) {
                            editor.putLong(key, (Long) data);
                        }

        editor.apply();
    }

    /**
     * 得到数据
     *
     * @param key
     * @param defValue
     * @return
     */
    public Object getData(String key, Object defValue) {

        String type = defValue.getClass()
                .getSimpleName();
        if ("Integer".equals(type)) {
            return mSharedPreferences.getInt(key, (Integer) defValue);
        } else
            if ("Boolean".equals(type)) {
                return mSharedPreferences.getBoolean(key, (Boolean) defValue);
            } else
                if ("String".equals(type)) {
                    return mSharedPreferences.getString(key, (String) defValue);
                } else
                    if ("Float".equals(type)) {
                        return mSharedPreferences.getFloat(key, (Float) defValue);
                    } else
                        if ("Long".equals(type)) {
                            return mSharedPreferences.getLong(key, (Long) defValue);
                        }

        return null;
    }

    /**
     * 删除指定数据
     *
     * @param strName
     * @return
     */
    public static Boolean deleteData(String strName) {
        return mSharedPreferences.edit()
                .remove(strName)
                .commit();
    }

    /**
     * 清空所有数据
     *
     * @param strName
     * @return
     */
    public static Boolean clearData(String strName) {
        return mSharedPreferences.edit()
                .clear()
                .commit();
    }
}
