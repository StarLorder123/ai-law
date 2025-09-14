package com.law.rag.service;

import com.law.rag.entity.FileEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {

    String insertFile(MultipartFile file);

    String getFileAbsolutePath(String path);

    FileEntity selectByFileid(String fileid);

    List<FileEntity> selectFileListByFileids(List<String> fileids);

}
