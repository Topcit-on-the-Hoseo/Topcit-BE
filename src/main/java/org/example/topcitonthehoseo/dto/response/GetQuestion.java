package org.example.topcitonthehoseo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class GetQuestion {

    private boolean questionType;

    private String questionContent;

    private Map<Integer, String> options;

    private double correctRate;
}
