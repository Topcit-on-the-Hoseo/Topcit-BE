package org.example.topcitonthehoseo.dto.response;

import lombok.*;

import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class GetQuestion {

    private Long questionId;

    private Integer questionNumber;

    private boolean questionType;

    private String questionContent;

    private Map<Integer, String> options;

    private double correctRate;

    private String userAnswer;

    private String correctAnswer;

    private Boolean isCorrect;
}
