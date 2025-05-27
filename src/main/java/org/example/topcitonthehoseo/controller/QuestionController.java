package org.example.topcitonthehoseo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.topcitonthehoseo.dto.response.GetQuestion;
import org.example.topcitonthehoseo.service.QuestionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/quiz")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping("/lecture/{lectureId}")
    public ResponseEntity<GetQuestion> createQuestion(@PathVariable Integer lectureId) {

        log.info("Create question");

        //TODO. user id token

        GetQuestion data = questionService.createQuestion(lectureId);

        return ResponseEntity.ok().body(data);
    }
}
