package com.scera.ailaw.ai_law_api_server.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Data
@Document("case_catalog")
public class CaseCatalog {

    @Id
    @Field("_id")
    private String id;

    @Field("case_id")
    private String caseId; // 案件唯一编号，例如 "CASE-20250401-XYZ"

    @Field("template_id")
    private String templateId; // 对应的目录模板 ID

    @Field("generated_at")
    private Date generatedAt; // 卷宗实例创建时间

    @Field("template")
    private CatalogTemplate template;

    @Data
    public static class CatalogDocument {

        @Field("item_index")
        private String itemIndex; // 在模板中的顺序编号

        @Field("title")
        private String title; // 文书标题（如“收案表”）

        @Field("file_id")
        private String fileId; // 上传文件 ID

    }
}
