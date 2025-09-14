package com.law.rag.components;

import lombok.Data;

public enum LlwStreamResponseStatusEnum {

    CONFIRM("confirm"),
    INPROCESS("doing"),
    OPERATION("operation"),
    ERROR("error"),
    COMPLETE("done");

    LlwStreamResponseStatusEnum(String value) {
        this.value = value;
    }

    private String value;

    public String getValue() {
        return value;
    }
}
