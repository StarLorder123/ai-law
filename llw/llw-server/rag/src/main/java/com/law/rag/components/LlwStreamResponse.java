package com.law.rag.components;

import lombok.Data;

@Data
public class LlwStreamResponse {

    private String id;

    private String memoryID;

    private String content;

    private LlwStreamResponseStatusEnum status;

    public LlwStreamResponse(String id, String memoryID, String content, LlwStreamResponseStatusEnum status) {
        this.id = id;
        this.memoryID = memoryID;
        this.content = content;
        this.status = status;
    }

    public LlwStreamResponse(String id, String memoryID, String content) {
        this.id = id;
        this.memoryID = memoryID;
        this.content = content;
        this.status = LlwStreamResponseStatusEnum.INPROCESS;
    }

    public LlwStreamResponse(String id, String content) {
        this.id = id;
        this.content = content;
        this.memoryID = null;
        this.status = LlwStreamResponseStatusEnum.INPROCESS;
    }
}
