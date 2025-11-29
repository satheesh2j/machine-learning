package com.talent.matcher.service;

import com.talent.matcher.dto.MatchRequest;
import com.talent.matcher.dto.MatchResult;
import com.talent.matcher.model.Resume;
import com.talent.matcher.repository.ResumeRepository;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MatchingService {
    private final ResumeRepository resumeRepository;
    private final EmbeddingService embeddingService;
    private final ResumeService resumeService;
    private final ExplanationService explanationService;

    public MatchingService(ResumeRepository resumeRepository, EmbeddingService embeddingService, ResumeService resumeService, ExplanationService explanationService) {
        this.resumeRepository = resumeRepository;
        this.embeddingService = embeddingService;
        this.resumeService = resumeService;
        this.explanationService = explanationService;
    }

    public List<MatchResult> match(MatchRequest request) {
        List<Double> jdEmbedding = embeddingService.embedText(request.description());
        return resumeRepository.findAll().stream()
                .filter(Resume::isActive)
                .map(resume -> toResult(resume, jdEmbedding, request))
                .sorted(Comparator.comparingDouble(MatchResult::score).reversed())
                .limit(50)
                .collect(Collectors.toList());
    }

    private MatchResult toResult(Resume resume, List<Double> jdEmbedding, MatchRequest request) {
        List<Double> resumeEmbedding = resumeService.readEmbedding(resume);
        double score = cosineSimilarity(jdEmbedding, resumeEmbedding);
        List<String> matchedSkills = resume.getSkills();
        String explanation = explanationService.explainFit(resume.getFullText(), request.description(), resume.getCandidateName());
        String upload = resume.getUploadedAt() != null ? DateTimeFormatter.ISO_INSTANT.format(resume.getUploadedAt()) : "";
        return new MatchResult(resume.getId(), resume.getCandidateName(), Math.round(score * 1000d) / 10d, matchedSkills, explanation, resume.getYearsOfExperience(), upload);
    }

    private double cosineSimilarity(List<Double> a, List<Double> b) {
        if (a.isEmpty() || b.isEmpty() || a.size() != b.size()) return 0;
        double dot = 0; double normA = 0; double normB = 0;
        for (int i = 0; i < a.size(); i++) {
            double x = a.get(i);
            double y = b.get(i);
            dot += x * y;
            normA += x * x;
            normB += y * y;
        }
        if (normA == 0 || normB == 0) return 0;
        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}
