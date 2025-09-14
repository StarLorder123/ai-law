package com.law.rag.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
@TableName("public.chat_base_collection")
public class ChatBaseCollectionEntity {

    @Id
    private Integer id;

    private String memoryid;

    private String content;

    private String role;
}
