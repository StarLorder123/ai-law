package com.scera.ailaw.ai_law_api_server.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users") // 指定 MongoDB 中的集合名
@Data
public class User {

    @Id
    private String id; // MongoDB 的主键

    private String name;

    private Integer age;

}
