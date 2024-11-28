package com.mss.checkin.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "IOB API", version = "v1",description = "Here you can find all the api details of IOB app. You can generate authentication token by passing login di and password. Once you have authorization token, then you can start test or build request body by calling each api. You no need to pass authorization token everytime to calling every api. Just pass the token in Authorization model, It will add the token to each api request. "))
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"

)
public class OpenApi30Config {
}
