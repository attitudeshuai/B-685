package com.example.unit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 单位管理系统主应用入口
 * 
 * @author System
 * @version 1.0.0
 */
@SpringBootApplication
public class UnitApplication {

    private static final Logger logger = LoggerFactory.getLogger(UnitApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(UnitApplication.class, args);
        logger.info("========================================");
        logger.info("  单位管理系统启动成功!");
        logger.info("  访问地址: http://localhost:8000");
        logger.info("========================================");
    }
}
