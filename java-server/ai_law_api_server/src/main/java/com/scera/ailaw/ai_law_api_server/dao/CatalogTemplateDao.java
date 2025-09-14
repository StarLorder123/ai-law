package com.scera.ailaw.ai_law_api_server.dao;

import com.scera.ailaw.ai_law_api_server.entity.CatalogTemplate;

import java.util.List;

public interface CatalogTemplateDao {
    CatalogTemplate save(CatalogTemplate template);

    CatalogTemplate findById(String id);

    List<CatalogTemplate> findAll();

    List<CatalogTemplate> findByNameLike(String keyword);

    boolean deleteById(String id);
}
