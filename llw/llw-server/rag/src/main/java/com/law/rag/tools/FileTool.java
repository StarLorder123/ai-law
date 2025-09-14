package com.law.rag.tools;

import com.law.rag.util.FileUtil;
import dev.langchain4j.agent.tool.Tool;

public class FileTool {

    @Tool("写文件")
    public String writeFile(String content){
        return FileUtil.writeFile("./test.txt", content);
    }
}
