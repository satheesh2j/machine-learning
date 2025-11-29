package com.talent.matcher.controller;

import com.talent.matcher.dto.ResumeResponse;
import com.talent.matcher.service.ResumeService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/resumes")
public class ResumeController {
    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResumeResponse> upload(@RequestPart("file") MultipartFile file,
                                                 @RequestPart(value = "candidateName", required = false) String candidateName) throws IOException {
        return ResponseEntity.ok(resumeService.upload(file, candidateName));
    }

    @GetMapping
    public ResponseEntity<List<ResumeResponse>> list() {
        return ResponseEntity.ok(resumeService.list());
    }

    @PostMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        resumeService.deactivate(id);
        return ResponseEntity.ok().build();
    }
}
