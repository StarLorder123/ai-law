package com.law.rag.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
public class OllamaConfig {
    @Value("${ollama.model.host}")
    private String host;

    @Value("${ollama.model.port}")
    private int port;

    @Value("${ollama.model.name}")
    private String name;
}
