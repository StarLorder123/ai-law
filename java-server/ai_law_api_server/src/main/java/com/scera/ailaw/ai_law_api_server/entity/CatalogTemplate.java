package com.scera.ailaw.ai_law_api_server.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.Map;

/**
 * 卷宗目录模板数据结构：
 * {
 * "_id": "66161b4f08463d001245d989",
 * "name": "刑事案件卷宗模板",
 * "type": "criminal",
 * "description": "用于标准刑事案件文书归档",
 * "version": 1,
 * "createdBy": "admin",
 * "createdAt": "2025-04-10T10:00:00.000Z",
 * "items": [
 * "66161b4f08463d001245d989-1":{
 * "index": 1,
 * "title": "收案表",
 * "required": true,
 * "category": "文书",
 * "description": "案件收案登记用表"
 * },
 * ...
 * ]
 * }
 */
@Data
@Document(collection = "catalog_template")
public class CatalogTemplate {

    /**
     * 模板唯一标识（MongoDB 主键 _id）
     */
    @Id
    private String id;

    /**
     * 模板名称，例如 “民事案件目录模板”
     */
    @Field("name")
    private String name;

    /**
     * 模板描述信息
     */
    @Field("description")
    private String description;

    /**
     * 模板版本号
     */
    @Field("version")
    private Integer version;

    /**
     * 创建人标识，例如用户ID或用户名
     */
    @Field("created_by")
    private String createdBy;

    /**
     * 模板创建时间
     */
    @Field("created_at")
    private Date createdAt;

    /**
     * 模板条目项，键为唯一标识或编号，值为具体条目信息
     */
    @Field("items")
    private Map<String, TemplateItem> items;

    /**
     * 模板条目类，描述每一项目录内容
     */
    @Data
    public static class TemplateItem {

        /**
         * 条目顺序编号，用于排序
         */
        @Field("index")
        private Integer index;

        /**
         * 条目的标题，例如“起诉状”或“判决书”
         */
        @Field("title")
        private String title;

        /**
         * 是否为必填项
         */
        @Field("required")
        private Boolean required;

        /**
         * 条目所属分类，例如“文书”或“证据”
         */
        @Field("category")
        private String category;

        /**
         * 条目的补充说明
         */
        @Field("description")
        private String description;
    }
}
