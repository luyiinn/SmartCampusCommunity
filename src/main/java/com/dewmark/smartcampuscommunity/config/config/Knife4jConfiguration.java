package com.dewmark.smartcampuscommunity.config.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * @description: Swagger/Knife4j文档配置类
 * @author: dewMark
 * @date: 2025/11/03
 **/
@Configuration
@Slf4j
public class Knife4jConfiguration implements WebMvcConfigurer {

    /**
     * 通过knife4j生成接口文档
     * @return
     */
    @Bean
    public Docket createRestApi() {
        log.info("准备生成接口文档...");
        return new Docket(DocumentationType.OAS_30) // 使用OpenAPI 3.0规范
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.dewmark.smartcampuscommunity.controller.user"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("智慧校园社区项目接口文档")
                .version("1.0")
                .description("智慧校园社区项目接口文档")
                .build();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置Swagger和Knife4j的资源处理器
        // Knife4j文档访问地址: http://localhost:8080/doc.html
        registry.addResourceHandler("doc.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        // Swagger UI访问地址: http://localhost:8080/swagger-ui/index.html
        registry.addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/springfox-swagger-ui/");
        // Webjars资源
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}
