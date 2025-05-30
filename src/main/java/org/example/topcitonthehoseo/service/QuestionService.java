package org.example.topcitonthehoseo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.topcitonthehoseo.dto.request.SaveQuestion;
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

    @Transactional
    protected void saveQuestion(Long userId, SaveQuestion saveQuestion) throws JsonProcessingException {

        log.debug("saveQuestion service in");

        Integer lectureId = saveQuestion.getLectureId();
        String saveAnswerKey = "quiz:" + userId + ":" + lectureId + ":" + saveQuestion.getQuestionId();
        String getQuestionKey = "quiz:" + userId + ":" + lectureId;

        String getQuestion = redisTemplate.opsForList().index(getQuestionKey, saveQuestion.getQuestionNumber());

        if(getQuestion == null) {
            throw new NoSuchElementException("해당 문제를 찾을 수 없습니다.");
        }

        GetQuestion saveAnswer = objectMapper.readValue(getQuestion, GetQuestion.class);

        saveAnswer.setUserAnswer(saveQuestion.getUserAnswer());
        String saveAnswerJson = objectMapper.writeValueAsString(saveAnswer);

        redisTemplate.opsForList().rightPush(saveAnswerKey, saveAnswerJson);
    }

    @Transactional
    public GetQuestion getQuestion(Long questionId, Long userId, SaveQuestion saveQuestion) throws JsonProcessingException {

        log.debug("getQuestion service in");

        saveQuestion(userId, saveQuestion);

        String getQuestionKey = "quiz:" + userId + ":" + saveQuestion.getLectureId();

        List<String> questionJsonList = redisTemplate.opsForList().range(getQuestionKey, 0, -1);

        if (questionJsonList == null || questionJsonList.isEmpty()) {
            throw new IllegalStateException("저장된 문제가 없습니다.");
        }

        for (String json : questionJsonList) {
            GetQuestion returnQuestion = objectMapper.readValue(json, GetQuestion.class);
            if (returnQuestion.getQuestionId().equals(questionId)) {
                return returnQuestion;
            }
        }

        throw new NoSuchElementException("해당 questionId를 가진 문제를 찾을 수 없습니다.");
    }

    @Transactional
    public void submitQuestion(Long userId, SaveQuestion saveQuestion) throws JsonProcessingException {

        log.debug("submitQuestion service in");

        saveQuestion(userId, saveQuestion);

        String pattern = "quiz:" + userId + ":" + saveQuestion.getLectureId() + ":*";
        Set<String> keys = redisTemplate.keys(pattern); // 모든 키 가져오기

        if (keys.isEmpty()) {
            throw new IllegalStateException("채점할 문제가 없습니다.");
        }

        for (String key : keys) {
            String[] parts = key.split(":");
            Long questionId = Long.valueOf(parts[3]);

            // REDIS
            String json = redisTemplate.opsForValue().get(key);
            if (json == null) continue;

            GetQuestion getQuestion = objectMapper.readValue(json, GetQuestion.class);

            // MYSQL
            Question question = questionRepository.findById(questionId)
                    .orElseThrow(() -> new NoSuchElementException("해당 문제 없음: " + questionId));

            String correctAnswer = "";

            if (question.isQuestionType()) {

                Map<Integer, String> options = getQuestion.getOptions();
                Options correctOption = optionRepository.findByQuestion_QuestionIdAndCorrect(getQuestion.getQuestionId(), true);

                for (Integer optionId : options.keySet()) {

                    if (String.valueOf(options.get(optionId)).equals(correctOption.getOptionContent())) {
                        correctAnswer = String.valueOf(optionId);
                        break;
                    }
                }

            }
            else {
                correctAnswer = question.getQuestionContent();
            }

            String userAnswer = getQuestion.getUserAnswer();

            Boolean isCorrect = correctAnswer.equals(userAnswer);

            getQuestion.setCorrectAnswer(correctAnswer);
            getQuestion.setIsCorrect(isCorrect);

            String updatedJson = objectMapper.writeValueAsString(getQuestion);
            redisTemplate.opsForValue().set(key, updatedJson);

        }
    }

    @Transactional
    public GetQuestion getQuestionResult(Long questionId, Long userId) throws JsonProcessingException {

        log.debug("getQuestionResult service in");

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("해당 문제를 찾을 수 없습니다."));

        String key = "quiz:" + userId + ":" + question.getLecture().getLectureId() + ":" + questionId;
        String resultJson = redisTemplate.opsForValue().get(key);

        return objectMapper.readValue(resultJson, GetQuestion.class);
    }
}
