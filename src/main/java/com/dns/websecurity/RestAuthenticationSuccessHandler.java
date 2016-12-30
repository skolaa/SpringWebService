package com.dns.websecurity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This class is used for as success handler that extends {@link SimpleUrlAuthenticationSuccessHandler} . After success authentication that class generate a token
 * Created by dhiren on 29/12/16.
 * @author dhiren
 * @since 29-12-2016
 * @see WebSecurityConfig
 */

@Component
public class RestAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {


    private final Logger logger = LoggerFactory.getLogger(RestAuthenticationSuccessHandler.class);

    private final RestTokenHandlerService restTokenHandlerService;


    @Autowired
    public RestAuthenticationSuccessHandler(RestTokenHandlerService restTokenHandlerService) {
        this.restTokenHandlerService = restTokenHandlerService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        try
        {
            String jsonString = restTokenHandlerService.generateToken(authentication);
            response.getWriter().println(jsonString);
        }
        catch (Exception e)
        {
            logger.error("Error after authentication", e);
        }
    }
}
