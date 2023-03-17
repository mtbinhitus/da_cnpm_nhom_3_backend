package com.example.examservice.security;

import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConfig  extends WebSecurityConfigurerAdapter implements ApplicationContextAware
{

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .anyRequest().permitAll();
        http.csrf().disable();
    }


//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//
//        http
//                .authorizeHttpRequests()
//                .anyRequest().permitAll();
//        return http.build();
//    }
    /**
     * Registers the KeycloakAuthenticationProvider with the authentication manager.
     */


    /**
     * Defines the session authentication strategy.
     */

//    @Bean
//    protected void configure(HttpSecurity http) throws Exception
//    {
//        http
//                .authorizeRequests()
////                .antMatchers("/test/**").hasRole("user")
////                .antMatchers("/admin/**").hasRole("admin")
//                .anyRequest().permitAll();
//        http.csrf().disable();
////        http.authorizeRequests()
//////                .antMatchers("/test").hasRole("user")
////                .anyRequest().authenticated()
////                .and()
////                .sessionManagement()
////                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
////                .and()
////                .cors()
////                .and()
////                .csrf().disable()
////                .oauth2ResourceServer()
////                .jwt();
//    }
}