package com.law.rag.controller;

import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.law.rag.dto.AiStreamMultiMemoryRequest;
import com.law.rag.entity.ChatBaseEntity;
import com.law.rag.service.ChatCollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import com.law.rag.dto.AiStreamMemoryRequest;
import com.law.rag.service.LLMModelService;

@RestController
@RequestMapping("/v1")
public class AiStreamController {

    @Autowired
    private LLMModelService llmModelService;

    @Autowired
    private ChatCollectionService chatCollectionService;

    @GetMapping("/ai-stream")
    public ResponseBodyEmitter streamAiResponse() {
        System.out.println("ai-stream request");
        ResponseBodyEmitter emitter = new ResponseBodyEmitter();
        System.out.println("Default Charset: " + Charset.defaultCharset());


        ExecutorService executor = Executors.newWorkStealingPool();
        executor.submit(() -> {
            System.out.println(new Date().toString());
            String value = "请帮我写一篇500字关于父亲的文章？";
            // try {
            //     System.out.println(new String(value.getBytes("UTF-8"),"UTF-8"));
            // } catch (UnsupportedEncodingException e) {
            //     // TODO Auto-generated catch block
            //     e.printStackTrace();
            // }
            llmModelService.webStreamChat(value, emitter);
            // try {
            //     emitter.send(llmModelService.chat("你是谁？"));
            // } catch (IOException e) {
            //     // TODO Auto-generated catch block
            //     e.printStackTrace();
            // }
            // emitter.complete(); 
        });

        return emitter;
    }

    /**
     * Stream，Memory，Chat API
     * @param request
     * @return
     */
    @PostMapping("/ai-stream-memory")
    public ResponseBodyEmitter streamMemoryAiResponse(@RequestBody AiStreamMemoryRequest request) {
        System.out.println("ai-stream request");
        System.out.println("body:" + request.toString());
//        if (request.getIsNewMemory()) {
//
//            ChatBaseEntity chatBaseEntity = new ChatBaseEntity();
//
//            chatBaseEntity.setId(request.getMemoryID());
//            chatBaseEntity.setCaseid(request.getCaseID());
//            chatBaseEntity.setUserid(request.getUserID());
//            chatBaseEntity.setCreateAt(new Timestamp(new Date().getTime()));
//
//            chatCollectionService.insertBaseChat(chatBaseEntity);
//        }
        ResponseBodyEmitter emitter = new ResponseBodyEmitter();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
//            llmModelService.webStreamChat(
//                    request.getUserInput(),
//                    emitter,
//                    "".equals(request.getMemoryID()) ? null : request.getMemoryID());
            llmModelService.webStreamChat(request, emitter);
        });

        return emitter;
    }

    /**
     * Function Calling，Memory，Chat API
     * @param request
     * @return
     */
    @PostMapping("/ai-tool-memory")
    public ResponseBodyEmitter toolMemoryAiResponse(@RequestBody AiStreamMemoryRequest request) {

        ResponseBodyEmitter emitter = new ResponseBodyEmitter();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            llmModelService.toolChat(request.getUserInput(), emitter, request.getMemoryID());
        });

        return emitter;

    }

    @PostMapping("/ai-stream-multi-memory")
    public ResponseBodyEmitter streamMultiMemoryAiResponse(@RequestBody AiStreamMultiMemoryRequest request) {
        System.out.println("ai-stream request");
        System.out.println("body:" + request.toString());
        ResponseBodyEmitter emitter = new ResponseBodyEmitter();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            llmModelService.webStreamChat(
                    request.getUserInput(),
                    emitter,
                    "".equals(request.getMemoryID()) ? null : request.getMemoryID());
        });

        return emitter;
    }

}
