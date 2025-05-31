package org.example.topcitonthehoseo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.topcitonthehoseo.service.PrepareService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Paths;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PrepareController {

    private final PrepareService prepareService;

    @PostMapping("/quiz/prepare")
    public ResponseEntity<String> prepareQuestion() {

        log.info("Prepare Controller");

        prepareService.prepareFromDirectory(Paths.get("question"));

        return ResponseEntity.ok().body("Topcit 문제 Database에 저장 완료");
    }
}
