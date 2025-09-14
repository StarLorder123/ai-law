package com.law.rag.service.impl;

import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson2.JSON;
import com.law.rag.components.*;
import com.law.rag.dto.AiStreamMemoryRequest;
import com.law.rag.dto.AiUserInput;
import com.law.rag.entity.CaseBaseCollectionEntity;
import com.law.rag.entity.ChatBaseCollectionEntity;
import com.law.rag.entity.ChatBaseEntity;
import com.law.rag.entity.FileEntity;
import com.law.rag.mapper.FileMapper;
import com.law.rag.service.CaseService;
import com.law.rag.service.ChatCollectionService;
import com.law.rag.service.FileService;
import com.law.rag.tools.CalculatorTool;
import com.law.rag.util.SpringContextHolder;
import com.law.rag.util.UUIDUtil;
import dev.langchain4j.data.message.*;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.service.AiServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import com.law.rag.service.LLMModelService;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;

@Service
public class LLMModelServiceImpl implements LLMModelService {

    private LlwMemoryPool pool = new LlwMemoryPool();

    @Value("${ollama.model.host}")
    private String host;

    @Value("${ollama.model.port}")
    private int port;

    @Value("${ollama.model.name}")
    private String modelName;

    private final Double TEMPEARATURE = 0.2;

    @Autowired
    private FileService fileService;

    @Autowired
    private ChatCollectionService chatCollectionService;

    @Autowired
    private CaseService caseService;

    private ChatLanguageModel model;

    private StreamingChatLanguageModel streamModel;

    /**
     * chat
     * 非流式输出
     *
     * @param value
     * @return
     */
    private String chat(String value) {
        if (model == null) {
            model = OllamaChatModel.builder()
                    .baseUrl(String.format("http://%s:%d", host, port))
                    .modelName(modelName)
                    .temperature(TEMPEARATURE)
                    .build();
        }
        return model.generate(value);
    }

    private Response<AiMessage> chat(List<?> values) {
        if (streamModel == null) {
            streamModel = OllamaStreamingChatModel.builder()
                    .baseUrl(String.format("http://%s:%d", host, port))
                    .modelName(modelName)
                    .temperature(0.0)
                    .build();
        }

        if (!values.isEmpty() && values.get(0) instanceof ChatMessage) {
            return model.generate((List<ChatMessage>) values);
        } else if (!values.isEmpty() && values.get(0) instanceof Content) {
            return model.generate(new UserMessage((List<Content>) values));
        }
        return null;
    }


    /**
     * 流式chat
     * 1. 判断streamModel是否存在，如果不存在就根据参数重新创建一个
     * 2. 将 指令 和 流式处理器（StreamHandler） 传入模型
     *
     * @param value
     * @param handler
     */
    private void streamChat(String value, LlwStreamingResponseHandler handler) {
        if (streamModel == null) {
            streamModel = OllamaStreamingChatModel.builder()
                    .baseUrl(String.format("http://%s:%d", host, port))
                    .modelName(modelName)
                    .temperature(TEMPEARATURE)
                    .build();
        }
        streamModel.generate(value, handler);
    }

    /**
     * 带附件的流式chat
     *
     * @param values
     * @param handler
     */
    private void streamChat(List<?> values, LlwStreamingResponseHandler handler) {
        if (streamModel == null) {
            streamModel = OllamaStreamingChatModel.builder()
                    .baseUrl(String.format("http://%s:%d", host, port))
                    .modelName(modelName)
                    .temperature(0.0)
                    .build();
        }

        if (!values.isEmpty() && values.get(0) instanceof ChatMessage) {
            streamModel.generate((List<ChatMessage>) values, handler);
        } else if (!values.isEmpty() && values.get(0) instanceof Content) {
            streamModel.generate(new UserMessage((List<Content>) values), handler);
        }
    }

    /**
     * 带有记忆的流式chat
     *
     * @param value
     * @param handler
     * @param memory
     */
    void streamChatWithMemory(Object value, LlwStreamingResponseHandler handler, LlwChatMemory memory) {
        if (value instanceof String) {
            memory.getChatMemory().add(new UserMessage((String) value));
            streamChat(memory.getChatMemory().messages(), handler);
        }
        if (value instanceof AiUserInput) {
            List<Content> contents = new ArrayList<>();

            List<FileEntity> fileEntities = fileService.selectFileListByFileids(((AiUserInput) value).getImages());
            for (FileEntity file : fileEntities) {
                contents.add(
                        new ImageContent(
                                Paths.get(fileService.getFileAbsolutePath(file.getPath())).toUri()
                        )
                );
            }

            contents.add(new TextContent(((AiUserInput) value).getChat()));

            memory.getChatMemory().add(new UserMessage(contents));
            streamChat(memory.getChatMemory().messages(), handler);
        }
    }

