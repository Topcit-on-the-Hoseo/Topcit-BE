package org.example.topcitonthehoseo.repository;

import java.util.List;
import java.util.Optional;

import org.example.topcitonthehoseo.entity.Lecture;
import org.example.topcitonthehoseo.entity.Score;
import org.example.topcitonthehoseo.entity.Season;
import org.example.topcitonthehoseo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScoreRepository extends JpaRepository<Score, Long> {

    List<Score> findByUser_UserIdAndSeason_SeasonId(Long userId, int SeasonId);

    Optional<Score> findByUserAndSeasonAndLecture(User user, Season season, Lecture lecture);
}
