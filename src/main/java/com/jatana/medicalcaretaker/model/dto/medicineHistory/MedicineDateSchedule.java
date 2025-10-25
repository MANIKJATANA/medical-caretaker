package com.jatana.medicalcaretaker.model.dto.medicineHistory;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record MedicineDateSchedule(
        @NotNull LocalDateTime actionTime,
        @NotNull String action
) {}
