package com.talent.matcher.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talent.matcher.dto.ResumeResponse;
import com.talent.matcher.model.Resume;
import com.talent.matcher.repository.ResumeRepository;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ResumeService {
    private final ResumeRepository resumeRepository;
    private final EmbeddingService embeddingService;
    private final ObjectMapper mapper = new ObjectMapper();
    private final Tika tika = new Tika();
    private final Path storagePath;

    public ResumeService(ResumeRepository resumeRepository,
                         EmbeddingService embeddingService,
                         @Value("${storage.resume-path:./storage/resumes}") String storagePathValue) throws IOException {
        this.resumeRepository = resumeRepository;
        this.embeddingService = embeddingService;
        this.storagePath = Path.of(storagePathValue);
        Files.createDirectories(storagePath);
    }

    public ResumeResponse upload(MultipartFile file, String candidateName) throws IOException {
        String safeName = Path.of(Optional.ofNullable(file.getOriginalFilename()).orElse("resume")).getFileName().toString();
        Path destination = storagePath.resolve(safeName);
        file.transferTo(destination);

        String text;
        try {
            text = tika.parseToString(destination.toFile());
        } catch (Exception e) {
            text = new String(file.getBytes());
        }
        List<Double> embedding = embeddingService.embedText(text);
        Resume resume = Resume.builder()
                .candidateName(Optional.ofNullable(candidateName).filter(s -> !s.isBlank()).orElseGet(() -> deriveName(file.getOriginalFilename())))
                .fileName(safeName)
                .uploadedAt(Instant.now())
                .fullText(text)
                .summary(text.substring(0, Math.min(500, text.length())))
                .active(true)
                .skills(extractSkills(text))
                .embeddingJson(mapper.writeValueAsString(embedding))
                .build();
        resumeRepository.save(resume);
        return toResponse(resume);
    }

    public List<ResumeResponse> list() {
        return resumeRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public void deactivate(Long id) {
        resumeRepository.findById(id).ifPresent(resume -> {
            resume.setActive(false);
            resumeRepository.save(resume);
        });
    }

    public Optional<Resume> get(Long id) {
        return resumeRepository.findById(id);
    }

    private ResumeResponse toResponse(Resume resume) {
        return new ResumeResponse(resume.getId(), resume.getCandidateName(), resume.getFileName(), resume.getUploadedAt(), resume.isActive(), resume.getYearsOfExperience(), resume.getSkills(), resume.getSummary());
    }

    private String deriveName(String fileName) {
        if (fileName == null) {
            return "Candidate";
        }
        return fileName.replace(".pdf", "").replace(".docx", "").replace(".txt", "");
    }

    private List<String> extractSkills(String text) {
        String lower = text.toLowerCase();
        List<String> known = List.of("java", "spring", "python", "react", "docker", "aws", "sql", "kubernetes");
        return known.stream().filter(lower::contains).collect(Collectors.toList());
    }

    public List<Double> readEmbedding(Resume resume) {
        try {
            if (resume.getEmbeddingJson() == null) return List.of();
            return Arrays.asList(mapper.readValue(resume.getEmbeddingJson(), Double[].class));
        } catch (Exception e) {
            return List.of();
        }
    }
}
