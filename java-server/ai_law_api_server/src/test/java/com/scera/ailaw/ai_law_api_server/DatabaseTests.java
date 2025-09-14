package com.scera.ailaw.ai_law_api_server;

import com.scera.ailaw.ai_law_api_server.dao.FileMetaDao;
import com.scera.ailaw.ai_law_api_server.entity.FileMeta;
import com.scera.ailaw.ai_law_api_server.entity.User;
import com.scera.ailaw.ai_law_api_server.utils.DateUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

@SpringBootTest
public class DatabaseTests {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private FileMetaDao fileMetaDao;

    @Test
    public void testMongoDB() {
        User user = new User();
        user.setId("3");
        user.setAge(3);
//        user.setName("hello world1");

        mongoTemplate.save(user);

        System.out.println(mongoTemplate.findAll(User.class));
    }

    @Test
    public void testFileMetaDao(){
        FileMeta fileMeta=new FileMeta();

        fileMeta.setFileID("123");
        fileMeta.setFileName("123");
        fileMeta.setType("123");
        fileMeta.setSize(123);
        fileMeta.setCreateTime(DateUtil.getNowDayDate());
        fileMetaDao.save(fileMeta);

        System.out.println(fileMetaDao.findAll());
    }

}
