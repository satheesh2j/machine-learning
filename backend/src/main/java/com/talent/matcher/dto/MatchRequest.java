package com.talent.matcher.dto;

import java.util.List;

public record MatchRequest(String title, String description, List<String> requiredSkills) {}
