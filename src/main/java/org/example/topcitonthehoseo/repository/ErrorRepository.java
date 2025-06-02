package org.example.topcitonthehoseo.repository;

import org.example.topcitonthehoseo.entity.QuestionError;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ErrorRepository extends CrudRepository<QuestionError, Long> {
}
