package com.felipezea.product.security;

import com.felipezea.product.service.AuthenticationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.io.PrintWriter;

@Component
public class AuthenticationFilter extends GenericFilterBean {
    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationFilter(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }


    // Handling the request
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException
    {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String path = httpRequest.getRequestURI();

        String[] allowedPaths = {
                "/api/auth/",
                "/v3/api-docs",
                "/v3/api-docs/",
                "/swagger-ui/",
                "/swagger-ui.html",
                "/proxy/",
                "/actuator/"
        };

        boolean isAllowed = false;

        for (String allowedPath : allowedPaths) {
            // Whitelist check to allow Swagger/API docs to pass through
            if (path.startsWith(allowedPath))
            {
                isAllowed = true;
                break;
            }
        }

        if (isAllowed)
        {
            filterChain.doFilter(request, response);
            return; // Important: return after passing through
        }

        try
        {
            // Get the API key from the http request
            Authentication authentication = this.authenticationService.getAuthentication((HttpServletRequest) request);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);

        }
        catch (Exception e)
        {
            HttpServletResponse httpResponse = (HttpServletResponse) response;

            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);

            PrintWriter writer = response.getWriter();

            writer.println(e.getMessage());
            writer.flush();
            writer.close();
        }
    }
}
