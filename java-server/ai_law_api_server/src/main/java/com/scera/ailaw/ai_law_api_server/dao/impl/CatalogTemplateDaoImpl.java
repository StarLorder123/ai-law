package com.scera.ailaw.ai_law_api_server.dao.impl;

import com.scera.ailaw.ai_law_api_server.dao.CatalogTemplateDao;
import com.scera.ailaw.ai_law_api_server.entity.CatalogTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@Repository
public class CatalogTemplateDaoImpl implements CatalogTemplateDao {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public CatalogTemplate save(CatalogTemplate template) {
        return mongoTemplate.save(template);
    }

    @Override
    public CatalogTemplate findById(String id) {
        return mongoTemplate.findById(id, CatalogTemplate.class);
    }

    @Override
    public List<CatalogTemplate> findAll() {
        return mongoTemplate.findAll(CatalogTemplate.class);
    }

    @Override
    public List<CatalogTemplate> findByNameLike(String keyword) {
        Query query = new Query(Criteria.where("name").regex(".*" + keyword + ".*", "i"));
        return mongoTemplate.find(query, CatalogTemplate.class);
    }

    @Override
    public boolean deleteById(String id) {
        Query query = new Query(Criteria.where("_id").is(id));
        return mongoTemplate.remove(query, CatalogTemplate.class).wasAcknowledged();
    }
}
