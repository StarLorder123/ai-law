package com.law.rag;

import lombok.Data;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.alibaba.fastjson2.JSON;
import java.util.List;

@SpringBootTest
public class CommonTests {

    @Data
    class MyEntity {
        private int id;
        private String name;

        public MyEntity() {}

        public MyEntity(int id, String name) {
            this.id = id;
            this.name = name;
        }

        // Getters and Setters
    }

    @Test
    public void testFastjson2_String2Object(){
        String jsonArray = "[{\"id\":1,\"name\":\"A\"},{\"id\":2,\"name\":\"B\"}]";
        List<MyEntity> entities = JSON.parseArray(jsonArray, MyEntity.class);
        for (MyEntity entity : entities) {
            System.out.println("ID: " + entity.getId() + ", Name: " + entity.getName());
        }
    }
}
