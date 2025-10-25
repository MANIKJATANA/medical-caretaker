package com.jatana.medicalcaretaker.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.HashMap;

public record UpdateScheduleRequest(
        @NotBlank String userId,
        @NotBlank String medicineId,
        @NotNull HashMap<@NotNull DayOfWeek, @NotNull HashMap<@NotNull LocalTime, @NotBlank String>> schedule
) {
}
