package org.foodorder.config;

//import org.springframework.context.annotation.Bean;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.web.SecurityFilterChain;
//
//import static org.springframework.security.config.Customizer.*;
//
//
//@EnableWebSecurity
//public class SecurityConfig {
//
//  @Bean
//  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
////    http.authorizeRequests(authorizeRequests -> authorizeRequests.anyRequest().authenticated())
////        .formLogin(withDefaults());
//    http.authorizeHttpRequests(authorizeRequests -> authorizeRequests.anyRequest().permitAll());
//    return http.build();
//  }
//}

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
            // Disable CSRF (optional for stateless APIs, use caution in real apps)
            .csrf(csrf -> csrf.disable())
            // Permit all requests without requiring authentication
            .authorizeHttpRequests(authorize -> authorize
                    .anyRequest().permitAll()
            )
            // Disable form login
            .formLogin(form -> form.disable())
            // Disable basic authentication
            .httpBasic(basic -> basic.disable());

    return http.build();
  }
}