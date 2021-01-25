/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.jwt;

import java.io.IOException;
import java.security.SignatureException;
import java.util.ArrayList;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.nusino.microservices.service.buscalendar.UserAdminService;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class AuthTokenFilter extends OncePerRequestFilter implements JwtHeaderInfo {
	@Autowired
	private JwtUtils jwtUtils;

	@Autowired
	private UserAdminService userAdminService;

	private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		try {
			String jwt = parseJwt(request);
			if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
				String userId = jwtUtils.getUserNameFromJwtToken(jwt);

				UserDetails userDetails = userAdminService.loadUserByUsername(userId);
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities());
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		} catch (Exception e) {
			logger.error("Cannot set user authentication: {}", e);
		}

		filterChain.doFilter(request, response);
	}

	private String parseJwt(HttpServletRequest request) {
		String headerAuth = request.getHeader(HEADER_NAME);
		if (StringUtils.hasText(headerAuth) && headerAuth.startsWith(HEADER_BEAR)) {
			return headerAuth.substring(7, headerAuth.length());
		}

		return null;
	}


}
