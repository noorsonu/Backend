package com.main.serviceImpls;

import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.main.entities.OtpCode;
import com.main.entities.UserEntity;
import com.main.enums.OtpPurpose;
import com.main.enums.OtpChannel;
import com.main.repositories.OtpCodeRepository;
import com.main.services.OtpService;
import com.main.services.SmsService;
import com.main.services.MailService;

@Service
public class OtpServiceImpl implements OtpService {

    @Autowired
    private OtpCodeRepository otpRepo;

    @Autowired private SmsService smsService;
    
    @Autowired private MailService mailService;

    private String generate6Digit() {
        int code = ThreadLocalRandom.current().nextInt(100000, 1000000);
        return String.valueOf(code);
    }

    @Override
    public String requestProfileUpdateOtp(UserEntity user) {
        return requestOtp(user, OtpPurpose.PROFILE_UPDATE, OtpChannel.SMS);
    }

    @Override
    public boolean verifyProfileUpdateOtp(UserEntity user, String code) {
        return verifyOtp(user, OtpPurpose.PROFILE_UPDATE, OtpChannel.SMS, code);
    }

    @Override
    public String requestOtp(UserEntity user, OtpPurpose purpose, OtpChannel channel) {
        String code = generate6Digit();
        OtpCode otp = new OtpCode();
        otp.setUser(user);
        otp.setCode(code);
        otp.setPurpose(purpose);
        otp.setChannel(channel);
        otp.setExpiresAt(Instant.now().plusSeconds(300)); // 5 minutes
        otp.setConsumed(false);
        otpRepo.save(otp);
        String message = "Your OTP code is " + code + ". It expires in 5 minutes.";
        if (channel == OtpChannel.SMS) {
            if (user.getPhoneNumber() == null || user.getPhoneNumber().isBlank()) {
                System.err.println("[OTP][SMS] No phone number for user");
            } else {
                smsService.sendSms(user.getPhoneNumber(), message);
            }
        } else {
            if (user.getEmail() == null || user.getEmail().isBlank()) {
                System.err.println("[OTP][EMAIL] No email for user");
            } else {
                mailService.sendEmail(user.getEmail(), "Your OTP Code", message);
            }
        }
        return code;
    }

    @Override
    public boolean verifyOtp(UserEntity user, OtpPurpose purpose, OtpChannel channel, String code) {
        return otpRepo
                .findTopByUserAndPurposeAndChannelAndCodeAndConsumedFalseAndExpiresAtAfterOrderByCreatedAtDesc(
                        user, purpose, channel, code, Instant.now())
                .map(match -> {
                    match.setConsumed(true);
                    otpRepo.save(match);
                    return true;
                })
                .orElse(false);
    }
}
