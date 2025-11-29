package com.talent.matcher.controller;

import com.talent.matcher.dto.MatchRequest;
import com.talent.matcher.dto.MatchResult;
import com.talent.matcher.service.MatchingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/match")
public class MatchingController {
    private final MatchingService matchingService;

    public MatchingController(MatchingService matchingService) {
        this.matchingService = matchingService;
    }

    @PostMapping
    public ResponseEntity<List<MatchResult>> match(@RequestBody MatchRequest request) {
        return ResponseEntity.ok(matchingService.match(request));
    }
}
