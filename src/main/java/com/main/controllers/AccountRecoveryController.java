//package com.main.controllers;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.main.dtos.AccountRecoveryConfirmDto;
//import com.main.entities.UserEntity;
//import com.main.enums.OtpChannel;
//import com.main.enums.OtpPurpose;
//import com.main.repositories.UserRepository;
//import com.main.services.OtpService;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import jakarta.validation.Valid;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/account")
//@CrossOrigin(origins = "*")
//public class AccountRecoveryController {
//
//    @Autowired
//    private UserRepository userRepository;
//    
//    @Autowired 
//    private OtpService otpService;
//    
//    @Autowired 
//    private PasswordEncoder passwordEncoder;
//
//    public static class OtpRequestDto {
//        public String by; // "phone" or "email"
//        public String identifier; // phoneNumber or email value
//    }
//
//    @PostMapping("/request-otp")
//    public ResponseEntity<?> requestOtp(@RequestBody OtpRequestDto body) {
//        if (body == null || body.by == null || body.identifier == null) {
//            return ResponseEntity.badRequest().body(Map.of("error", "by and identifier required"));
//        }
//        UserEntity user;
//        OtpChannel channel;
//        if ("phone".equalsIgnoreCase(body.by)) {
//            user = userRepository.findByPhoneNumber(body.identifier).orElse(null);
//            channel = OtpChannel.SMS;
//        } else if ("email".equalsIgnoreCase(body.by)) {
//            user = userRepository.findByEmail(body.identifier).orElse(null);
//            channel = OtpChannel.EMAIL;
//        } else {
//            return ResponseEntity.badRequest().body(Map.of("error", "by must be 'phone' or 'email'"));
//        }
//        if (user == null) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Account not found"));
//        }
//        otpService.requestOtp(user, OtpPurpose.ACCOUNT_RECOVERY, channel);
//        return ResponseEntity.ok(Map.of("message", "OTP sent"));
//    }
//
//    @PostMapping("/confirm-update")
//    public ResponseEntity<?> confirmUpdate(@Valid @RequestBody AccountRecoveryConfirmDto body) {
//        OtpChannel channel;
//        UserEntity user;
//        if ("phone".equalsIgnoreCase(body.getBy())) {
//            channel = OtpChannel.SMS;
//            user = userRepository.findByPhoneNumber(body.getIdentifier()).orElse(null);
//        } else if ("email".equalsIgnoreCase(body.getBy())) {
//            channel = OtpChannel.EMAIL;
//            user = userRepository.findByEmail(body.getIdentifier()).orElse(null);
//        } else {
//            return ResponseEntity.badRequest().body(Map.of("error", "by must be 'phone' or 'email'"));
//        }
//        if (user == null) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Account not found"));
//        }
//        boolean ok = otpService.verifyOtp(user, OtpPurpose.ACCOUNT_RECOVERY, channel, body.getCode());
//        if (!ok) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Invalid or expired OTP"));
//        }
//        // Apply updates with uniqueness checks
//        if (body.getNewEmail() != null && !body.getNewEmail().isBlank()) {
//            if (!body.getNewEmail().equalsIgnoreCase(user.getEmail()) && userRepository.existsByEmail(body.getNewEmail())) {
//                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "Email already in use"));
//            }
//            user.setEmail(body.getNewEmail());
//        }
//        if (body.getNewPhoneNumber() != null && !body.getNewPhoneNumber().isBlank()) {
//            if (user.getPhoneNumber() == null || !body.getNewPhoneNumber().equals(user.getPhoneNumber())) {
//                if (userRepository.existsByPhoneNumber(body.getNewPhoneNumber())) {
//                    return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "Phone number already in use"));
//                }
//            }
//            user.setPhoneNumber(body.getNewPhoneNumber());
//        }
//        if (body.getNewPassword() != null && !body.getNewPassword().isBlank()) {
//            user.setPassword(passwordEncoder.encode(body.getNewPassword()));
//        }
//        userRepository.save(user);
//        return ResponseEntity.ok(Map.of("message", "Account updated"));
//    }
//}
