package com.scera.ailaw.ai_law_api_server.utils;

import java.util.*;

public class ArrayUtil {

    // 是否为空（null 或长度为 0）
    public static <T> boolean isEmpty(T[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(int[] array) {
        return array == null || array.length == 0;
    }

    // 是否不为空
    public static <T> boolean isNotEmpty(T[] array) {
        return !isEmpty(array);
    }

    // 合并两个数组
    public static <T> T[] merge(T[] first, T[] second) {
        if (isEmpty(first)) return second;
        if (isEmpty(second)) return first;

        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    // 数组去重（返回 List）
    public static <T> List<T> distinct(T[] array) {
        if (isEmpty(array)) return Collections.emptyList();
        return new ArrayList<>(new LinkedHashSet<>(Arrays.asList(array)));
    }

    // 是否包含某个元素
    public static <T> boolean contains(T[] array, T value) {
        if (isEmpty(array)) return false;
        for (T t : array) {
            if (Objects.equals(t, value)) return true;
        }
        return false;
    }

    // 找出最大值（int 数组）
    public static int max(int[] array) {
        if (isEmpty(array)) throw new IllegalArgumentException("array is empty");
        int max = array[0];
        for (int val : array) {
            if (val > max) max = val;
        }
        return max;
    }

    // 找出最小值（int 数组）
    public static int min(int[] array) {
        if (isEmpty(array)) throw new IllegalArgumentException("array is empty");
        int min = array[0];
        for (int val : array) {
            if (val < min) min = val;
        }
        return min;
    }

    // 数组转字符串（默认用 , 分隔）
    public static String join(Object[] array) {
        return join(array, ",");
    }

    public static String join(Object[] array, String delimiter) {
        if (isEmpty(array)) return "";
        return String.join(delimiter, Arrays.stream(array).map(String::valueOf).toArray(String[]::new));
    }
}

