package com.skodin.DTO;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class QuestionDTO {

    private final String question;
    private final String answer;

}
