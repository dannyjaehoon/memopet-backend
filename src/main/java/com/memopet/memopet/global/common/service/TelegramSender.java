package com.memopet.memopet.global.common.service;

import com.memopet.memopet.global.common.dto.EmailMessageDto;
import com.memopet.memopet.global.common.exception.BadRequestRuntimeException;
import com.memopet.memopet.global.configproperties.ConfigTelegram;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
@Slf4j
@RequiredArgsConstructor
public class TelegramSender extends TelegramLongPollingBot {
    private final ConfigTelegram configTelegram;

    public void sendFailureNotification(String message)  {
        log.info("Send a noti to telegram");

        var response = new SendMessage(configTelegram.getChatId(), message);
        try {
            execute(response);  // 429 Too Many Requests 에러 발생. 1분에 20번 밖에 못보내요...
        } catch (Exception e) {
//            throw new BadRequestRuntimeException(e.getMessage());
        }

    }

    @Override
    public String getBotUsername() {
        return configTelegram.getName();
    }

    @Override
    public String getBotToken() {
        return configTelegram.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        // 수신시 처리 로직
    }
}
