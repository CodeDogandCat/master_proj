package cn.edu.hfut.lilei.shareboard.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateTimeUtil {

    public static long millisSecondInHours(int hours) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.HOUR, hours);
        return c.getTimeInMillis();
    }

    public static long millisNow() {
        Calendar c = Calendar.getInstance();
        return c.getTimeInMillis();
    }


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


    /**
     * 由 聊天页面显示的时间
     *
     * @param startMillis
     * @return
     */
    public static String getChatDateTime(long startMillis) {

        Date date = new Date(startMillis);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);

        String preStr = sdf.format(date);
        sdf = new SimpleDateFormat("HH:mm", Locale.CHINA);
        String time = sdf.format(date);


        Calendar now = Calendar.getInstance();
        now.set(Calendar.HOUR_OF_DAY, 1);
        now.set(Calendar.MINUTE, 1);
        now.set(Calendar.SECOND, 1);
        now.set(Calendar.MILLISECOND, 1);
        long tmpNowMillis = now.getTimeInMillis();

        Calendar start = Calendar.getInstance();
        start.setTimeInMillis(startMillis);
        start.set(Calendar.HOUR_OF_DAY, 1);
        start.set(Calendar.MINUTE, 1);
        start.set(Calendar.SECOND, 1);
        start.set(Calendar.MILLISECOND, 1);
        long tmpStartMillis = start.getTimeInMillis();


        if (now.equals(start)) {
            preStr = "今天 " + time;
        } else {
            start.add(Calendar.DAY_OF_MONTH, 1);

            if (now.equals(start)) {
                preStr = "昨天 " + time;
            } else
                if (isThisWeek(startMillis)) {
                    start.setTimeInMillis(startMillis);
                    String mWay = String.valueOf(start.get(Calendar.DAY_OF_WEEK));
                    preStr = "星期" + DateTimeUtil.weekConvert(mWay) + " " + time;
                }


        }
        return preStr;
    }

    //判断选择的日期是否是本周
    public static boolean isThisWeek(long time) {
        Calendar calendar = Calendar.getInstance();
        int currentWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        calendar.setTime(new Date(time));
        int paramWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        if (paramWeek == currentWeek) {
            return true;
        }
        return false;
    }

    //判断选择的日期是否是今天
    public static boolean isToday(long time) {
        return isThisTime(time, "yyyy-MM-dd");
    }

    //判断选择的日期是否是本月
    public static boolean isThisMonth(long time) {
        return isThisTime(time, "yyyy-MM");
    }

    private static boolean isThisTime(long time, String pattern) {
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.CHINA);
        String param = sdf.format(date);//参数时间
        String now = sdf.format(new Date());//当前时间
        if (param.equals(now)) {
            return true;
        }
        return false;
    }

}
