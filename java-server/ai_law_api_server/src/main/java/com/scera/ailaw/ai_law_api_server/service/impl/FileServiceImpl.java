package com.scera.ailaw.ai_law_api_server.service.impl;

import com.scera.ailaw.ai_law_api_server.service.FileService;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class FileServiceImpl implements FileService {

    // 存储文件的固定路径
    private static final String STORAGE_PATH = "/path/to/your/storage/directory/";

    @Override
    public void saveFile(InputStream inputStream, String originalFileName) {
        // 计算InputStream的大小
        long fileSize = calculateInputStreamSize(inputStream);

        // 重新打开InputStream，因为计算大小后流会被读取完
        try {
            inputStream.reset();
        } catch (IOException e) {
            e.printStackTrace();
            // 如果不支持reset，需要重新获取InputStream
        }

        // 创建文件对象
        File file = new File(STORAGE_PATH + originalFileName);

        // 确保目录存在
        File directory = file.getParentFile();
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // 写入文件流
        try (OutputStream outputStream = new FileOutputStream(file)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 计算InputStream的大小
     * @param inputStream 输入流
     * @return 输入流的大小（字节数）
     */
    private long calculateInputStreamSize(InputStream inputStream) {
        long size = 0;
        try {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                size += bytesRead;
            }
            // 将流重置到开始位置
            inputStream.reset();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }
}
