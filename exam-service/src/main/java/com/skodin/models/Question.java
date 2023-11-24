package com.skodin.models;

import lombok.*;

@Getter
@ToString
@RequiredArgsConstructor
public class Question {

    private final String question;
    private final String answer;

}
