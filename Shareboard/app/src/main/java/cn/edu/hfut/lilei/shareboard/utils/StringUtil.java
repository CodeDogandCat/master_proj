package cn.edu.hfut.lilei.shareboard.utils;

import org.apache.commons.lang.StringUtils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;

public class StringUtil {
    /**
     * 截取一段字符的长度,不区分中英文,如果数字不正好，则少取一个字符位
     *
     * @param origin    原始字符串
     * @param begin     截取开始位置(一个汉字长度按2算的)
     * @param end       截取结束位置(一个汉字长度按2算的)
     * @param appendStr 待添加字符串
     * @param encoding  编码格式,默认GBK
     * @return 返回的字符串
     */
    public static String substring(String origin, int begin, int end, String appendStr,
                                   String encoding) {
        if (origin == null || origin.equals("")) {
            return appendStr;
        }
        if (begin < 0) {
            begin = 0;
        }
        if (end < 0) {
            return "";
        }
        if (begin > end) {
            return "";
        }
        if (begin == end) {
            return "";
        }
        if (begin > length(origin)) {
            return "";
        }
        if (end > length(origin)) {
            end = length(origin) - 1;
        }
        if (StringUtils.isBlank(encoding) || StringUtils.isEmpty(encoding)) {
            encoding = "GBK";
        }
        int len = end - begin;
        byte[] strByte = new byte[len];
        try {
            System.arraycopy(origin.getBytes(encoding), begin, strByte, 0, len);
            int count = 0;
            for (int i = 0; i < len; i++) {
                int value = (int) strByte[i];
                if (value < 0) {
                    count++;
                }
            }
            if (count % 2 != 0) {
                len = (len == 1) ? ++len : --len;
            }
            return new String(strByte, 0, len, encoding) + appendStr;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (StringIndexOutOfBoundsException ex) {
            return appendStr;
        } catch (Exception ex) {
            return "";
        }
    }

    /**
     * 判断一个字符是Ascill字符还是其它字符（如汉，日，韩文字符）
     *
     * @param c 需要判断的字符
     * @return 返回true, Ascill字符
     */
    public static boolean isLetter(char c) {
        int k = 0x80;
        return c / k == 0 ? true : false;
    }

    /**
     * 得到一个字符串的长度,显示的长度,一个汉字或日韩文长度为2,英文字符长度为1
     *
     * @param s 需要得到长度的字符串
     * @return i得到的字符串长度
     */
    public static int length(String s) {
        if (s == null) {
            return 0;
        }
        char[] c = s.toCharArray();
        int len = 0;
        for (int i = 0; i < c.length; i++) {
            len++;
            //如果为汉，日，韩，则多加一位
            if (!isLetter(c[i])) {
                len++;
            }
        }
        return len;
    }


    /******************************* Second Method ****************************/

    /**
     * 截取一段字符的长度(汉、日、韩文字符长度为2),不区分中英文,如果数字不正好，则少取一个字符位
     *
     * @param str                原始字符串
     * @param srcPos             开始位置
     * @param specialCharsLength 截取长度(汉、日、韩文字符长度为2)
     * @return
     */
    public static String substring(String str, int srcPos, int specialCharsLength) {
        if (str == null || "".equals(str) || specialCharsLength < 1) {
            return "";
        }
        if (srcPos < 0) {
            srcPos = 0;
        }
        if (specialCharsLength <= 0) {
            return "";
        }
        //获得字符串的长度
        char[] chars = str.toCharArray();
        if (srcPos > chars.length) {
            return "";
        }
        int charsLength = getCharsLength(chars, specialCharsLength);
        return new String(chars, srcPos, charsLength);
    }

    /**
     * 获取一段字符的长度，输入长度中汉、日、韩文字符长度为2，输出长度中所有字符均长度为1
     *
     * @param chars              一段字符
     * @param specialCharsLength 输入长度，汉、日、韩文字符长度为2
     * @return 输出长度，所有字符均长度为1
     */
    private static int getCharsLength(char[] chars, int specialCharsLength) {
        int count = 0;
        int normalCharsLength = 0;
        for (int i = 0; i < chars.length; i++) {
            int specialCharLength = getSpecialCharLength(chars[i]);
            if (count <= specialCharsLength - specialCharLength) {
                count += specialCharLength;
                normalCharsLength++;
            } else {
                break;
            }
        }
        return normalCharsLength;
    }

    /**
     * 获取字符长度：汉、日、韩文字符长度为2，ASCII码等字符长度为1
     *
     * @param c 字符
     * @return 字符长度
     */
    private static int getSpecialCharLength(char c) {
        if (isLetter(c)) {
            return 1;
        } else {
            return 2;
        }
    }


    // 基本数据类型
    private final static String[] types = {"int", "java.lang.String", "boolean", "char",
            "float", "double", "long", "short", "byte"};

    /**
     * 将对象转化为String
     *
     * @param object
     * @return
     */
    public static <T> String objectToString(T object) {
        if (object == null) {
            return "Object{object is null}";
        }
        if (object.toString()
                .startsWith(object.getClass()
                        .getName() + "@")) {
            StringBuilder builder = new StringBuilder(object.getClass()
                    .getSimpleName() + "{");
            Field[] fields = object.getClass()
                    .getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                boolean flag = false;
                for (String type : types) {
                    if (field.getType()
                            .getName()
                            .equalsIgnoreCase(type)) {
                        flag = true;
                        Object value = null;
                        try {
                            value = field.get(object);
                        } catch (IllegalAccessException e) {
                            value = e;
                        }
                        finally {
                            builder.append(String.format("%s=%s, ", field.getName(),
                                    value == null ? "null" : value.toString()));
                            break;
                        }
                    }
                }
                if (!flag) {
                    builder.append(String.format("%s=%s, ", field.getName(), "Object"));
                }
            }
            return builder.replace(builder.length() - 2, builder.length() - 1, "}")
                    .toString();
        } else {
            return object.toString();
        }
    }

    /**
     * 是否为空
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }

    /**
     * 字符串是否为空
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        if (str == null || str.length() == 0 || str.equalsIgnoreCase("null") || str.isEmpty() ||
                str.equals("")) {
            return true;
        } else {
            return false;
        }
    }


}
