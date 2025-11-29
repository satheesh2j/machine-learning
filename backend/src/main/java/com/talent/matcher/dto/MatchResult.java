package com.talent.matcher.dto;

import java.util.List;

public record MatchResult(Long resumeId,
                          String candidateName,
                          double score,
                          List<String> matchedSkills,
                          String explanation,
                          Double yearsOfExperience,
                          String uploadDate) {}
