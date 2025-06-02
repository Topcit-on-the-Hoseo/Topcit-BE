package org.example.topcitonthehoseo.repository;

import org.example.topcitonthehoseo.entity.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LectureRepository extends JpaRepository<Lecture, Long> {
}
