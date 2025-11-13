package com.main.services;

import com.main.entities.UserEntity;
import com.main.enums.OtpPurpose;
import com.main.enums.OtpChannel;

public interface OtpService {
    // Backward-compat helpers
    String requestProfileUpdateOtp(UserEntity user);
    boolean verifyProfileUpdateOtp(UserEntity user, String code);

    // New generic channel-aware API
    String requestOtp(UserEntity user, OtpPurpose purpose, OtpChannel channel);
    boolean verifyOtp(UserEntity user, OtpPurpose purpose, OtpChannel channel, String code);
}
