package com.project.configurations;

import lombok.extern.slf4j.Slf4j;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j(topic = "Access denied handler")
public class FrontendAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            authentication.getAuthorities().forEach(authority -> log.info("Authority: {}", authority.getAuthority()));
            log.warn(String.format(
                    "User \"%s\" tried to access protected URL \"%s\": %s",
                    authentication.getName(),
                    httpServletRequest.getRequestURI(),
                    e.getMessage()
            ));
        }
        httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + "/403");
    }
}
