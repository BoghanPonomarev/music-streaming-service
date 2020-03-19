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
@Order(1000)
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class SecurityFilter implements Filter {

    private final AdminTokenRepository adminTokenRepository;

    private String adminUrl = "/admin/";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        if(!req.getMethod().equals("OPTIONS")) {
            String requestURL = req.getRequestURL().toString();
            if (requestURL.contains(adminUrl) && !requestURL.contains("/login")) {
                String token = req.getHeader("Authorization");
                if (StringUtils.isEmpty(token) || !adminTokenRepository.findByToken(token).isPresent()) {
                    throw new NotAuthorizedException("Deny");
                }
            }
        }
        chain.doFilter(request,response);
    }

}
