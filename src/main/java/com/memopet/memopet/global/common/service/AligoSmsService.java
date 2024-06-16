package com.memopet.memopet.global.common.service;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AligoSmsService {

    @Value("${aligo.api.url}")
    private String apiUrl;

    @Value("${aligo.api.key}")
    private String apiKey;

    @Value("${aligo.user.id}")
    private String userId;

    @Value("${aligo.sender.phone}")
    private String senderPhone;

    private final RestTemplate restTemplate;


    public String sendSms(String recipientPhone, String message) {
        Map<String, String> params = new HashMap<>();
        params.put("key", apiKey);
        params.put("user_id", userId);
        params.put("sender", senderPhone);
        params.put("receiver", recipientPhone);
        params.put("msg", message);

        return restTemplate.postForObject(apiUrl, params, String.class);
    }
}
