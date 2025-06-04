package org.example.topcitonthehoseo.repository;

import org.example.topcitonthehoseo.entity.Season;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface SeasonRepository extends JpaRepository<Season, Long> {

    @Query("SELECT s FROM Season s WHERE :now BETWEEN s.seasonStart AND s.seasonEnd")
    Optional<Season> findCurrentSeason(@Param("now") LocalDateTime now);
}
