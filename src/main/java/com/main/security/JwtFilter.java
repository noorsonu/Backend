package com.main.security;

import org.springframework.web.filter.OncePerRequestFilter;

import com.main.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

	@Autowired
	private JwtUtils jwtUtils;
	
	@Lazy @Autowired
	private UserService userService;
	
	@Autowired
	private TokenBlackList tokenBlacklist;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {

		String header = request.getHeader("Authorization");
		String token = null;

		if (header != null && header.startsWith("Bearer ")) {
			token = header.substring(7);
		}

		if (token != null && jwtUtils.validateToken(token) && !tokenBlacklist.isBlacklisted(token)) {
			String email = jwtUtils.extractUsername(token);
			UserDetails userDetails = userService.loadUserByEmail(email);
			UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null,
					userDetails.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(auth);
		}

		chain.doFilter(request, response);
	}
}
