package com.law.rag.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.law.rag.vo.CaseAFileVo;
import com.law.rag.entity.CaseCollectionEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface CaseCollectionMapper extends BaseMapper<CaseCollectionEntity> {

    // 根据CaseID获取带有文件的查询信息
    @Select("""
            SELECT cc.*, f.filename, f.path, f.type, f.size, f.sum, f.create_at 
            FROM public.case_collection cc
            LEFT JOIN public.file f ON cc.content = f.fileid
            WHERE cc.id = #{id}
            """)
    CaseAFileVo getCaseCollectionWithFile(@Param("id") String id);
}
