package org.example.topcitonthehoseo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.topcitonthehoseo.dto.response.GetRankListResponseDto;
import org.example.topcitonthehoseo.entity.*;
import org.example.topcitonthehoseo.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RankService {

    private final UserRepository userRepository;

    private final UserRankRepository userRankRepository;

    private final ScoreRepository scoreRepository;

    private final SeasonRepository seasonRepository;
    private final LectureRepository lectureRepository;
    private final RankRepository rankRepository;

    @Transactional
    public List<GetRankListResponseDto> getRankList(int seasonId, Long userId) {

        log.debug("get rank list service in");

        List<UserRank> userRanks = userRankRepository.findBySeason_SeasonId(seasonId);
        List<GetRankListResponseDto> rankList = new ArrayList<>();

        for (UserRank userRank : userRanks) {
            List<Score> scoreList = scoreRepository.findByUser_UserIdAndSeason_SeasonId(userId, seasonId);

            Map<Integer, Integer> scoreMap = scoreList.stream()
                    .collect(Collectors.toMap(
                            s -> s.getLecture().getLectureId(),
                            Score::getScore
                    ));

            int totalScore = scoreMap.values().stream()
                    .mapToInt(Integer::intValue)
                    .sum();

            GetRankListResponseDto getRankListResponseDto = GetRankListResponseDto.builder()
                    .nickname(userRank.getUser().getNickname())
                    .rankImage(userRank.getRanking().getRankImage())
                    .lecture1Score(scoreMap.getOrDefault(1, 0))
                    .lecture2Score(scoreMap.getOrDefault(2, 0))
                    .lecture3Score(scoreMap.getOrDefault(3, 0))
                    .lecture4Score(scoreMap.getOrDefault(4, 0))
                    .lecture5Score(scoreMap.getOrDefault(5, 0))
                    .lecture6Score(scoreMap.getOrDefault(6, 0))
                    .totalScore(totalScore)
                    .build();

            rankList.add(getRankListResponseDto);
        }

        return rankList;
    }

    @Transactional
    public void saveRanking(Long userId, Integer lectureId, int score) {

        Date now = new Date();
        Season season = seasonRepository.findCurrentSeason(now)
                .orElseThrow(() -> new IllegalStateException("현재 진행 중인 시즌이 없습니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 유저가 없습니다."));

        Lecture lecture = lectureRepository.findById(Long.valueOf(lectureId))
                .orElseThrow(() -> new NoSuchElementException("해당 영역이 없습니다."));

        Score scoreData = Score.builder()
                .user(user)
                .season(season)
                .lecture(lecture)
                .score(score)
                .build();

        scoreRepository.save(scoreData);

        List<Score> scoreList = scoreRepository.findByUser_UserIdAndSeason_SeasonId(userId, season.getSeasonId());

        Map<Integer, Integer> scoreMap = scoreList.stream()
                .collect(Collectors.toMap(
                        s -> s.getLecture().getLectureId(),
                        Score::getScore
                ));

        boolean hasZero = scoreMap.values().stream().anyMatch(lectureScore -> lectureScore == 0);

        String rankName;

        int totalScore = scoreMap.values().stream()
                .mapToInt(Integer::intValue)
                .sum();

        if (hasZero) {
            rankName = "UNRANK";
        } else if (totalScore >= 1 && totalScore <= 30) {
            rankName = "BRONZE";
        } else if (totalScore >= 31 && totalScore <= 60) {
            rankName = "SILVER";
        } else if (totalScore >= 61 && totalScore <= 80) {
            rankName = "GOLD";
        } else if (totalScore >= 81 && totalScore <= 100) {
            rankName = "PLATINUM";
        } else if (totalScore >= 100 && totalScore <= 120) {
            rankName = "DIAMOND";
        } else {
            throw new RuntimeException("잘못된 점수 값입니다.");
        }

        Ranking ranking = rankRepository.findByRankName(rankName)
                .orElseThrow(() -> new NoSuchElementException("해당 이름을 가진 랭크가 없습니다."));

        UserRank userRank = UserRank.builder()
                .user(user)
                .season(season)
                .ranking(ranking)
                .build();

        userRankRepository.save(userRank);
    }
}