    /**
     * 带有记忆功能的Chat
     *
     * @param value
     * @param memory
     * @param tool
     * @return
     */
    String toolChatWithMemory(String value, LlwChatMemory memory, Object tool) {

        if (model == null) {
            model = OllamaChatModel.builder()
                    .baseUrl(String.format("http://%s:%d", host, port))
                    .modelName(modelName)
                    .temperature(TEMPEARATURE)
                    .build();
        }

        StringAssistant assistant = AiServices.builder(StringAssistant.class)
                .chatLanguageModel(model)
                .tools(new CalculatorTool())
                .chatMemory(memory.getChatMemory())
                .build();

        return assistant.chat(value);
    }

    /**
     * 获取memory
     *
     * @param memoryID
     * @return
     */
    LlwChatMemory getMemory(String memoryID) {

        LlwChatMemory memory = new LlwChatMemory(memoryID);

        SystemMessage systemMessage = SystemMessage.from(
                "你是一名律师助理。需要根据法律法规和给出的案件信息为律师给出对应的建议。\n" +
                        "现提出如下要求：\n" +
                        "1. 建议需要贴近提供的事实\n" +
                        "2. 法律条款引用恰当，引用的法律条款必须在文书中明确提及。" +
                        "3. 对材料中涉及的人称输出准确，且前后保持一致");
        memory.getChatMemory().add(systemMessage);

        List<ChatBaseCollectionEntity> list = chatCollectionService.queryByMemoryid(memoryID);

        for (ChatBaseCollectionEntity entity : list) {
            if (entity.getRole().equals(LlwRoleEnum.USER.getValue())) {
                memory.getChatMemory().add(new UserMessage(entity.getContent()));
            } else if (entity.getRole().equals(LlwRoleEnum.ASSISTANT.getValue())) {
                memory.getChatMemory().add(new AiMessage(entity.getContent()));
            }
        }

        return memory;
    }

    /**
     * 从缓存中获取memory记录
     *
     * @param memoryID
     * @return
     */
    LlwChatMemory getMemoryFromCache(String memoryID) {
        LlwChatMemory memory = pool.get(memoryID);

        if (memory != null) {
            return memory;
        }

        return getMemory(memoryID);
    }

    @Override
    public void webStreamChat(String value, ResponseBodyEmitter emitter) {
        System.out.println();
        streamChat(value, new LlwStreamingResponseHandler(emitter));
    }

    /**
     * 带有上下文的流式输出交流
     *
     * @param value
     * @param emitter
     * @param memoryID
     */
    @Override
    public void webStreamChat(String value, ResponseBodyEmitter emitter, String memoryID) {

        LlwChatMemory memory;

        if (memoryID == null) {
            return;
        }

        if (pool.get(memoryID) == null) {
            memory = getMemory(memoryID);
            pool.put(memoryID, memory);
        } else {
            memory = pool.get(memoryID);
        }

        ChatCollectionService chatCollectionService = SpringContextHolder.getBean(ChatCollectionService.class);
        if (chatCollectionService != null) {
            ChatBaseCollectionEntity entity = new ChatBaseCollectionEntity();
            entity.setMemoryid(memory.getChatID());
            entity.setContent(value);
            entity.setRole(LlwRoleEnum.USER.getValue());

            chatCollectionService.insert(entity);
        }

        streamChatWithMemory(value, new LlwStreamingResponseHandler(emitter, memory), memory);

    }

