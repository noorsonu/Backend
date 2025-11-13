package com.main.serviceImpls;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.main.services.SmsService;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import jakarta.annotation.PostConstruct;

@Service
public class TwilioSmsService implements SmsService {

    @Value("${twilio.accountSid:}")
    private String accountSid;

    @Value("${twilio.authToken:}")
    private String authToken;

    @Value("${twilio.fromNumber:}")
    private String fromNumber;

    private volatile boolean initialized = false;

    @PostConstruct
    public void init() {
        if (accountSid != null && !accountSid.isBlank() && authToken != null && !authToken.isBlank()) {
            Twilio.init(accountSid, authToken);
            initialized = true;
        }
    }

    @Override
    public void sendSms(String to, String message) {
        try {
            if (!initialized) init();
            if (!initialized) {
                System.err.println("[SMS] Twilio not configured");
                return;
            }
            Message.creator(new PhoneNumber(to), new PhoneNumber(fromNumber), message).create();
        } catch (Exception e) {
            System.err.println("[SMS] Failed to send: " + e.getMessage());
        }
    }
}
