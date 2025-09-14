package com.law.rag.service.impl;

import com.law.rag.entity.CaseBaseCollectionEntity;
import com.law.rag.mapper.CaseBaseCollectionMapper;
import com.law.rag.service.CaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CaseServiceImpl implements CaseService {

    @Autowired
    private CaseBaseCollectionMapper caseBaseCollectionMapper;

    @Override
    public List<CaseBaseCollectionEntity> getAllBaseCases() {
        return caseBaseCollectionMapper.selectList(null);
    }

    @Override
    public CaseBaseCollectionEntity getBaseCaseById(String id) {
        return caseBaseCollectionMapper.selectById(id);
    }
}
