package com.law.rag.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileUtil {

    public static String getExtension(String filename) {
        if (filename != null && filename.contains(".")) {
            return filename.substring(filename.lastIndexOf(".") + 1);
        }
        return null;
    }

    /**
     * 计算 MultipartFile 文件的 MD5 值
     *
     * @param file MultipartFile 对象
     * @return 文件的 MD5 值
     * @throws IOException              如果读取文件内容失败
     * @throws NoSuchAlgorithmException 如果 MD5 算法不可用
     */
    public static String calculateMd5(MultipartFile file) throws IOException, NoSuchAlgorithmException {
        // 获取文件的字节流
        byte[] fileBytes = file.getBytes();

        // 创建 MD5 消息摘要实例
        MessageDigest md = MessageDigest.getInstance("MD5");

        // 计算 MD5 值
        byte[] md5Bytes = md.digest(fileBytes);

        // 转换为十六进制字符串
        StringBuilder hexString = new StringBuilder();
        for (byte b : md5Bytes) {
            String hex = Integer.toHexString(0xFF & b);
            if (hex.length() == 1) {
                hexString.append('0'); // 补零
            }
            hexString.append(hex);
        }

        return hexString.toString();
    }

    /**
     * 将字符串内容写入到新的文件中
     */
    public static String writeFile(String filePath, String content) {
        File file = new File(filePath);

        // 使用 try-with-resources 确保资源自动关闭
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(content); // 写入内容
            System.out.println("内容已成功写入到文件：" + filePath);
        } catch (IOException e) {
            System.err.println("写入文件时发生错误: " + e.getMessage());
        }

        return filePath;
    }

}
