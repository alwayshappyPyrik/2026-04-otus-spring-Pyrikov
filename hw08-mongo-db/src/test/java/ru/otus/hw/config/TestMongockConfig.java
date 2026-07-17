package ru.otus.hw.config;

import io.mongock.driver.mongodb.springdata.v4.SpringDataMongoV4Driver;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.core.MongoTemplate;

@SpringBootApplication
@ComponentScan(basePackages = "ru.otus.hw")
public class TestMongockConfig {

    @Bean
    public SpringDataMongoV4Driver driver(MongoTemplate mongoTemplate) {
        return SpringDataMongoV4Driver.withDefaultLock(mongoTemplate);
    }
}
