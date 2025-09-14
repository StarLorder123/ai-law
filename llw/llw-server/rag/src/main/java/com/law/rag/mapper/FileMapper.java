package com.law.rag.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.law.rag.entity.FileEntity;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface FileMapper extends BaseMapper<FileEntity> {

    @Select("SELECT * FROM public.file WHERE fileid = #{fileid}")
    FileEntity findByFileId(String fileid);

    // 使用 @Select 注解查询多个 FileID 并返回 List<FileEntity>
    @Select("<script>" +
            "SELECT * FROM public.file WHERE fileid IN " +
            "<foreach item='item' collection='fileIds' open='(' separator=',' close=')'>" +
            "#{item}" +
            "</foreach>" +
            "</script>")
    List<FileEntity> getFilesByIds(List<String> fileIds);
}
