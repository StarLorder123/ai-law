package com.law.rag.controller;

import com.law.rag.dto.Response;
import com.law.rag.dto.ResponseCode;
import com.law.rag.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping("/uploadFile")
    public ResponseEntity<Response> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String fileid=fileService.insertFile(file);

            if(fileid==null){
                throw new Exception("Fail to save file");
            }

            Map<String,String> map=new HashMap<>();
            map.put("fileid",fileid);

            Response response=new Response();
            response.setCode(ResponseCode.SUCCESS.getCode());
            response.setData(map);

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            Response response=new Response();
            response.setCode(ResponseCode.FAILURE.getCode());
            response.setMessage(e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
