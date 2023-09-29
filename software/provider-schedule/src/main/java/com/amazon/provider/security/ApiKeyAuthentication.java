package com.amazon.provider.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
public class ApiKeyAuthentication extends AbstractPreAuthenticatedProcessingFilter {
    private static final String API_KEY_HEADER_NAME = "X-API-KEY";

    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        return request.getHeader(API_KEY_HEADER_NAME);
    }
    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
        return null;
    }
}