package com.skodin.mailservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendMessageDto {

    private String emailTo;
    private String emailSubject;
    private String confirmationLink;
    private String recipientName;

}
