package com.jing.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
@MapperScan("com.jing.admin.mapper")
public class AdminApplication {
    public static ApplicationContext context;
    public static void main(String[] args) {
        context = SpringApplication.run(AdminApplication.class, args);
    }

}
