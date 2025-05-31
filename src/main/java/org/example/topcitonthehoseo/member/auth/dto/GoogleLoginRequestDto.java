package org.example.topcitonthehoseo.member.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GoogleLoginRequestDto {
    private String credential;  // JWT or Google token
}
