package org.example.topcitonthehoseo.repository;

import org.example.topcitonthehoseo.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TopicRepository extends JpaRepository<Topic, Long> {

    List<Topic> findByLecture_LectureId(Integer lectureId);

    Optional<Topic> findByTopicName(String topicName);
}
