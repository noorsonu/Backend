package com.main.repositories;

import java.time.Instant;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.main.entities.OtpCode;
import com.main.entities.UserEntity;
import com.main.enums.OtpPurpose;
import com.main.enums.OtpChannel;

public interface OtpCodeRepository extends JpaRepository<OtpCode, Long> {
    Optional<OtpCode> findTopByUserAndPurposeAndChannelAndCodeAndConsumedFalseAndExpiresAtAfterOrderByCreatedAtDesc(
            UserEntity user, OtpPurpose purpose, OtpChannel channel, String code, Instant now);
}
