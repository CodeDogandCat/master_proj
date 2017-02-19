package cn.edu.hfut.lilei.shareboard.utils;

import java.util.Calendar;

public class DateTimeUtil {
    public static int zeroConvert(int hour_12) {
        int result;
        if (hour_12 == 0) {
            result = 12;
        } else {
            result = hour_12;
        }
        return result;
    }

    public static long minuteLength(long startMillis, long endMillis) {
        return ((endMillis - startMillis) / (60 * 1000));
    }

    public static String addZero(int minute) {
        String result;
        result = "" + ((minute < 10) ? ("0" + minute) : minute);
        return result;
    }

    /**
     * 由毫秒使时间获得是否为今天，明天，还是一般日期
     *
     * @param startMillis
     * @return
     */
    public static String getPreString(long startMillis) {
        String preStr;
        Calendar now = Calendar.getInstance();
        now.set(Calendar.HOUR_OF_DAY, 1);
        now.set(Calendar.MINUTE, 1);
        now.set(Calendar.SECOND, 1);
        now.set(Calendar.MILLISECOND, 1);
//        Log.i("now_hour", now.get(Calendar.HOUR_OF_DAY) + "");
        long tmpNowMillis = now.getTimeInMillis();

        Calendar start = Calendar.getInstance();
        start.setTimeInMillis(startMillis);
        start.set(Calendar.HOUR_OF_DAY, 1);
        start.set(Calendar.MINUTE, 1);
        start.set(Calendar.SECOND, 1);
        start.set(Calendar.MILLISECOND, 1);
//        Log.i("start_hour", start.get(Calendar.HOUR_OF_DAY) + "");
        long tmpStartMillis = start.getTimeInMillis();

//        Log.i("now_millis", tmpNowMillis + "");
//        Log.i("start_millis", tmpStartMillis + "");

        if (now.equals(start)) {
            preStr = "今天,";
        } else {
            now.add(Calendar.DAY_OF_MONTH, 1);

            if (now.equals(start)) {
                preStr = "明天,";
            } else {
                start.setTimeInMillis(startMillis);
                String mWay = String.valueOf(start.get(Calendar.DAY_OF_WEEK));
                preStr = start.get(Calendar.YEAR) + "年" + (start.get(Calendar.MONTH) + 1) + "月" +
                        start.get(Calendar.DAY_OF_MONTH) + "日" + "周" +
                        DateTimeUtil.weekConvert(mWay) + ",";
            }
        }
        return preStr;
    }

    public static String weekConvert(String mWay) {

        if ("1".equals(mWay)) {
            mWay = "日";
        } else
            if ("2".equals(mWay)) {
                mWay = "一";
            } else
                if ("3".equals(mWay)) {
                    mWay = "二";
                } else
                    if ("4".equals(mWay)) {
                        mWay = "三";
                    } else
                        if ("5".equals(mWay)) {
                            mWay = "四";
                        } else
                            if ("6".equals(mWay)) {
                                mWay = "五";
                            } else
                                if ("7".equals(mWay)) {
                                    mWay = "六";
                                }
        return mWay;
    }

}
