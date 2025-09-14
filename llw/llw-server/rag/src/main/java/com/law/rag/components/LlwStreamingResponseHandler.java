package com.law.rag.components;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson2.JSON;
import com.law.rag.entity.ChatBaseCollectionEntity;
import com.law.rag.service.ChatCollectionService;
import com.law.rag.util.SpringContextHolder;
import com.law.rag.util.UUIDUtil;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.StreamingResponseHandler;
import dev.langchain4j.model.output.Response;

public class LlwStreamingResponseHandler implements StreamingResponseHandler<AiMessage> {

    // 用于统计输出的单词数量
    private final int WORD_THRESHOLD = 10; // 每当输出5个单词时触发一次
    private final StringBuilder currentOutput = new StringBuilder();
    private int wordCount = 0;

    String tokenstring = "";

    private ResponseBodyEmitter emitter;
    private LlwChatMemory memory;

    public LlwStreamingResponseHandler(ResponseBodyEmitter emitter) {
        this.emitter = emitter;
    }

    public LlwStreamingResponseHandler(ResponseBodyEmitter emitter, LlwChatMemory memory) {
        this.emitter = emitter;
        this.memory = memory;
    }

    private String getTokenStringResponse(LlwStreamResponseStatusEnum status) {
        return JSON.toJSONString(
                new LlwStreamResponse(
                        UUIDUtil.generateUUID(),
                        memory == null ? null : memory.getChatID(),
                        currentOutput.toString(),
                        status == null ? LlwStreamResponseStatusEnum.INPROCESS : status
                )
        ).trim();
    }

    @Override
    public void onNext(String token) {
        tokenstring += token;
        // 将token添加到输出中
        currentOutput.append(token);

        // 计算当前token中的单词数量
        String[] tokens = token.split("\\s+");
        wordCount += tokens.length;

        // 每当输出的单词数达到5个时，触发一次输出
        if (wordCount >= WORD_THRESHOLD) {
            try {
                // System.out.println("Output so far: " + currentOutput.toString().trim());
                emitter.send(getTokenStringResponse(null));
                wordCount = 0; // 重置单词计数器
                currentOutput.setLength(0); // 重置输出内容
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onComplete(Response<AiMessage> response) {
        // 完成后输出最终内容
        try {
            System.out.println("Output so far: " + currentOutput.toString().trim());

            emitter.send(getTokenStringResponse(LlwStreamResponseStatusEnum.COMPLETE));

            wordCount = 0; // 重置单词计数器
            currentOutput.setLength(0); // 重置输出内容
            emitter.complete();
        } catch (IOException e) {
            emitter.complete();
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // 将Agent输出的内容放入到数据库中去
        ChatCollectionService chatCollectionService= SpringContextHolder.getBean(ChatCollectionService.class);
        if (chatCollectionService != null) {
            ChatBaseCollectionEntity entity=new ChatBaseCollectionEntity();
            entity.setMemoryid(memory.getChatID());
            entity.setContent(response.content().text());
            entity.setRole(LlwRoleEnum.ASSISTANT.getValue());

            chatCollectionService.insert(entity);
        }

        if (memory != null) {
            memory.getChatMemory().add(
                    new AiMessage(response.content().text()));
        }

        System.out.println("Final output: " + response.content().text());

    }

    @Override
    public void onError(Throwable error) {
        try {
            emitter.send(getTokenStringResponse(LlwStreamResponseStatusEnum.ERROR));
        } catch (IOException e) {
            emitter.complete();
            throw new RuntimeException(e);
        }
    }

}