    @Override
    public void webStreamChat(AiStreamMemoryRequest request, ResponseBodyEmitter emitter) {

        /**
         * 1. 查询ChatBase表中是否存在该memoryID
         * 2. 如果不存在，说明这是第一次开启这个session。需要新建一个memoryID
         * 3. 如果存在，说明这个session已经存在，需要从数据库中加载对应的memory
         */
        LlwChatMemory memory;

        ChatCollectionService chatCollectionService = SpringContextHolder.getBean(ChatCollectionService.class);

        String input = request.getUserInput();

        if (request.getMemoryID() == null) {
            return;
        }

        if (pool.get(request.getMemoryID()) == null) {

            ChatBaseEntity chatBaseEntity = new ChatBaseEntity();

            chatBaseEntity.setId(request.getMemoryID());

            memory = getMemory(request.getMemoryID());

            if (chatCollectionService.queryByParams(chatBaseEntity).isEmpty()) {
                chatBaseEntity.setCaseid(request.getCaseID());
                chatBaseEntity.setUserid(request.getUserID());
                chatBaseEntity.setCreateAt(new Timestamp(new Date().getTime()));
                chatBaseEntity.setBrief(request.getUserInput());

                chatCollectionService.insertBaseChat(chatBaseEntity);

                if (request.getCaseID() != null) {
                    CaseBaseCollectionEntity caseBaseCollectionEntity = caseService.getBaseCaseById(request.getCaseID());

                    if (caseBaseCollectionEntity != null) {

                        input = "当前的文书如下：\n" +
                                caseBaseCollectionEntity.getContent() +
                                request.getUserInput();

                        memory.getChatMemory().add(
                                new UserMessage(input)
                        );

                        ChatBaseCollectionEntity entity = new ChatBaseCollectionEntity();
                        entity.setMemoryid(memory.getChatID());
                        entity.setContent(input);
                        entity.setRole(LlwRoleEnum.USER.getValue());

                        chatCollectionService.insert(entity);

                    }
                }
            }

            pool.put(request.getMemoryID(), memory);
        } else {

            memory = pool.get(request.getMemoryID());

            ChatBaseCollectionEntity entity = new ChatBaseCollectionEntity();
            entity.setMemoryid(memory.getChatID());
            entity.setContent(request.getUserInput());
            entity.setRole(LlwRoleEnum.USER.getValue());

            chatCollectionService.insert(entity);

        }

        streamChatWithMemory(input, new LlwStreamingResponseHandler(emitter, memory), memory);

    }

    @Override
    public void webStreamChat(AiUserInput value, ResponseBodyEmitter emitter) {
        List<Content> contents = new ArrayList<>();

        List<FileEntity> fileEntities = fileService.selectFileListByFileids(value.getImages());
        for (FileEntity file : fileEntities) {
            contents.add(
                    new ImageContent(
                            Paths.get(fileService.getFileAbsolutePath(file.getPath())).toUri()
                    )
            );
        }

        contents.add(new TextContent(value.getChat()));

        streamChat(contents, new LlwStreamingResponseHandler(emitter));
    }

    /**
     * 带有上下文以及图片的流式交流输出
     *
     * @param value
     * @param emitter
     * @param memoryID
     */
    @Override
    public void webStreamChat(AiUserInput value, ResponseBodyEmitter emitter, String memoryID) {

        LlwChatMemory memory;

        if (memoryID == null) {
            return;
        }

        if (pool.get(memoryID) == null) {
            memory = getMemory(memoryID);
            pool.put(memoryID, memory);
        } else {
            memory = pool.get(memoryID);
        }

        ChatCollectionService chatCollectionService = SpringContextHolder.getBean(ChatCollectionService.class);
        if (chatCollectionService != null) {
            ChatBaseCollectionEntity entity = new ChatBaseCollectionEntity();
            entity.setMemoryid(memory.getChatID());
            entity.setContent(JSON.toJSONString(value));
            entity.setRole(LlwRoleEnum.USER.getValue());

            chatCollectionService.insert(entity);
        }

        streamChatWithMemory(value, new LlwStreamingResponseHandler(emitter, memory), memory);
    }

    @Override
    public void toolChat(String value, ResponseBodyEmitter emitter, String memoryID) {

        LlwChatMemory memory = getMemoryFromCache(memoryID);
        chatCollectionService.addUserMessage(memoryID, value);
        String reply = toolChatWithMemory(value, memory, new CalculatorTool());
        try {

            chatCollectionService.addAiMessage(memoryID, reply);
            memory.getChatMemory().add(new AiMessage(reply));

            emitter.send(JSON.toJSONString(
                    new LlwStreamResponse(
                            UUIDUtil.generateUUID(),
                            memoryID,
                            reply,
                            LlwStreamResponseStatusEnum.COMPLETE
                    )
            ).trim());
            emitter.complete();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
