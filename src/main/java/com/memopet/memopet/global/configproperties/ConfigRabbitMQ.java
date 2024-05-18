package com.memopet.memopet.global.configproperties;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "spring.rabbitmq")
@ConfigurationPropertiesScan
@Validated
@Component
@Getter
@Setter
public class ConfigRabbitMQ {

    @NotBlank
    private String host;
    @NotBlank
    private String port;
    @NotBlank
    private String username;
    @NotBlank
    private String password;
}
