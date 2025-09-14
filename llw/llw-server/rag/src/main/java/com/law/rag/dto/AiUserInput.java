package com.law.rag.dto;

import lombok.Data;

import java.util.List;

@Data
public class AiUserInput {

    private String chat;

    private List<String> images;
}
