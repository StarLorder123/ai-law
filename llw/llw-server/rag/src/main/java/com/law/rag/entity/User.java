package com.law.rag.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "users") // 指定 MongoDB 中的集合名
@Data
public class User {

    @Id
    private String id; // MongoDB 的主键

    private String name;

    private Integer age;

}
