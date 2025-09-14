package com.law.rag.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
@TableName("public.case_base_collection")
public class CaseBaseCollectionEntity {

    @Id
    private String id;

    private String content;

    private String filePath;

    private String htmlContent;

    private String feature;
}
