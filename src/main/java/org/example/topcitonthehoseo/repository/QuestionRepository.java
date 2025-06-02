package org.example.topcitonthehoseo.repository;

import org.example.topcitonthehoseo.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findByTopic_TopicId(Integer topicId);
}
