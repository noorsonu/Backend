package com.main.entities;

import java.time.Instant;

import com.main.enums.OtpPurpose;
import com.main.enums.OtpChannel;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class OtpCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private UserEntity user;

    @Column(nullable = false, length = 6)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OtpPurpose purpose;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OtpChannel channel;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private Boolean consumed = false;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();
}
