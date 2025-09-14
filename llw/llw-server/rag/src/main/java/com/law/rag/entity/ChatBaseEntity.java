package com.law.rag.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;

@Data
@TableName("public.chat_base")
public class ChatBaseEntity {

    private String id;

    private String caseid;

    private String userid;

    private Timestamp createAt;

    private String brief;
}
