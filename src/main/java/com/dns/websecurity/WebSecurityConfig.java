package com.dns.websecurity;

import com.dns.service.UserLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutFilter;

/**
 * This is web security configuration class that extends {@link WebSecurityConfigurerAdapter}
 * Created by dhiren on 29/12/16.
 * @author dhiren
 * @since 29-12-2016
 * @see WebSecurityConfigurerAdapter
 * @see RestAuthenticationEntryPoint
 * @see RestAuthenticationSuccessHandler
 * @see RestAuthenticationFilter
 * @see UserLoginService
 */

@EnableWebSecurity
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    private final RestAuthenticationSuccessHandler authenticationSuccessHandler;

    private final RestAuthenticationFilter restAuthenticationFilter;

    private final UserLoginService userLoginService;


    /**
     * This is bean of {@link PasswordEncoder}
     * @return PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    public WebSecurityConfig(RestAuthenticationEntryPoint restAuthenticationEntryPoint, RestAuthenticationSuccessHandler authenticationSuccessHandler, RestAuthenticationFilter restAuthenticationFilter, UserLoginService userLoginService) {
        this.restAuthenticationEntryPoint = restAuthenticationEntryPoint;
        this.authenticationSuccessHandler = authenticationSuccessHandler;
        this.restAuthenticationFilter = restAuthenticationFilter;
        this.userLoginService = userLoginService;
    }

    /**
     * This is override method. It is used for set userDetailsService into {@link AuthenticationManagerBuilder}
     * @param auth {@link AuthenticationManagerBuilder}
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userLoginService).passwordEncoder(passwordEncoder());
    }


    /**
     * This is override method. It is used for configure http security.
     * In this method I am pass my {@link RestAuthenticationEntryPoint} , {@link RestAuthenticationSuccessHandler} and {@link RestAuthenticationFilter} .
     * also I will set a permit for signup except this anyRequest is authenticated
     * @param http {@link HttpSecurity}
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.headers().httpStrictTransportSecurity();
        http.csrf().disable().exceptionHandling().authenticationEntryPoint(restAuthenticationEntryPoint).and().authorizeRequests().antMatchers(HttpMethod.POST, "/sign-up/**").permitAll().anyRequest().authenticated().
                and().formLogin().loginProcessingUrl("/login").usernameParameter("username").passwordParameter("password").successHandler(authenticationSuccessHandler).failureHandler(new SimpleUrlAuthenticationFailureHandler()).
                and().logout().logoutUrl("/logout").logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler()).
                and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().httpBasic().disable();
        http.addFilterBefore(restAuthenticationFilter, LogoutFilter.class);
    }
}
