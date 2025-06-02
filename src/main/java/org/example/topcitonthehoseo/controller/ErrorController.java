package org.example.topcitonthehoseo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.topcitonthehoseo.dto.request.ReceiveErrorRequestDto;
import org.example.topcitonthehoseo.service.ErrorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/error")
@RequiredArgsConstructor
public class ErrorController {

    private final ErrorService errorService;

    @PostMapping("/{questionId}")
    public ResponseEntity<String> receiveError(@PathVariable Long questionId, @RequestBody ReceiveErrorRequestDto receiveErrorRequestDto) {

        log.info("Receive error Controller");

        Long userId = 1L;
        errorService.receiveError(questionId, userId, receiveErrorRequestDto);

        return ResponseEntity.ok().body("오류 신고가 완료되었습니다.");
    }

}
