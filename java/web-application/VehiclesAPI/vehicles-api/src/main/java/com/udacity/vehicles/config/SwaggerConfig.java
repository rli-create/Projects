package com.udacity.vehicles.config;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

@Configuration
@EnableSwagger2
@ApiResponses( value = {
    @ApiResponse(code=400, message = "Bad request, please follow the API documentation for usage"),
    @ApiResponse(code=401, message = "Due to security constraints, your access request cannot be authorized"),
    @ApiResponse(code=500, message = "The server is down, please make sure car service is running")
})
public class SwaggerConfig {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
                .useDefaultResponseMessages(false);
    }

    public ApiInfo apiInfo() {
        return new ApiInfo(
                    "Price API",
                    "This API return a price based on vehicle id.",
                    "1.0",
                    "http://www.aceflyer.com/tos",
                    new Contact("Ryan Li", "www.aceflyer.com", "aceflyer@gmail.com"),
                    "License of API", "http://www.aceflyer.com/license", Collections.emptyList()
        );
    }
}
