package com.law.rag.vo;

import lombok.Data;

@Data
public class ChatQueryRequest {

    private String memoryID;

    private String caseID;

    private String userID;
}
