package com.talent.matcher.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "resumes")
public class Resume {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String candidateName;
    private String fileName;
    private Instant uploadedAt;

    @Lob
    @Column(length = 4000)
    private String summary;

    @Lob
    private String fullText;

    private boolean active;

    private Double yearsOfExperience;

    @ElementCollection
    private List<String> skills;

    @Lob
    private String embeddingJson;
}
