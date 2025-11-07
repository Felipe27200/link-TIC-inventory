package com.felipezea.product.service;

import com.felipezea.product.security.AuthenticationMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    @Value("${auth.token.header.name}")
    private String AUTH_TOKEN_HEADER_NAME;
    @Value("${auth.token}")
    private String AUTH_TOKEN;

    public Authentication getAuthentication(HttpServletRequest request) {
        // Get the API key in the header's request
        String apiKey = request.getHeader(AUTH_TOKEN_HEADER_NAME);

        if (apiKey == null || apiKey.isEmpty() || !apiKey.equals(AUTH_TOKEN))
        {
            throw new BadCredentialsException("Invalid API Key");
        }

        return new AuthenticationMapper(apiKey, AuthorityUtils.NO_AUTHORITIES);
    }
}
