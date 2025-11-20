package com.main.controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.main.dtos.AuthRequest;
import com.main.dtos.AuthResponse;
import com.main.dtos.RegisterRequest;
import com.main.security.CookieUtil;
import com.main.security.JwtUtils;
import com.main.security.TokenBlackList;
import com.main.services.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
//@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired 
    private AuthenticationManager authManager;
    @Autowired 
    private JwtUtils jwtUtils;
    @Autowired 
    private UserService userService;
    @Autowired 
    private TokenBlackList tokenBlacklist;
    @Autowired
    private CookieUtil cookieUtil;

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> register(@RequestBody @Valid RegisterRequest req) {
        try {
            userService.register(req);
            String message = "User registered successfully";
            return ResponseEntity.ok(Map.of("message", message));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    private ResponseEntity<?> performLogin(AuthRequest req, HttpServletResponse response) {
        try {
            Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(auth);
            String token = jwtUtils.generateToken((UserDetails) auth.getPrincipal());

            // Store JWT in secure, HttpOnly cookie
            cookieUtil.addSecureCookie(response, "AUTH_TOKEN", token);

            // Also return JWT in the response body for clients that need to store/use it directly
            return ResponseEntity.ok(new AuthResponse(token, "Login successful"));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid email or password"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Login failed: " + e.getMessage()));
        }
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(@RequestBody @Valid AuthRequest req, HttpServletResponse response) {
        return performLogin(req, response);
    }


    @PostMapping(value = "/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        String token = null;

        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            token = header.substring(7);
        }

        if (token == null) {
            token = cookieUtil.getTokenFromCookies(request);
        }

        if (token != null) {
            tokenBlacklist.blacklist(token);
            // delete auth cookie if present
            cookieUtil.deleteCookie(response, "AUTH_TOKEN");
            return ResponseEntity.ok(Map.of("message", "Logout successful"));
        }
        return ResponseEntity.badRequest().body(Map.of("error", "No token provided"));
    }
}
