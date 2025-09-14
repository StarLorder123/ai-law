package com.law.rag.service.impl;

import com.law.rag.RagApplication;
import com.law.rag.entity.FileEntity;
import com.law.rag.mapper.FileMapper;
import com.law.rag.service.FileService;
import com.law.rag.util.DateUtil;
import com.law.rag.util.FileUtil;
import com.law.rag.util.UUIDUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Service
public class FileServiceImpl implements FileService {

    private static final Logger log = LoggerFactory.getLogger(FileService.class);

    @Value("${llw.upload.dir}")
    private String fileRootDir;

    @Autowired
    private FileMapper fileMapper;

    private int insertFileEntity(FileEntity fileEntity){
        return fileMapper.insert(fileEntity);
    }

    private void saveFile(MultipartFile file,String fileDir,String path) throws IOException {
        File directory = new File(fileRootDir+File.separator+fileDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File destFile=new File(fileRootDir+path);
        file.transferTo(destFile);
    }

    /**
     * 保存文件的流程：
     * 1. 根据入参file获取文件名、文件后缀等信息，将FileEntity实例化完成；
     * 2. 将file文件流保存起来
     * 3. 插入file文件实体，并返回fileid
     * @param file
     * @return
     */
    @Override
    public String insertFile(MultipartFile file) {
        FileEntity fileEntity=new FileEntity();

        String fileid=UUIDUtil.generateUUIDWithoutHyphens();
        String type= FileUtil.getExtension(file.getOriginalFilename());

        String fileDir= DateUtil.getNowDayString();
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append(File.separator)
                        .append(fileDir)
                                .append(File.separator)
                                        .append(fileid)
                                                .append(".")
                                                        .append(type);

        fileEntity.setFileid(fileid);
        fileEntity.setFilename(file.getOriginalFilename());
        fileEntity.setCreateAt(new Timestamp(new Date().getTime()));
        fileEntity.setType(type);
        fileEntity.setSize(file.getSize());
        fileEntity.setPath(stringBuilder.toString());

        try{
            saveFile(file,fileDir,stringBuilder.toString());
        }catch (IOException e) {
            log.error(e.getMessage());
            return null;
        }

        fileMapper.insert(fileEntity);

        return fileid;
    }

    @Override
    public String getFileAbsolutePath(String path) {
        return fileRootDir+path;
    }

    @Override
    public FileEntity selectByFileid(String fileid) {
        return fileMapper.findByFileId(fileid);
    }

    @Override
    public List<FileEntity> selectFileListByFileids(List<String> fileids) {
        return fileMapper.getFilesByIds(fileids);
    }
}
