package com.law.rag.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.law.rag.entity.ChatBaseCollectionEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ChatBaseCollectionMapper extends BaseMapper<ChatBaseCollectionEntity> {

    @Select("""
            SELECT * FROM public.chat_base_collection cbc
            WHERE cbc.memoryid=#{memoryid}
            ORDER BY id ASC
            """)
    List<ChatBaseCollectionEntity> queryByMemoryId(@Param("memoryid")String memoryid);
}
