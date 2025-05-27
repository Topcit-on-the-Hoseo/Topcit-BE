package org.example.topcitonthehoseo.repository;

import org.example.topcitonthehoseo.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopicRepository extends JpaRepository<Topic, Long> {
}
