package com.law.rag.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.sql.Timestamp;

@Data
@TableName("public.file")
public class FileEntity {

    @Id
    private String fileid;

    private String filename;

    private String path;

    private String type;

    private Long size;

    private String sum;

    private Timestamp createAt;
}
