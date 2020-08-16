package com.honezhi.sso.server;

import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.async.DeferredResult;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * <p>@author: tsb </p>
 * <p>@since: 2020-08-16 19:17 </p>
 *
 * @Description Swagger的配置类
 **/
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    /**
     * 创建API应用
     * apiInfo() 增加API相关信息
     * 通过select()函数返回一个ApiSelectorBuilder实例,用来控制哪些接口暴露给Swagger来展现，
     * 本例采用指定扫描的包路径来定义指定要建立API的目录。
     */
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .genericModelSubstitutes(DeferredResult.class)
                .useDefaultResponseMessages(false)
                .forCodeGeneration(false)
                .pathMapping("/")
                // .host("localhost:9093")  // 不配的话，默认当前项目端口
                .apiInfo(apiInfo())
                .select() // 选择哪些路径和api会生成document
                .apis(RequestHandlerSelectors.any())// 对所有api进行监控
                // .apis(RequestHandlerSelectors.basePackage("com.honezhi.sso.server.controller"))    // 选择监控的package
                // .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))          // 只监控有ApiOperation注解的接口
                // 不显示错误的接口地址
                .paths(Predicates.not(PathSelectors.regex("/error.*")))     // 错误路径不监控
                .paths(PathSelectors.regex("/.*"))      // 对根下所有路径进行监控
                .build();
    }

    /**
     * 创建该API的基本信息（这些基本信息会展现在文档页面中）
     * 访问地址：http://项目实际地址/swagger-ui.html
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("SSO单点登录")
                .description("SSO单点登录项目接口文档")
                .termsOfServiceUrl("http://www.honezhi.com")
                // .contact(new Contact("tsb", "http://www.honezhi.com", "tangshengbo@honezhi.com"))
                .contact("tangshengbo@honezhi.com")
                .version("1.0")
                .build();
    }

}
