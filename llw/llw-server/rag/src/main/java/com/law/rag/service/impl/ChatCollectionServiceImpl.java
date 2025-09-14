package com.law.rag.service.impl;

import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.law.rag.components.LlwRoleEnum;
import com.law.rag.entity.ChatBaseCollectionEntity;
import com.law.rag.entity.ChatBaseEntity;
import com.law.rag.mapper.ChatBaseCollectionMapper;
import com.law.rag.mapper.ChatBaseMapper;
import com.law.rag.service.ChatCollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.convert.QueryMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatCollectionServiceImpl implements ChatCollectionService {

    @Autowired
    private ChatBaseCollectionMapper chatBaseCollectionMapper;

    @Autowired
    private ChatBaseMapper chatBaseMapper;

    @Override
    public void insert(ChatBaseCollectionEntity entity) {
        chatBaseCollectionMapper.insert(entity);
    }

    @Override
    public List<ChatBaseCollectionEntity> queryByMemoryid(String memoryid) {
        return memoryid == null ? chatBaseCollectionMapper.selectList(null) : chatBaseCollectionMapper.queryByMemoryId(memoryid);
    }

    @Override
    public void addAiMessage(String memoryID, String message) {
        ChatBaseCollectionEntity entity=new ChatBaseCollectionEntity();
        entity.setMemoryid(memoryID);
        entity.setContent(message);
        entity.setRole(LlwRoleEnum.ASSISTANT.getValue());

        insert(entity);
    }

    @Override
    public void addUserMessage(String memoryID, String message) {
        ChatBaseCollectionEntity entity=new ChatBaseCollectionEntity();
        entity.setMemoryid(memoryID);
        entity.setContent(message);
        entity.setRole(LlwRoleEnum.USER.getValue());

        insert(entity);
    }

    @Override
    public void insertBaseChat(ChatBaseEntity entity) {

        if (!queryByParams(entity).isEmpty()) {
            return;
        }
        chatBaseMapper.insert(entity);
    }

    @Override
    public List<ChatBaseEntity> queryByParams(ChatBaseEntity entity) {

        QueryWrapper<ChatBaseEntity> wrapper = new QueryWrapper<>();

        if (entity.getId() != null) {
            wrapper.eq("id", entity.getId());
        }
        if (entity.getCaseid() != null) {
            wrapper.eq("caseid", entity.getCaseid());
        }
        if (entity.getUserid() != null) {
            wrapper.eq("userid", entity.getUserid());
        }
        return chatBaseMapper.selectList(wrapper);
    }

}
