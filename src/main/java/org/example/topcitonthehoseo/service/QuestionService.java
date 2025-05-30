package org.example.topcitonthehoseo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.topcitonthehoseo.dto.response.GetQuestion;
import org.example.topcitonthehoseo.entity.Options;
import org.example.topcitonthehoseo.entity.Question;
import org.example.topcitonthehoseo.entity.Topic;
import org.example.topcitonthehoseo.repository.OptionRepository;
import org.example.topcitonthehoseo.repository.QuestionRepository;
import org.example.topcitonthehoseo.repository.TopicRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionService {

    private final TopicRepository topicRepository;

    private final QuestionRepository questionRepository;

    private final OptionRepository optionRepository;

    private final ObjectMapper objectMapper;

    private final StringRedisTemplate redisTemplate;

    @Transactional
    public GetQuestion createQuestion(Integer lectureId, Long userId) throws JsonProcessingException {

        log.debug("createQuestion service in");

        // lectureId에 맞는 topicId 리스트로 가져오기
        List<Topic> topicList = topicRepository.findByLecture_LectureId(lectureId);
        List<Question> allQuestions = new ArrayList<>();
        List<GetQuestion> returnQuestions = new ArrayList<>();

        // topicId, lectureId에 맞는 문제 가져오기 (topicId 당 2개씩)
        for (Topic topic : topicList) {
            List<Question> questionList = questionRepository.findByTopic_TopicId(topic.getTopicId());

            if (questionList.isEmpty()) {
                log.debug("question list is empty : topic id = " + topic.getTopicId());
                continue;
            }

            Collections.shuffle(questionList);
            allQuestions.add(questionList.getFirst());
            allQuestions.add(questionList.getLast());
        }

        log.debug("topic list 다 돌고 난 후 allQuestions 리스트 개수 출력 : " + allQuestions.size());

        // 객관식인 애들은 Option 가져오기
        for (Question question : allQuestions) {
            if(question.isQuestionType()) {
                List<Options> options = optionRepository.findByQuestion_QuestionId(question.getQuestionId());
                Collections.shuffle(options);

                Integer index = 0;
                Map<Integer, String > optionMap = new HashMap<>();
                for (Options option : options) {
                    optionMap.put(index, option.getOptionContent());
                    index++;
                }

                GetQuestion getQuestion = GetQuestion.builder()
                        .questionId(question.getQuestionId())
                        .questionType(question.isQuestionType())
                        .questionContent(question.getQuestionContent())
                        .options(optionMap)
                        .correctRate(question.getCorrectRate())
                        .build();

                returnQuestions.add(getQuestion);

            } else {
                GetQuestion getQuestion = GetQuestion.builder()
                        .questionId(question.getQuestionId())
                        .questionType(question.isQuestionType())
                        .questionContent(question.getQuestionContent())
                        .correctRate(question.getCorrectRate())
                        .build();

                returnQuestions.add(getQuestion);
            }
        }

        // 리스트로 저장 후, 랜덤으로 돌리기
        Collections.shuffle(returnQuestions);

        Integer questionNum = 0;
        for (GetQuestion getQuestion : returnQuestions) {
            questionNum++;
            getQuestion.setQuestionNumber(questionNum);
        }

        // redis에 저장
        String key = "quiz:" + userId + ":" + lectureId;
        redisTemplate.delete(key);

        for (GetQuestion getQuestion : returnQuestions) {
            String jsonQuiz = objectMapper.writeValueAsString(getQuestion);
            redisTemplate.opsForList().rightPush(key, jsonQuiz);
        }

        // 1번 목록만 빼서 GetQuestion Type으로 return
        return returnQuestions.getFirst();
    }
}
