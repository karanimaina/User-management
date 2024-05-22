package io.avania.io.usermanagement.security;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author David C Makuba
 * @created 31/01/2023
 **/
@Configuration
@Getter
public class AppProperties {
    @Value("${key.name}")
    private String name;
    @Value("${key.password}")
    private String password;
    @Value("${key.alias}")
    private String alias;

}
