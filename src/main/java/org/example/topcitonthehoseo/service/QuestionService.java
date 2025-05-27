package org.example.topcitonthehoseo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.topcitonthehoseo.dto.response.GetQuestion;
import org.example.topcitonthehoseo.entity.Topic;
import org.example.topcitonthehoseo.repository.TopicRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionService {

    private final TopicRepository topicRepository;

    @Transactional
    public GetQuestion createQuestion(Integer lectureId) {

        // lectureId에 맞는 topicId 리스트로 가져오기
        Optional<Topic> topicList = topicRepository.findById(Long.valueOf(lectureId));

        // topicId, lectureId에 맞는 문제 가져오기 (topicId 당 2개씩)

        // 객관식인 애들은 Option 가져오기

        // 리스트로 저장 후, 랜덤으로 돌리기

        // redis에 저장

        // 1번 목록만 빼서 GetQuestion Type으로 return
        return GetQuestion.builder().build();
    }
}
