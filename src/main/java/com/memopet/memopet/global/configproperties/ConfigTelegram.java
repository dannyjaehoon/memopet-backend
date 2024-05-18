package com.memopet.memopet.global.configproperties;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "telegram")
@ConfigurationPropertiesScan
@Validated
@Component
@Getter
@Setter
public class ConfigTelegram {

    @NotBlank
    private String name;
    @NotBlank
    private String token;
    @NotBlank
    private String chatId;
}
