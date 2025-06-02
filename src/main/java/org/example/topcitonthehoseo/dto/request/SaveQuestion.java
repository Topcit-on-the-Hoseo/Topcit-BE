package org.example.topcitonthehoseo.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class SaveQuestion {

    private Integer lectureId;

    private Long questionId;

    private Integer questionNumber;

    private String userAnswer;
}
