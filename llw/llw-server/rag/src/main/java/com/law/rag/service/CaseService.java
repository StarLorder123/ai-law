package com.law.rag.service;

import com.law.rag.entity.CaseBaseCollectionEntity;

import java.util.List;

public interface CaseService {
    List<CaseBaseCollectionEntity> getAllBaseCases();

    CaseBaseCollectionEntity getBaseCaseById(String id);
}
