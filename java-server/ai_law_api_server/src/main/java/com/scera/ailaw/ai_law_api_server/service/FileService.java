package com.scera.ailaw.ai_law_api_server.service;

import com.scera.ailaw.ai_law_api_server.entity.FileMeta;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface FileService {

    void saveFile(InputStream inputStream, String originalFileName);

}
