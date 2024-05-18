package com.memopet.memopet.global.common.service;

import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;
import org.springframework.stereotype.Service;

@Service
public class UserAgentService {

    private final UserAgentAnalyzer userAgentAnalyzer;

    public UserAgentService() {
        this.userAgentAnalyzer = UserAgentAnalyzer.newBuilder().build();
    }

    public UserAgent parseUserAgent(String userAgentString) {
        return userAgentAnalyzer.parse(userAgentString);
    }
}
