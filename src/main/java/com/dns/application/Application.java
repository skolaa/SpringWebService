package com.dns.application;

import com.dns.dao.UserEntityDao;
import com.dns.model.UserEntity;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.HashMap;
import java.util.Map;

/**
 * This is main entry class of this application
 * Created by dhiren on 29/12/16.
 * @author dhiren
 * @since 29-12-2016
 */

@SpringBootApplication
@EntityScan(basePackageClasses = {UserEntity.class})
@EnableSwagger2
@EnableTransactionManagement
@EnableJpaRepositories(basePackageClasses = {UserEntityDao.class})
@ComponentScan(basePackages = {"com.dns.*"})
public class Application {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }
}
