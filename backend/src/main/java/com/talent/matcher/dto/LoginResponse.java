package com.talent.matcher.dto;

public record LoginResponse(String token, boolean requirePasswordChange) {}
