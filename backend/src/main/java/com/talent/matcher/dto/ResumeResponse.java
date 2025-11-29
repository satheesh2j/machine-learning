package com.talent.matcher.dto;

import java.time.Instant;
import java.util.List;

public record ResumeResponse(Long id,
                             String candidateName,
                             String fileName,
                             Instant uploadedAt,
                             boolean active,
                             Double yearsOfExperience,
                             List<String> skills,
                             String summary) {}
