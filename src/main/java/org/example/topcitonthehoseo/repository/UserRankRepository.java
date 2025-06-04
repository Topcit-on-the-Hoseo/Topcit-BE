package org.example.topcitonthehoseo.repository;

import org.example.topcitonthehoseo.entity.UserRank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRankRepository extends JpaRepository<UserRank, Long> {

    List<UserRank> findBySeason_SeasonId(int seasonId);
}
