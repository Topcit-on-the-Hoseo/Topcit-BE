package org.example.topcitonthehoseo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.topcitonthehoseo.dto.response.GetRankListResponseDto;
import org.example.topcitonthehoseo.service.RankService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/rank")
@RequiredArgsConstructor
public class RankController {

    private final RankService rankService;

    @PostMapping("/list/{seasonId}")
    public List<GetRankListResponseDto> getRankList(@PathVariable int seasonId) {

        log.info("get rank list Controller");

        Long userId = 1L;
        return rankService.getRankList(seasonId, userId);
    }

}
