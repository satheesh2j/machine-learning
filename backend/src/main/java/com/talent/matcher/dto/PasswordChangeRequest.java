package com.talent.matcher.dto;

import jakarta.validation.constraints.NotBlank;

public record PasswordChangeRequest(@NotBlank String newPassword) {
}
