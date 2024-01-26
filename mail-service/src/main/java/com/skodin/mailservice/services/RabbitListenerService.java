package com.skodin.mailservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skodin.mailservice.dtos.SendMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class RabbitListenerService {

    private final MailService mailService;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = "${custom.rabbit.first-queue}")
    public void sendMessage(String jsonMessage) {
        try {
            log.info("Attempt to parse jsonMessage");
            SendMessageDto message = objectMapper.readValue(jsonMessage, SendMessageDto.class);

            mailService.sendActivationCodeMessage(message.getEmailTo(), message.getEmailSubject(),
                    message.getConfirmationLink(), message.getRecipientName());
        } catch (JsonProcessingException e) {
            log.error("Error sending mail: {}, {}", e.toString(), e);
        }
    }

}
