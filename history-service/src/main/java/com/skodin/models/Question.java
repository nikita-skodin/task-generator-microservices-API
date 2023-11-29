package com.skodin.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Question {

    @Id
    @GeneratedValue()
    private Long id;

    private String question;

    private String answer;

    public Question(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }
}
