package com.cake.fast.example.crqs;

import com.cake.framework.cqrs.bus.EventBus;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * TODO
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2022/11/2 22:48
 * @email 18668485565163.com
 */
@SpringBootApplication
public class Application {


    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Component
    static class Com implements CommandLineRunner {

        @Resource
        private ApplicationContext applicationContext;

        @Override
        public void run(String... args) throws Exception {
            System.out.println("AAA");
            EventBus bean = applicationContext.getBean(EventBus.class);
            System.out.println(bean);
        }
    }
}
