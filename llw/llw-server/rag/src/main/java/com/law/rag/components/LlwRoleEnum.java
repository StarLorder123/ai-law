package com.law.rag.components;

public enum LlwRoleEnum {

    USER("USER"),
    ASSISTANT("ASSISTANT"),
    SYSTEM("SYSTEM");

    private String value;

    LlwRoleEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
