package com.law.rag.dto;

import lombok.Data;

@Data
public class AiStreamMemoryRequest {

    private String userInput;

    private String memoryID;

    private Boolean isNewMemory;

    private String caseID;

    private String userID;
}
