package com.main.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;

import com.main.dtos.AdminSingleConfirmDto;
import com.main.dtos.SetPhoneRequest;
import com.main.dtos.UserDto;
import com.main.entities.UserEntity;
import com.main.repositories.UserRepository;
import com.main.services.OtpService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class ProfileController {

    @Autowired 
    private UserRepository userRepository;
    
    @Autowired 
    private OtpService otpService;
    
    @Autowired 
    private PasswordEncoder passwordEncoder;

    private UserEntity currentUser(Authentication auth) {
        return userRepository.findByEmail(auth.getName()).orElseThrow();
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getMyProfile(Authentication auth) {
        UserEntity me = currentUser(auth);
        UserDto userDto = new UserDto(me.getId(), me.getName(), me.getEmail(), me.getUserType(), null); // Password is null for security
        return ResponseEntity.ok(userDto);
    }

    
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/request-otp-sms")
    public ResponseEntity<?> requestOtp(Authentication auth) {
        UserEntity me = currentUser(auth);
        if (me.getPhoneNumber() == null || me.getPhoneNumber().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Admin has no phoneNumber set. Set phoneNumber first.");
        }
        otpService.requestProfileUpdateOtp(me);
        return ResponseEntity.ok().body("OTP sent to your phone number");
    }

    // Admin requests OTP by email only
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/request-otp-email")
    public ResponseEntity<?> requestOtpEmail(Authentication auth) {
        UserEntity me = currentUser(auth);
        if (me.getEmail() == null || me.getEmail().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Admin has no email set.");
        }
        otpService.requestOtp(me, com.main.enums.OtpPurpose.PROFILE_UPDATE, com.main.enums.OtpChannel.EMAIL);
        return ResponseEntity.ok().body("OTP sent to your email");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/confirm-update-single")
    public ResponseEntity<?> confirmUpdateSingle(@Valid @RequestBody AdminSingleConfirmDto body,
                                                 Authentication auth) {
        UserEntity me = currentUser(auth);
        boolean usePhone;
        if ("phone".equalsIgnoreCase(body.getChannel())) {
            usePhone = true;
        } else if ("email".equalsIgnoreCase(body.getChannel())) {
            usePhone = false;
        } else {
            return ResponseEntity.badRequest().body("channel must be 'phone' or 'email'");
        }

        boolean ok = otpService.verifyOtp(me, com.main.enums.OtpPurpose.PROFILE_UPDATE,
                usePhone ? com.main.enums.OtpChannel.SMS : com.main.enums.OtpChannel.EMAIL,
                body.getCode());
        if (!ok) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired OTP");
        }

        boolean wantsPassword = body.getNewPassword() != null && !body.getNewPassword().isBlank();
        boolean wantsEmail = body.getNewEmail() != null && !body.getNewEmail().isBlank();
        boolean wantsPhone = body.getNewPhoneNumber() != null && !body.getNewPhoneNumber().isBlank();

        // Cross-channel enforcement
        if (wantsPhone && usePhone) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Changing phone number requires email OTP");
        }
        if (wantsEmail && !usePhone) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Changing email requires phone OTP");
        }
        if (wantsEmail && wantsPhone) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Change email and phone in separate requests");
        }

        // Uniqueness checks
        if (wantsEmail) {
            if (!body.getNewEmail().equalsIgnoreCase(me.getEmail()) && userRepository.existsByEmail(body.getNewEmail())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already in use");
            }
            me.setEmail(body.getNewEmail());
        }
        if (wantsPhone) {
            if (me.getPhoneNumber() == null || !body.getNewPhoneNumber().equals(me.getPhoneNumber())) {
                if (userRepository.existsByPhoneNumber(body.getNewPhoneNumber())) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).body("Phone number already in use");
                }
            }
            me.setPhoneNumber(body.getNewPhoneNumber());
        }
        if (wantsPassword) {
            me.setPassword(passwordEncoder.encode(body.getNewPassword()));
        }

        userRepository.save(me);
        return ResponseEntity.ok().body("Profile updated successfully");
    }

    // Admin sets/updates own phone number (no OTP, admin-only). Useful for first setup.
//    @PreAuthorize("hasRole('ADMIN')")
//    @PutMapping("/set-phone")
//    public ResponseEntity<?> setPhone(@Valid @RequestBody SetPhoneRequest body,
//                                      Authentication auth) {
//        UserEntity me = currentUser(auth);
//        if (body.getPhoneNumber() == null || body.getPhoneNumber().isBlank()) {
//            return ResponseEntity.badRequest().body("phoneNumber is required");
//        }
//        // Uniqueness check
//        if (me.getPhoneNumber() == null || !body.getPhoneNumber().equals(me.getPhoneNumber())) {
//            if (userRepository.existsByPhoneNumber(body.getPhoneNumber())) {
//                return ResponseEntity.status(HttpStatus.CONFLICT).body("Phone number already in use");
//            }
//        }
//        me.setPhoneNumber(body.getPhoneNumber());
//        userRepository.save(me);
//        return ResponseEntity.ok().body("Phone number set");
//    }
}
