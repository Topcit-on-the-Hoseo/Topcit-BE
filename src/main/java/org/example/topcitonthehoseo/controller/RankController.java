package org.example.topcitonthehoseo.controller;

import java.util.List;

import org.example.topcitonthehoseo.dto.response.GetRankListResponseDto;
import org.example.topcitonthehoseo.service.RankService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/rank")
@RequiredArgsConstructor
public class RankController {

    private final RankService rankService;

    @PostMapping("/list/{seasonId}")
    public List<GetRankListResponseDto> getRankList(@PathVariable int seasonId) {
        return rankService.getRankList(seasonId);
    }

}