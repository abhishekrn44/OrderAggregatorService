package io.abhishek.orderaggservice;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain defaultpringSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/openapi/**", "/webjars/**", "/actuator/**").permitAll()
//                .antMatchers(POST, "/order/**").hasAuthority("SCOPE_write")
//                .antMatchers(DELETE, "/order/**").hasAuthority("SCOPE_write")
                .antMatchers(GET, "/order/**").hasAuthority("SCOPE_read")
                .anyRequest().authenticated()
                .and()
                .oauth2ResourceServer()
                .jwt();
        return http.build();
    }
}