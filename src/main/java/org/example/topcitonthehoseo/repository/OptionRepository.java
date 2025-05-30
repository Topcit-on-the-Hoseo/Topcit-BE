package org.example.topcitonthehoseo.repository;

import org.example.topcitonthehoseo.entity.Options;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OptionRepository extends JpaRepository<Options, Long> {

    List<Options> findByQuestion_QuestionId(Long questionId);

    Options findByQuestion_QuestionIdAndCorrect(Long questionId, boolean correct);
}
