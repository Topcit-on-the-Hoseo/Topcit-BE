package org.example.topcitonthehoseo.repository;

import org.example.topcitonthehoseo.entity.Ranking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RankRepository extends JpaRepository<Ranking, Long> {

    Optional<Ranking> findByRankName(String rankName);
}
