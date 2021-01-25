/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.jwt;

import com.nusino.microservices.service.buscalendar.UserAdminService;
import com.nusino.microservices.vo.buscalendar.UserSecurityDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

@Component
public class UserTokenSession implements JwtHeaderInfo{
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private UserAdminService userAdminService;

    public String retrieveTokenUserId(HttpServletRequest request) {
        String jwt = parseJwt(request);
        if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
            return jwtUtils.getUserNameFromJwtToken(jwt);
        }
        return null;
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader(HEADER_NAME);
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith(HEADER_BEAR)) {
            return headerAuth.substring(7, headerAuth.length());
        }

        return null;
    }
}
