package org.example.topcitonthehoseo.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RegisterRequestDto {
    private String studentId;
    private String password;
    private String email;
    private String nickname;
}
