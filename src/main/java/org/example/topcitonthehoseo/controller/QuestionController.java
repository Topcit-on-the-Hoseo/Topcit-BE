package org.example.topcitonthehoseo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.topcitonthehoseo.dto.request.LectureIdRequestDto;
import org.example.topcitonthehoseo.dto.request.SaveQuestion;
import org.example.topcitonthehoseo.dto.response.GetQuestion;
import org.example.topcitonthehoseo.service.QuestionService;
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

    @PostMapping("/{questionNumber}")
    public ResponseEntity<GetQuestion> getQuestion(@PathVariable Integer questionNumber, @RequestBody SaveQuestion saveQuestion) throws JsonProcessingException {

        log.info("Get question Controller");

        Long userId = 1L;
        GetQuestion data = questionService.getQuestion(questionNumber, userId, saveQuestion);

        return ResponseEntity.ok().body(data);
    }

    @PostMapping("/submit")
    public ResponseEntity<String> submitQuestion(@RequestBody SaveQuestion saveQuestion) throws JsonProcessingException {

        log.info("Submit question Controller");

        Long userId = 1L;
        questionService.submitQuestion(userId, saveQuestion);

        return ResponseEntity.ok().body("제출 완료");
    }

    @PostMapping("/result/{questionNumber}")
    public ResponseEntity<GetQuestion> getQuestionResult(@PathVariable Integer questionNumber, @RequestBody LectureIdRequestDto lectureIdRequestDto) throws JsonProcessingException {

        log.info("Get question result Controller");

        Long userId = 1L;
        GetQuestion data = questionService.getQuestionResult(questionNumber, userId, lectureIdRequestDto);

        return ResponseEntity.ok().body(data);
    }
}
