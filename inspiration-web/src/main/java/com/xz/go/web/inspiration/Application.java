package com.xz.go.web.inspiration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author liangjin 2020/06/25 9:37 下午
 */
@SpringBootApplication(scanBasePackages = "com.xz.go.*")
public class Application {
    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(Application.class, args);
    }
}
