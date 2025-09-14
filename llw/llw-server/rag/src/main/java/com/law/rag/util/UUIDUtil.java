package com.law.rag.util;

import java.util.UUID;

public class UUIDUtil {
    /**
     * 生成一个标准格式的UUID
     * 格式：8-4-4-4-12（如：550e8400-e29b-41d4-a716-446655440000）
     *
     * @return 标准格式的UUID字符串
     */
    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * 生成一个不带横线的UUID
     * 格式：32位字符串（如：550e8400e29b41d4a716446655440000）
     *
     * @return 去掉横线的UUID字符串
     */
    public static String generateUUIDWithoutHyphens() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 自定义长度的随机UUID（基于标准UUID生成）
     *
     * @param length 返回的字符串长度
     * @return 自定义长度的UUID字符串
     */
    public static String generateCustomLengthUUID(int length) {
        String uuid = generateUUIDWithoutHyphens();
        return uuid.length() >= length ? uuid.substring(0, length) : uuid;
    }
}
