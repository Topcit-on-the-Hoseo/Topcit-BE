package org.example.topcitonthehoseo.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GoogleLoginRequestDto {
    private String credential;  // JWT or Google token
}
