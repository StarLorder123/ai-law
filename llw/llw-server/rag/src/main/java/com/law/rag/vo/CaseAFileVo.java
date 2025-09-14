package com.law.rag.vo;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class CaseAFileVo {
    private String id;

    private String name;

    private String content;

    private Timestamp judgeTime;

    private String judgeCourt;

    private String caseType;

    private String filename;

    private String path;

    private String type;

    private Long size;

    private String sum;

    private Timestamp createAt;
}
