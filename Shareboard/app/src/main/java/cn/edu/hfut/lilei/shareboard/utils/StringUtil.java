package cn.edu.hfut.lilei.shareboard.utils;

import org.apache.commons.lang.StringUtils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.showLog;

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


    /**
     * 判断email格式是否正确
     *
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        if (isEmpty(email)) {
            return false;
        }
        String str =
                "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);

        return m.matches();
    }

    /**
     * 判断是否全是数字
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    /**
     * 判断姓 、名 是否符合
     *
     * @param str
     * @return
     */
    public static boolean isValidName(String str) {

        if (isEmpty(str)) {
            return false;
        }
        //可以包括汉字，英文字母，数字（不能数字开头）
        String regex = "^[a-zA-Z\u4E00-\u9FA5][a-zA-Z0-9\u4E00-\u9FA5]*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher isName = pattern.matcher(str);
        if (!isName.matches()) {
            return false;
        }
        return true;
    }

    /**
     * 判断会议主题 是否符合
     *
     * @param str
     * @return
     */
    public static boolean isValidTheme(String str) {

        if (isEmpty(str)) {
            return false;
        }
        //可以包括汉字，英文字母，数字（不能数字开头）
        String regex = "^[a-zA-Z\u4E00-\u9FA5][a-zA-Z0-9\u4E00-\u9FA5]*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher isName = pattern.matcher(str);
        if (!isName.matches()) {
            return false;
        }
        return true;
    }

    /**
     * 判断meetingUrl 是否符合
     *
     * @param str
     * @return
     */
    public static boolean isValidMeetingUrl(String str) {

        if (isEmpty(str) || (length(str) != 12)) {
            return false;
        }
//        if (!isNumeric(str)) {
//            return false;
//        }
        return true;
    }

    /**
     * 判断密码 是否符合
     *
     * @param str
     * @return
     */
    public static boolean isValidPassword(String str) {

        if (isEmpty(str) || (length(str) != 12)) {
            return false;
        }
        //可以字母开头，数字和下划线
        String regex = "^[a-zA-Z][a-zA-Z0-9_]*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher isPwd = pattern.matcher(str);
        if (!isPwd.matches()) {
            return false;
        }
        return true;
    }

    /**
     * 判断密码 是否符合
     *
     * @param str
     * @return
     */
    public static boolean isValidMeetingPassword(String str) {

        if (isEmpty(str) || (length(str) != 8)) {
            return false;
        }
        //可以字母开头，数字和下划线
        String regex = "^[a-zA-Z][a-zA-Z0-9_]*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher isPwd = pattern.matcher(str);
        if (!isPwd.matches()) {
            return false;
        }
        return true;
    }


    /**
     * 对字符串md5加密
     *
     * @param str
     * @return
     */
    public static String getMD5(String str) {
        // 生成一个MD5加密计算摘要
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
            // 计算md5函数
            md.update((str + "lilei").getBytes());
            // digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
            // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
            return new BigInteger(1, md.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }


    }

    /**
     * 判断网址是否合法
     *
     * @param urlString
     * @return
     */
    public static boolean isValidUrl(String urlString) {
        URI uri = null;
        try {
            uri = new URI(urlString);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return false;
        }

        if (uri.getHost() == null) {
            return false;
        }
        if (uri.getScheme()
                .equalsIgnoreCase("http") || uri.getScheme()
                .equalsIgnoreCase("https")) {
            return true;
        }
        return false;
    }

    ////////////////////////////////////////////////////////////////////

    /**
     * des 加密
     *
     * @param data
     * @param password
     * @return
     */
    public static byte[] encrypt(byte[] data, byte[] password) {
        byte[] ret = null;
        //判断参数是否符合条件
        if (data != null && data.length > 0) {
            if (password != null && password.length == 8) {
                try {
                    //1、创造加密引擎
                    Cipher cipher = Cipher.getInstance("DES");
                    //2、初始化
                    SecretKeySpec key = new SecretKeySpec(password, "DES");
                    cipher.init(Cipher.ENCRYPT_MODE, key);
                    //3、对数据进行加密
                    ret = cipher.doFinal(data);
                    showLog("加密完成");

                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (NoSuchPaddingException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                } catch (BadPaddingException e) {
                    e.printStackTrace();
                } catch (IllegalBlockSizeException e) {
                    e.printStackTrace();
                }
            }
        }
        return ret;
    }

    /**
     * des  解密
     *
     * @param data
     * @param password
     * @return
     */
    public static byte[] decrypt(byte[] data, byte[] password) {
        byte[] ret = null;

        if (data != null && data.length > 0) {
            if (password != null && password.length == 8) {
                try {
                    //1、获取解密引擎
                    Cipher cipher = Cipher.getInstance("DES");

                    //2、初始化
                    SecretKeySpec key = new SecretKeySpec(password, "DES");

                    cipher.init(Cipher.DECRYPT_MODE, key);

                    //3、解密
                    ret = cipher.doFinal(data);

                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (NoSuchPaddingException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                } catch (BadPaddingException e) {
                    e.printStackTrace();
                } catch (IllegalBlockSizeException e) {
                    e.printStackTrace();
                }

            }

        }
        return ret;
    }

    ////////////////////////////////////////////////////////////////////
    // AES  加密
    ////////////////////////////////////////////////////////////////////

    public static String encrypt_security(String seed, String cleartext)
            throws Exception {
        byte[] rawKey = getRawKey(seed.getBytes());
        byte[] result = encrypt_bytes(rawKey, cleartext.getBytes());
        return toHex(result);
    }

    private static byte[] encrypt_bytes(byte[] raw, byte[] clear) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(clear);
        return encrypted;
    }

    private static String toHex(byte[] buf) {
        final String HEX = "0123456789ABCDEF";
        if (buf == null)
            return "";
        StringBuffer result = new StringBuffer(2 * buf.length);
        for (int i = 0; i < buf.length; i++) {
            result.append(HEX.charAt((buf[i] >> 4) & 0x0f))
                    .append(
                            HEX.charAt(buf[i] & 0x0f));
        }
        return result.toString();
    }

    public static String decrypt_security(String seed, String encrypted)
            throws Exception {
        byte[] rawKey = getRawKey(seed.getBytes());
        byte[] enc = toByte(encrypted);
        byte[] result = decrypt_bytes(rawKey, enc);
        return new String(result);
    }

    private static byte[] decrypt_bytes(byte[] raw, byte[] encrypted)
            throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] decrypted = cipher.doFinal(encrypted);
        return decrypted;
    }

    private static byte[] toByte(String hexString) {
        int len = hexString.length() / 2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++)
            result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2),
                    16)
                    .byteValue();
        return result;
    }

    private static byte[] getRawKey(byte[] seed) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "Crypto");
        sr.setSeed(seed);
        kgen.init(128, sr); // 192 and 256 bits may not be available
        SecretKey skey = kgen.generateKey();
        byte[] raw = skey.getEncoded();
        return raw;
    }


    public static int appVersionCompare(String server, String local) {
        showLog("appVersionCompare" + server + local);
        String[] server_arr = server.split("\\.");
        String[] local_arr = local.split("\\.");
        if (server_arr.length == 3 && local_arr.length == 3) {
            if (Integer.valueOf(server_arr[0]) > Integer.valueOf(local_arr[0])) {
                showLog("Integer.valueOf(server_arr[0]) > Integer.valueOf(local_arr[0])");
                return 1;
            }
            if (Integer.valueOf(server_arr[0]) < Integer.valueOf(local_arr[0])) {
                showLog("Integer.valueOf(server_arr[0]) < Integer.valueOf(local_arr[0])");
                return -1;
            }
            if (Integer.valueOf(server_arr[1]) > Integer.valueOf(local_arr[1])) {
                showLog("Integer.valueOf(server_arr[1]) > Integer.valueOf(local_arr[1])");
                return 1;
            }
            if (Integer.valueOf(server_arr[1]) < Integer.valueOf(local_arr[1])) {
                showLog("Integer.valueOf(server_arr[1]) < Integer.valueOf(local_arr[1])");
                return -1;
            }
            if (Integer.valueOf(server_arr[2]) > Integer.valueOf(local_arr[2])) {
                showLog("Integer.valueOf(server_arr[2]) > Integer.valueOf(local_arr[2])");
                return 1;
            }
            if (Integer.valueOf(server_arr[2]) < Integer.valueOf(local_arr[2])) {
                showLog("Integer.valueOf(server_arr[2]) < Integer.valueOf(local_arr[2])");
                return -1;
            }
            showLog("===");
            return 0;

        }
        showLog("不满足server_arr.length == 3 && local_arr.length == 3");

        return 0;
    }


}


