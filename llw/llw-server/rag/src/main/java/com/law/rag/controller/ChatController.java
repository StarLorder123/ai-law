package com.law.rag.controller;

import com.law.rag.dto.Response;
import com.law.rag.dto.ResponseCode;
import com.law.rag.entity.CaseBaseCollectionEntity;
import com.law.rag.entity.ChatBaseCollectionEntity;
import com.law.rag.entity.ChatBaseEntity;
import com.law.rag.service.ChatCollectionService;
import com.law.rag.vo.CaseQueryRequest;
import com.law.rag.vo.ChatQueryRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1")
public class ChatController {

    @Autowired
    private ChatCollectionService chatCollectionService;

    @PostMapping("/query_chat")
    public ResponseEntity<Response> queryCase(@RequestBody ChatQueryRequest chatQueryRequest) {
        try {
            Map<String, Object> map = new HashMap<>();

            List<ChatBaseCollectionEntity> list = chatCollectionService.queryByMemoryid(chatQueryRequest.getMemoryID());

            map.put("chatContentList", list);

            Response response = new Response();
            response.setCode(ResponseCode.SUCCESS.getCode());
            response.setData(map);

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            Response response = new Response();
            response.setCode(ResponseCode.FAILURE.getCode());
            response.setMessage(e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/query_chat_list")
    public ResponseEntity<Response> queryCaseList(@RequestBody ChatQueryRequest chatQueryRequest) {
        try {
            Map<String, Object> map = new HashMap<>();
            ChatBaseEntity chatBaseEntity = new ChatBaseEntity();

            chatBaseEntity.setCaseid(chatQueryRequest.getCaseID());
            chatBaseEntity.setCaseid(chatQueryRequest.getCaseID());

            List<ChatBaseEntity> list = chatCollectionService.queryByParams(chatBaseEntity);

            map.put("chatList", list);

            Response response = new Response();
            response.setCode(ResponseCode.SUCCESS.getCode());
            response.setData(map);

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            Response response = new Response();
            response.setCode(ResponseCode.FAILURE.getCode());
            response.setMessage(e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
