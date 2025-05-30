package org.example.topcitonthehoseo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.example.topcitonthehoseo.dto.request.SaveQuestion;
import org.example.topcitonthehoseo.dto.response.GetQuestion;
import org.example.topcitonthehoseo.service.QuestionService;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/quiz")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping("/lecture/{lectureId}")
    public ResponseEntity<GetQuestion> createQuestion(@PathVariable Integer lectureId) throws JsonProcessingException {

        log.info("Create question Controller");

        Long userId = 1L;
        GetQuestion data = questionService.createQuestion(lectureId, userId);

        return ResponseEntity.ok().body(data);
    }

    @PostMapping("/{questionId}")
    public ResponseEntity<GetQuestion> getQuestion(@PathVariable Long questionId, @RequestBody SaveQuestion saveQuestion) throws JsonProcessingException, ChangeSetPersister.NotFoundException {

        log.info("Get question Controller");

        Long userId = 1L;
        GetQuestion data = questionService.getQuestion(questionId, userId, saveQuestion);

        return ResponseEntity.ok().body(data);
    }

    @PostMapping("/submit")
    public ResponseEntity<String> submitQuestion(@RequestBody SaveQuestion saveQuestion) throws JsonProcessingException {

        log.info("Submit question Controller");

        Long userId = 1L;
        questionService.submitQuestion(userId, saveQuestion);

        return ResponseEntity.ok().body("제출 완료");
    }

    @PostMapping("/result/{questionId}")
    public ResponseEntity<GetQuestion> getQuestionResult(@PathVariable Long questionId) throws JsonProcessingException {

        log.info("Get question result Controller");

        Long userId = 1L;
        GetQuestion data = questionService.getQuestionResult(questionId, userId);

        return ResponseEntity.ok().body(data);
    }
}
