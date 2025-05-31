package org.example.topcitonthehoseo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class MarkdownParser {
    public record OptionData(String text, boolean correct) {}

    public record QuestionData(String question, boolean isObjective, List<OptionData> options, String answerText) {}

    public QuestionData parse(Path file, boolean isObjective) {
        try {
            List<String> lines = Files.readAllLines(file);
            String question = "";
            List<OptionData> options = new ArrayList<>();
            String answerText = "";

            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i).trim();

                if (line.startsWith("**문제:**")) {
                    String content = line.replace("**문제:**", "").trim();
                    if (!content.isEmpty()) {
                        question = content;
                    } else if (i + 1 < lines.size()) {
                        question = lines.get(i + 1).trim();
                    }
                    log.debug("찍어보기 @@@@@@ Question: " + question);
                }

                if (line.startsWith("**선택지:**")) {
                    for (int j = i + 1; j < lines.size(); j++) {
                        String opt = lines.get(j).trim();
                        if (opt.isEmpty() || opt.startsWith("**")) break;
                        options.add(new OptionData(opt.substring(3).trim(), false));
                    }
                }

                if (line.startsWith("**정답:**")) {
                    String raw = line.replace("**정답:**", "").trim();
                    if (isObjective) {
                        int correctIndex = Integer.parseInt(raw) - 1;
                        if (correctIndex < options.size()) {
                            OptionData correctOption = options.get(correctIndex);
                            options.set(correctIndex, new OptionData(correctOption.text(), true));
                        }
                    } else {
                        answerText = raw;
                    }
                }
            }

            return new QuestionData(question, isObjective, options, answerText);
        } catch (IOException e) {
            throw new RuntimeException("마크다운 파싱 실패: " + file, e);
        }
    }

}
