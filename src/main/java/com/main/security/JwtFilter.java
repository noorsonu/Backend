package com.main.security;

import org.springframework.web.filter.OncePerRequestFilter;

import com.main.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtFilter extends OncePerRequestFilter {

	@Autowired
	private JwtUtils jwtUtils;
	
	@Lazy @Autowired
	private UserService userService;
	
	@Autowired
	private TokenBlackList tokenBlacklist;
	
	@Autowired
	private CookieUtil cookieUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {

		String header = request.getHeader("Authorization");
		String token = null;

		// 1) Try Authorization: Bearer <token>
		if (header != null && header.startsWith("Bearer ")) {
			token = header.substring(7);
		}
		// 2) Fallback to secure cookie AUTH_TOKEN
		if (token == null) {
			token = cookieUtil.getTokenFromCookies(request);
		}

		if (token != null && jwtUtils.validateToken(token) && !tokenBlacklist.isBlacklisted(token)) {
            String email = jwtUtils.extractUsername(token);
            List<String> roles = jwtUtils.extractRoles(token);
            UserDetails userDetails = userService.loadUserByEmail(email);

            List<SimpleGrantedAuthority> authorities = roles.stream()
                                                            .map(SimpleGrantedAuthority::new)
                                                            .collect(Collectors.toList());

			UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null,
					authorities);
			SecurityContextHolder.getContext().setAuthentication(auth);
		}

		chain.doFilter(request, response);
	}
}
