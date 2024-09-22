package org.foodorder.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class OpenAPIConfig {

  @Value("${openapi.dev-url}")
  private String devUrl;

  @Bean
  public GroupedOpenApi publicApi() {
    return GroupedOpenApi.builder().group("public-api").pathsToMatch("/api/**").build();
  }

  @Bean
  public OpenAPI myOpenAPI() {
    Server devServer = new Server();
    devServer.setUrl(devUrl);
    devServer.setDescription("Server URL in Development environment");

    Contact contact = new Contact();
    contact.setEmail("food-ordering@gmail.com");
    contact.setName("food-ordering");
    contact.setUrl("https://www.food-ordering.com");

    License mitLicense = new License().name("MIT License").url("https://choosealicense.com/licenses/mit/");

    Info info = new Info().title("Demo project for Food Ordering System.")
        .version("1.0")
        .contact(contact)
        .description("Demo project for Food Ordering System.")
        .termsOfService("https://www.food-ordering.com/terms")
        .license(mitLicense);

    return new OpenAPI().info(info).servers(List.of(devServer));
  }
}
