package com.law.rag.controller;

import com.law.rag.dto.Response;
import com.law.rag.dto.ResponseCode;
import com.law.rag.entity.CaseBaseCollectionEntity;
import com.law.rag.service.CaseService;
import com.law.rag.vo.CaseQueryRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/v1")
public class CaseController {

    @Autowired
    private CaseService caseService;

    @PostMapping("/query_base_case")
    public ResponseEntity<Response> queryCase(@RequestBody CaseQueryRequest caseQueryRequest) {
        try {
            Map<String, Object> map = new HashMap<>();

            /*
             * 判断逻辑：
             * 1. 如果caseID不为空，则根据caseID查询，返回单个case对象
             * 2. 如果caseID为空，则查询所有case对象，返回列表
             */
            if (caseQueryRequest.getCaseID() != null) {
                CaseBaseCollectionEntity caseBaseCollectionEntity = caseService.getBaseCaseById(caseQueryRequest.getCaseID());

                map.put("case", caseBaseCollectionEntity);
            }else{
                List<CaseBaseCollectionEntity> list = caseService.getAllBaseCases();

                map.put("caseList", list);
            }

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
