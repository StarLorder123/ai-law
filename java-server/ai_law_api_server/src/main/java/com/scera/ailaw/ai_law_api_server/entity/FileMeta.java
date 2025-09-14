package com.scera.ailaw.ai_law_api_server.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

/**
 * 文件元信息实体类，对应 MongoDB 中的 file_meta 集合。
 */
@Data
@Document(collection = "file_meta")
public class FileMeta {

    /**
     * 文件唯一标识，对应 MongoDB 的 _id 字段。
     */
    @Id
    private String fileID;

    /**
     * 文件名，例如 "contract_v2.pdf"。
     */
    @Field("file_name")
    private String fileName;

    /**
     * 文件类型，例如 "pdf", "jpg", "docx"。
     */
    @Field("type")
    private String type;

    /**
     * 文件大小，单位为字节（byte）。
     */
    @Field("size")
    private long size;

    /**
     * 文件的创建时间（上传时间），使用 Date 类型。
     */
    @Field("create_time")
    private Date createTime;

}
