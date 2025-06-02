package org.example.topcitonthehoseo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.topcitonthehoseo.config.MarkdownParser;
import org.example.topcitonthehoseo.entity.Lecture;
import org.example.topcitonthehoseo.entity.Options;
import org.example.topcitonthehoseo.entity.Question;
import org.example.topcitonthehoseo.entity.Topic;
import org.example.topcitonthehoseo.repository.LectureRepository;
import org.example.topcitonthehoseo.repository.OptionRepository;
import org.example.topcitonthehoseo.repository.QuestionRepository;
import org.example.topcitonthehoseo.repository.TopicRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrepareService {

    private final LectureRepository lectureRepository;

    private final TopicRepository topicRepository;

    private final QuestionRepository questionRepository;

    private final OptionRepository optionRepository;

    private final MarkdownParser markdownParser;

    public void prepareFromDirectory(Path baseDir) {

        try (Stream<Path> chapterDirs = Files.list(baseDir)) {
            chapterDirs.filter(Files::isDirectory).forEach(chapter -> {

                int lectureNum = Integer.parseInt(chapter.getFileName().toString().replace("chapter.", ""));
                Lecture lecture = saveOrGetLecture(lectureNum);
                log.debug("1-1. lecture num : {}", lectureNum);
                log.debug("1-2. lecture id : {}", lecture.getLectureId());

                try (Stream<Path> topicDirs = Files.list(chapter)) {
                    topicDirs.filter(Files::isDirectory).forEach(topicDir -> {

                        int topicNum = Integer.parseInt(topicDir.getFileName().toString());
                        int topicOffset = ((topicNum - 1) / 5) + 1;  // 0 나눗셈도 정상 작동할까?
                        log.debug("2-1. topic num : {}", topicNum);
                        log.debug("2-2. topic offset : {}", topicOffset);

                        String topicRealNum = lectureNum +  "-" + topicOffset;
                        log.debug("2-3. topic realNum : {}", topicRealNum);

                        Topic topic = saveOrGetTopic(lecture, topicRealNum);
                        log.debug("2-4. topic id : {}", topic.getTopicId());

                        List<String> types = List.of("객관식", "단답형");
                        for (String type : types) {
                            Path typePath = topicDir.resolve(type);
                            if (!Files.exists(typePath)) continue;

                            boolean isObjective = type.equals("객관식");  // 객관식이면 true, 주관식이면 false
                            log.debug("3-1. topic type : {}", isObjective);

                            try (Stream<Path> mdFiles = Files.list(typePath)) {
                                mdFiles.filter(p -> p.toString().endsWith(".md"))
                                        .forEach(file -> {
                                            MarkdownParser.QuestionData data = markdownParser.parse(file, isObjective);
                                            saveQuestion(data, lecture, topic);
                                });
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Lecture saveOrGetLecture(int lectureNum) {
        return lectureRepository.findById((long) lectureNum)
                .orElseGet(() -> lectureRepository.save(
                        Lecture.builder()
                                .lectureId(lectureNum)
                                .lectureName("임시 Lecture " + lectureNum)
                                .build()));
    }

    private Topic saveOrGetTopic(Lecture lecture, String topicNum) {
        return topicRepository.findByTopicName(topicNum)
                .orElseGet(() -> topicRepository.save(
                        Topic.builder()
                                .topicName(topicNum)
                                .lecture(lecture)
                                .build()));
    }

    private void saveQuestion(MarkdownParser.QuestionData data, Lecture lecture, Topic topic) {
        Question question = Question.builder()
                .questionContent(data.question())
                .lecture(lecture)
                .topic(topic)
                .questionType(data.isObjective())
                .answerText(data.isObjective() ? null : data.answerText())
                .build();

        question = questionRepository.save(question);
        log.debug("4-1. question info : {}, {}, {}, {}", question.getQuestionId(), question.getLecture().getLectureId(), question.getTopic().getTopicId(), question.getAnswerText());

        if (data.isObjective()) {
            for (MarkdownParser.OptionData option : data.options()) {
                optionRepository.save(Options.builder()
                        .question(question)
                        .optionContent(option.text())
                        .correct(option.correct())
                        .build());
            }
        }
    }
}
