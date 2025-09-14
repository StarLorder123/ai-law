package com.scera.ailaw.ai_law_api_server.dao.impl;

import com.scera.ailaw.ai_law_api_server.dao.FileMetaDao;
import com.scera.ailaw.ai_law_api_server.entity.FileMeta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FileMetaDaoImpl implements FileMetaDao {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public FileMeta save(FileMeta fileMeta) {
        return mongoTemplate.save(fileMeta);
    }

    @Override
    public List<FileMeta> findAll() {
        return mongoTemplate.findAll(FileMeta.class);
    }
}
