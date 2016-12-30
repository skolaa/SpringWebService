package com.dns.websecurity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This is custom filter that extends {@link GenericFilterBean}.
 * Its main task if in header auth token is there generate the {@link Authentication} based on token and set to {@link SecurityContextHolder} context
 * Created by dhiren on 29/12/16.
 * @author dhiren
 * @since 29-12-2016
 * @see GenericFilterBean
 */

@Component
public class RestAuthenticationFilter extends GenericFilterBean {

    private static final Logger logger = LoggerFactory.getLogger(RestAuthenticationFilter.class);

    private final RestTokenHandlerService restTokenHandlerService;

    @Autowired
    public RestAuthenticationFilter(RestTokenHandlerService restTokenHandlerService) {
        this.restTokenHandlerService = restTokenHandlerService;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String token = request.getHeader("x-auth-token");
        if (token != null) {
            logger.info("Token : " + token);
            Authentication authentication = restTokenHandlerService.buildAuthentication(token, request);
            if (authentication != null) {
                logger.info("CONTEXT BEFORE SETTING : " + SecurityContextHolder.getContext());
                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.info("CONTEXT AFTER SETTING : " + SecurityContextHolder.getContext());
            }

        }

        filterChain.doFilter(request, response);
    }
}
