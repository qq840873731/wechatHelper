package com.naxions.www.wechathelper.util;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 过滤 emoji 表情
 */
public class FilterUtil {
    public static final String NULL="null";
    public static final String EMPTY="";
    private static final int NUMBER_LENGTH = 11;

    private static boolean isEmojiCharacter(char codePoint) {
        return (codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA)
                || (codePoint == 0xD)
                || ((codePoint >= 0x20) && (codePoint <= 0xD7FF))
                || ((codePoint >= 0xE000) && (codePoint <= 0xFFFD))
                || ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF));
    }

    /**
     * 过滤emoji 或者 其他非文字类型的字符
     *
     * @param source
     * @return
     */
    public static String filterEmoji(String source) {
        if (source.equals(EMPTY) || source.equals(NULL) || source == null) {
            return "";
        }
        StringBuilder buf = null;
        int len = source.length();
        for (int i = 0; i < len; i++) {
            char codePoint = source.charAt(i);
            if (isEmojiCharacter(codePoint)) {
                if (buf == null) {
                    buf = new StringBuilder(source.length());
                }
                buf.append(codePoint);
            }
        }
        if (buf == null) {
            return source;
        } else {
            if (buf.length() == len) {
                buf = null;
                return source;
            } else {
                return buf.toString();
            }
        }
    }

    /**
     * 过滤联系人,只获取备注有电话号码的联系人并写入 csv 上传
     *
     * @param nickName
     * @return
     */
    public static boolean filterPhoneNumber(String nickName) {

        // 过滤出纯数字
        nickName = Pattern.compile("[^0-9]").matcher(nickName.trim()).replaceAll("");
       
        if (nickName.length() < NUMBER_LENGTH) {
            return false;
        }
        char[] chars = nickName.toCharArray();
        //所有11位数字的集合
        ArrayList<String> phoneList = new ArrayList<>();
        for (int i = 0; i < chars.length; i++) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int j = 0; j < NUMBER_LENGTH; j++) {
                if (i + j < chars.length) {
                    stringBuilder.append(chars[i + j]);
                }
            }
            if (stringBuilder.length() == NUMBER_LENGTH) {
                phoneList.add(stringBuilder.toString());
            }
        }

        List<String> regexList = new ArrayList<>();
        /**
         * 手机号码
         * 移动：134[0-8],135,136,137,138,139,150,151,157,158,159,182,187,188
         * 联通：130,131,132,152,155,156,185,186
         * 电信：133,1349,153,180,189,181(增加)
         */
        regexList.add("^1(3[0-9]|5[0-35-9]|8[025-9])\\d{8}$");
        /**
         * 中国移动：China Mobile
         * 134[0-8],135,136,137,138,139,150,151,157,158,159,182,187,188
         */
        regexList.add("^1(34[0-8]|(3[5-9]|5[017-9]|8[2378])\\d)\\d{7}$");
        /**
         * 中国联通：China Unicom
         * 130,131,132,152,155,156,185,186
         */
        regexList.add("^1(3[0-2]|5[256]|8[56])\\d{8}$");
        /**
         * 中国电信：China Telecom
         * 133,1349,153,180,189,181(增加)
         */
        regexList.add("^1((33|53|8[019])[0-9]|349)\\d{7}$");
        for (String phone : phoneList) {
            for (String regex : regexList) {
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(phone);
                if (matcher.matches()) {
                    return true;
                }
            }
        }

        return false;
    }
}