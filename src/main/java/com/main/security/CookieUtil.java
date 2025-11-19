package com.main.security;

import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CookieUtil {
	
	public void addSecureCookie(HttpServletResponse response, String name, String value) {
		Cookie cookie = new Cookie(name, value);
		cookie.setHttpOnly(true);          // Prevent JS access (XSS protection)
		cookie.setSecure(true);            // Only send over HTTPS
		cookie.setPath("/");              // Cookie valid for full domain
		cookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
		cookie.setAttribute("SameSite", "Strict");
		response.addCookie(cookie);
	}
	
	public void deleteCookie(HttpServletResponse response, String name) {
		Cookie cookie = new Cookie(name, null);
		cookie.setHttpOnly(true);
		cookie.setSecure(true);
		cookie.setPath("/");
		cookie.setMaxAge(0); // delete immediately
		cookie.setAttribute("SameSite", "Strict");
		response.addCookie(cookie);
	}
	
	public String getTokenFromCookies(HttpServletRequest request) {
		if (request.getCookies() == null) return null;
		for (Cookie cookie : request.getCookies()) {
			if ("AUTH_TOKEN".equals(cookie.getName())) {
				return cookie.getValue();
			}
		}
		return null;
	}

}
