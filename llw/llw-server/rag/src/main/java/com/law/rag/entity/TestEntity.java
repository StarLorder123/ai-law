package com.law.rag.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("public.test")
public class TestEntity {

    private Integer id;
    private String username;

}
