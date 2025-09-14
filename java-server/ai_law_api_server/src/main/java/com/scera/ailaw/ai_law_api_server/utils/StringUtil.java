package com.scera.ailaw.ai_law_api_server.utils;

import java.util.UUID;

public class StringUtil {

    // 判空
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    // 非空
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    // 去除前缀
    public static String removePrefix(String str, String prefix) {
        if (isNotEmpty(str) && str.startsWith(prefix)) {
            return str.substring(prefix.length());
        }
        return str;
    }

    // 去除后缀
    public static String removeSuffix(String str, String suffix) {
        if (isNotEmpty(str) && str.endsWith(suffix)) {
            return str.substring(0, str.length() - suffix.length());
        }
        return str;
    }

    // 驼峰转下划线 userName -> user_name
    public static String camelToSnake(String str) {
        if (isEmpty(str)) return str;
        return str.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
    }

    // 下划线转驼峰 user_name -> userName
    public static String snakeToCamel(String str) {
        if (isEmpty(str)) return str;
        StringBuilder sb = new StringBuilder();
        boolean upper = false;
        for (char c : str.toCharArray()) {
            if (c == '_') {
                upper = true;
            } else {
                sb.append(upper ? Character.toUpperCase(c) : c);
                upper = false;
            }
        }
        return sb.toString();
    }

    // 首字母大写
    public static String capitalize(String str) {
        if (isEmpty(str)) return str;
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    // 首字母小写
    public static String uncapitalize(String str) {
        if (isEmpty(str)) return str;
        return Character.toLowerCase(str.charAt(0)) + str.substring(1);
    }

    // 生成 UUID（去掉中横线）
    public static String generateUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}

