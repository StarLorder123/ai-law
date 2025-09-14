package com.scera.ailaw.ai_law_api_server.utils;

import java.io.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.UUID;

public class FileUtil {

    /**
     * 读取文件内容为字符串
     */
    public static String readFileToString(String filePath) throws IOException {
        return Files.readString(Paths.get(filePath), StandardCharsets.UTF_8);
    }

    /**
     * 写字符串内容到文件（会覆盖原文件）
     */
    public static void writeStringToFile(String filePath, String content) throws IOException {
        Files.write(Paths.get(filePath), content.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 追加字符串到文件
     */
    public static void appendStringToFile(String filePath, String content) throws IOException {
        Files.write(Paths.get(filePath), content.getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
    }

    /**
     * 拷贝文件
     */
    public static void copyFile(String sourcePath, String destPath) throws IOException {
        Files.copy(Paths.get(sourcePath), Paths.get(destPath), StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * 删除文件或目录（递归删除）
     */
    public static void deleteFileOrDirectory(File file) throws IOException {
        if (!file.exists()) return;

        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                deleteFileOrDirectory(child);
            }
        }
        if (!file.delete()) {
            throw new IOException("删除失败: " + file.getAbsolutePath());
        }
    }

    /**
     * 获取文件扩展名
     */
    public static String getFileExtension(String fileName) {
        int index = fileName.lastIndexOf(".");
        return index > 0 ? fileName.substring(index + 1) : "";
    }

    /**
     * 获取文件名（不含扩展名）
     */
    public static String getFileNameWithoutExt(String fileName) {
        int index = fileName.lastIndexOf(".");
        return index > 0 ? fileName.substring(0, index) : fileName;
    }

    /**
     * 创建目录
     */
    public static void createDir(String dirPath) throws IOException {
        Files.createDirectories(Paths.get(dirPath));
    }

    /**
     * 检查文件是否存在
     */
    public static boolean exists(String path) {
        return Files.exists(Paths.get(path));
    }

    /**
     * 保存字节流到文件
     */
    public static void saveBytesToFile(byte[] data, String filePath) throws IOException {
        Files.write(Paths.get(filePath), data);
    }

    /**
     * 过滤非法字符，生成安全的文件名
     */
    public static String toSafeFileName(String originalName) {
        if (originalName == null || originalName.trim().isEmpty()) {
            return UUID.randomUUID().toString();
        }

        // 去掉路径信息
        originalName = originalName.replaceAll("\\\\", "/");
        originalName = originalName.substring(originalName.lastIndexOf('/') + 1);

        // 移除控制字符
        originalName = originalName.replaceAll("[\\p{Cntrl}]", "");

        // 替换非法字符
        originalName = originalName.replaceAll("[\\\\/:*?\"<>|]", "_");

        // 去除特殊 Unicode 组合字符（如表情）
        originalName = Normalizer.normalize(originalName, Normalizer.Form.NFKC).replaceAll("[^\\w.\\-]", "_");

        // 限制最大长度（如 100）
        int maxLength = 100;
        if (originalName.length() > maxLength) {
            String extension = getFileExtension(originalName);
            String baseName = getFileNameWithoutExt(originalName);
            baseName = baseName.substring(0, Math.min(baseName.length(), maxLength - extension.length() - 1));
            originalName = baseName + "." + extension;
        }

        return originalName;
    }

    /**
     * 格式化文件大小
     */
    public static String formatSize(long size) {
        if (size < 1024) return size + " B";
        int unit = 1024;
        double kbSize = size / (double) unit;
        if (kbSize < unit) return String.format("%.2f KB", kbSize);
        double mbSize = kbSize / unit;
        if (mbSize < unit) return String.format("%.2f MB", mbSize);
        double gbSize = mbSize / unit;
        return String.format("%.2f GB", gbSize);
    }

    /**
     * 计算文件夹（目录）大小（递归方式）
     *
     * @param directory
     * @return
     */
    public static long getDirectorySize(File directory) {
        if (!directory.exists()) return 0;
        if (directory.isFile()) return directory.length();

        long size = 0;
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                size += getDirectorySize(file);
            }
        }
        return size;
    }

    /**
     * 计算文件或目录的总大小（单位：字节）
     *
     * @param fileOrDir 文件或目录
     * @return 大小（字节数）
     */
    public static long getSizeInBytes(File fileOrDir) {
        if (fileOrDir == null || !fileOrDir.exists()) return 0;

        if (fileOrDir.isFile()) {
            return fileOrDir.length();
        }

        long total = 0;
        File[] files = fileOrDir.listFiles();
        if (files != null) {
            for (File file : files) {
                total += getSizeInBytes(file);
            }
        }
        return total;
    }
}
