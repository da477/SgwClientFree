package org.da477.SgwClientFree;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * SgwClientApplication сlass
 *
 * This class is the main entry point for the Spring Boot application.
 * It configures the application with Vaadin UI, scheduling, asynchronous processing,
 * and transaction management.
 *
 * @author da477
 * @version 1.0
 * @since 5/16/24
 */
@SpringBootApplication
@Theme(value = "vaadinstart")
@EnableScheduling
@EnableAsync
@EnableTransactionManagement
public class SgwClientFreeApp extends SpringBootServletInitializer implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(SgwClientFreeApp.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(SgwClientFreeApp.class);
    }
}
