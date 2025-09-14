package com.law.rag.components;

import com.law.rag.util.UUIDUtil;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.TokenWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiTokenizer;
import lombok.Data;

@Data
public class LlwChatMemory {

    private String ChatID;

    private ChatMemory chatMemory;

    private static final int MAX_TOKENS = 128 * 1000;

    public LlwChatMemory() {
        this.ChatID = UUIDUtil.generateUUIDWithoutHyphens();
        this.chatMemory = TokenWindowChatMemory.withMaxTokens(MAX_TOKENS, new OpenAiTokenizer());
    }

    public LlwChatMemory(String ChatID) {
        this.ChatID = ChatID;
        this.chatMemory = TokenWindowChatMemory.withMaxTokens(MAX_TOKENS, new OpenAiTokenizer());
    }

}
