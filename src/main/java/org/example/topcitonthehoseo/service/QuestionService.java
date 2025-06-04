package org.example.topcitonthehoseo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.topcitonthehoseo.dto.request.LectureIdRequestDto;
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
    private final RankService rankService;

    @Transactional
    public GetQuestion createQuestion(Integer lectureId, Long userId) throws JsonProcessingException {

        log.debug("createQuestion service in");

        List<Topic> topicList = topicRepository.findByLecture_LectureId(lectureId);
        List<Question> allQuestions = new ArrayList<>();
        List<GetQuestion> returnQuestions = new ArrayList<>();

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

        for (Question question : allQuestions) {
            if(question.isQuestionType()) {
                List<Options> options = optionRepository.findByQuestion_QuestionId(question.getQuestionId());
                Collections.shuffle(options);

                Integer index = 1; // 0 -> 1 수정
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

        Collections.shuffle(returnQuestions);

        Integer questionNum = 0;
        for (GetQuestion getQuestion : returnQuestions) {
            questionNum++;
            getQuestion.setQuestionNumber(questionNum);
        }

        String key = "quiz:" + userId + ":" + lectureId;
        redisTemplate.delete(key);

        for (GetQuestion getQuestion : returnQuestions) {
            String jsonQuiz = objectMapper.writeValueAsString(getQuestion);
            redisTemplate.opsForList().rightPush(key, jsonQuiz);
        }

        log.debug("create question service return {}, {}", returnQuestions.getFirst().getQuestionId(), returnQuestions.getFirst().getQuestionContent());

        return returnQuestions.getFirst();
    }

    @Transactional
    protected void saveQuestion(Long userId, SaveQuestion saveQuestion) throws JsonProcessingException {

        log.debug("saveQuestion service in, {}", saveQuestion.getQuestionNumber());

        Integer lectureId = saveQuestion.getLectureId();
        String saveAnswerKey = "quiz:" + userId + ":" + lectureId + ":" + saveQuestion.getQuestionNumber();
        String getQuestionKey = "quiz:" + userId + ":" + lectureId;

        String getQuestion = redisTemplate.opsForList().index(getQuestionKey, saveQuestion.getQuestionNumber() - 1);

        if(getQuestion == null) {
            throw new NoSuchElementException("해당 문제를 찾을 수 없습니다.");
        }

        GetQuestion saveAnswer = objectMapper.readValue(getQuestion, GetQuestion.class);
        saveAnswer.setUserAnswer(saveQuestion.getUserAnswer());
        String saveAnswerJson = objectMapper.writeValueAsString(saveAnswer);

        redisTemplate.delete(saveAnswerKey);
        redisTemplate.opsForList().rightPush(saveAnswerKey, saveAnswerJson);
    }

    @Transactional
    public GetQuestion getQuestion(Integer questionNumber, Long userId, SaveQuestion saveQuestion) throws JsonProcessingException {

        log.debug("getQuestion service in");

        saveQuestion(userId, saveQuestion);

        String getQuestionKey = "quiz:" + userId + ":" + saveQuestion.getLectureId();

        String getQuestionData = redisTemplate.opsForList().index(getQuestionKey, questionNumber - 1);

        if (Objects.requireNonNull(getQuestionData).isEmpty()) {
            throw new IllegalStateException("저장된 문제가 없습니다.");
        }

        return objectMapper.readValue(getQuestionData, GetQuestion.class);
    }

    @Transactional
    public void submitQuestion(Long userId, SaveQuestion saveQuestion) throws JsonProcessingException {

        log.debug("submitQuestion service in");

        saveQuestion(userId, saveQuestion);

        log.debug("submit save question out");

        String pattern = "quiz:" + userId + ":" + saveQuestion.getLectureId() + ":*";
        Set<String> keys = redisTemplate.keys(pattern);
        int totalScore = 0;

        if (Objects.requireNonNull(keys).isEmpty()) {
            throw new IllegalStateException("채점할 문제가 없습니다.");
        }

        for (String key : keys) {
//            String[] parts = key.split(":");
//            Integer questionNumber = Integer.valueOf(parts[3]);

            log.debug("key : " + key);
            String json = redisTemplate.opsForList().getLast(key);
            log.debug("json : " + json);
            GetQuestion getQuestion = objectMapper.readValue(json, GetQuestion.class);

            Question question = questionRepository.findById(getQuestion.getQuestionId())
                    .orElseThrow(() -> new NoSuchElementException("해당 문제 없음: " + getQuestion.getQuestionId()));

            String correctAnswer = "";

            if (question.isQuestionType()) {

                Map<Integer, String> options = getQuestion.getOptions();
                Options correctOption = optionRepository.findByQuestion_QuestionIdAndCorrect(getQuestion.getQuestionId(), true);

                for (Integer optionId : options.keySet()) {

                    if (String.valueOf(options.get(optionId)).equals(correctOption.getOptionContent())) {
                        correctAnswer = String.valueOf(optionId);
                        log.debug("객관식 - correctAnswer : " + correctAnswer);
                        break;
                    }
                }
            }
            else {
                correctAnswer = question.getAnswerText();
                log.debug("주관식 - correctAnswer : " + correctAnswer);
            }

            String userAnswer = getQuestion.getUserAnswer();
            Boolean isCorrect = correctAnswer.equals(userAnswer);

            question.setTriedNum(question.getTriedNum() + 1);
            if(isCorrect) {
                question.setCorrectedNum(question.getCorrectedNum() + 1);
                totalScore ++;
            }
            question.setCorrectRate(((double) question.getCorrectedNum() / question.getTriedNum()) * 100);

            questionRepository.save(question);

            getQuestion.setCorrectAnswer(correctAnswer);
            getQuestion.setIsCorrect(isCorrect);
            getQuestion.setCorrectRate(question.getCorrectRate());

            log.debug("ranking 하기 전");
            rankService.saveRanking(userId, saveQuestion.getLectureId(), totalScore);

            String updatedJson = objectMapper.writeValueAsString(getQuestion);
            redisTemplate.opsForList().rightPush(key, updatedJson);
        }
    }

    @Transactional
    public GetQuestion getQuestionResult(Integer questionNumber, Long userId, LectureIdRequestDto lectureIdRequestDto) throws JsonProcessingException {

        log.debug("getQuestionResult service in");

//        Question question = questionRepository.findById(questionId)
//                .orElseThrow(() -> new RuntimeException("해당 문제를 찾을 수 없습니다."));

        String key = "quiz:" + userId + ":" + lectureIdRequestDto.getLectureId() + ":" + questionNumber;
        String resultJson = redisTemplate.opsForList().getLast(key);

        return objectMapper.readValue(resultJson, GetQuestion.class);
    }
}
