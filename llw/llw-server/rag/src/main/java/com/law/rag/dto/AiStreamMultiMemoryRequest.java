package com.law.rag.dto;

import lombok.Data;

@Data
public class AiStreamMultiMemoryRequest {

    private String memoryID;

    private AiUserInput userInput;
}
