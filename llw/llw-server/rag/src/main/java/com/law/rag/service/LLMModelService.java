package com.law.rag.service;

import com.law.rag.dto.AiStreamMemoryRequest;
import com.law.rag.dto.AiUserInput;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import com.law.rag.components.LlwStreamingResponseHandler;

public interface LLMModelService {

    void webStreamChat(String value, ResponseBodyEmitter emitter);

    void webStreamChat(String value, ResponseBodyEmitter emitter, String memoryID);

    void webStreamChat(AiStreamMemoryRequest request, ResponseBodyEmitter emitter);

    void webStreamChat(AiUserInput value, ResponseBodyEmitter emitter);

    void webStreamChat(AiUserInput value, ResponseBodyEmitter emitter, String memoryID);

    void toolChat(String value, ResponseBodyEmitter emitter, String memoryID);
}
