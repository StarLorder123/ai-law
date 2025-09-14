package com.scera.ailaw.ai_law_api_server.dao;

import com.scera.ailaw.ai_law_api_server.entity.FileMeta;

import java.util.List;

public interface FileMetaDao {

    FileMeta save(FileMeta fileMeta);

    List<FileMeta> findAll();

}
