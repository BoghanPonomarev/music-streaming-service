package com.service.web.filter;

import com.service.dao.AdminTokenRepository;
import com.service.exception.NotAuthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class SecurityFilter implements Filter {

    private static final String TOKEN_HEADER_KEY = "Authorization";
    private static final String OPTION_METHOD_NAME = "OPTIONS";
    private static final String ADMIN_URL = "/admin/";
    private static final String LOGIN_URL = "/login";

    private final AdminTokenRepository adminTokenRepository;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        if(!httpRequest.getMethod().equals(OPTION_METHOD_NAME)) {
            handleSecuredMethods(httpRequest);
        }
        chain.doFilter(request,response);
    }

    private void handleSecuredMethods(HttpServletRequest request ) {
        String requestURL = request.getRequestURL().toString();
        if (requestURL.contains(ADMIN_URL) && !requestURL.contains(LOGIN_URL)) {
            String token = request.getHeader(TOKEN_HEADER_KEY);
            if (StringUtils.isEmpty(token) || !adminTokenRepository.findByToken(token).isPresent()) {
                throw new NotAuthorizedException("Deny");
            }
        }
    }

}
