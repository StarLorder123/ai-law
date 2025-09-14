package com.law.rag.tools;

import com.law.rag.entity.TestEntity;
import com.law.rag.mapper.TestMapper;
import com.law.rag.util.SpringContextHolder;
import dev.langchain4j.agent.tool.Tool;

/**
 * 这是一个测试的Tool
 */
public class CalculatorTool {
    @Tool("Calculates the length of a string")
    int stringLength(String s) {
        System.out.println("Called stringLength() with s='" + s + "'");


        return s.length();
    }

    @Tool("Calculates the sum of two numbers")
    int add(int a, int b) {
        System.out.println("Called add() with a=" + a + ", b=" + b);
        return a + b;
    }

    @Tool("Calculates the square root of a number")
    double sqrt(int x) {
        System.out.println("Called sqrt() with x=" + x);
        return Math.sqrt(x);
    }

    @Tool("Add Email")
    String addEmail(String name, int num) {
        System.out.println("Called addEmail() with name='" + name + "'");
        TestEntity testEntity = new TestEntity();
        testEntity.setId(num);
        testEntity.setUsername(name);

        TestMapper testMapper = SpringContextHolder.getBean(TestMapper.class);
        testMapper.insert(testEntity);
        return "Add Email Successfully";
    }

    @Tool("发送邮件")
    String sendEmail(String email, String content) {
        System.out.println("Called sendEmail() with email='" + email + "', content='" + content + "'");
        return "Search E-mail address first, then do send e-mail";
    }

    @Tool("查询邮箱地址")
    String searchEmail(String name, int id) {
        TestMapper testMapper = SpringContextHolder.getBean(TestMapper.class);
        TestEntity testEntity = testMapper.selectById(id);
        System.out.println("Called searchEmail() with name='" + testEntity.getUsername() + "'");
        return "查询到的邮箱为：" + testEntity.getUsername() + "@163.com";
    }

    @Tool("Do send Email")
    String doSendEmail(String email) {
        System.out.println("Called doSendEmail() with email='" + email);
        return "send successfully";
    }
}
