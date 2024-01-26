package com.skodin.reactiveauthservice.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skodin.reactiveauthservice.dtos.SendMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class MailSandler {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @Value("${custom.rabbit.exchange}")
    private String exchange;

    @Value("${custom.rabbit.routing}")
    private String baseRouting;

    @SneakyThrows
    public void sendActivationCodeMessage(String to, String subject, String link, String userName) {

        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType("application/json");

        SendMessageDto sendMessageDto = new SendMessageDto(to, subject, link, userName);

        Message message = new Message(objectMapper.writeValueAsString(sendMessageDto).getBytes(), messageProperties);

        rabbitTemplate.send(exchange, baseRouting, message);
    }

}