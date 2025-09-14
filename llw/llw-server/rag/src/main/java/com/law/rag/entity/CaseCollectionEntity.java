package com.law.rag.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.sql.Timestamp;

@Data
@TableName("public.case_collection")
public class CaseCollectionEntity {

    @Id
    private String id;

    private String name;

    private String content;

    private Timestamp judgeTime;

    private String judgeCourt;

    private String caseType;
}
