package com.law.rag.service;

import com.law.rag.entity.ChatBaseCollectionEntity;
import com.law.rag.entity.ChatBaseEntity;

import java.util.List;

public interface ChatCollectionService {

    void insert(ChatBaseCollectionEntity entity);

    List<ChatBaseCollectionEntity> queryByMemoryid(String memoryid);

    void addAiMessage(String memoryID, String message);

    void addUserMessage(String memoryID, String message);

    void insertBaseChat(ChatBaseEntity entity);

    List<ChatBaseEntity> queryByParams(ChatBaseEntity entity);
}
