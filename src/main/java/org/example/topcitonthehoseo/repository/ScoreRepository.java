package org.example.topcitonthehoseo.repository;

import org.example.topcitonthehoseo.entity.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScoreRepository extends JpaRepository<Score, Long> {

    List<Score> findByUser_UserIdAndSeason_SeasonId(Long userId, int SeasonId);
}
