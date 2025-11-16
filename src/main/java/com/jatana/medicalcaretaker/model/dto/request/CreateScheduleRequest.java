package com.jatana.medicalcaretaker.model.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;

public record CreateScheduleRequest(
        // User data
        @NotBlank String userId,
        
        // Multiple medicines data (input-only type below)
        @NotEmpty List<@Valid MedicineInput> medicines
) {

    public record MedicineInput(
            @NotBlank String medicineId,
            // per-medicine schedule embedded here to avoid cross-map id mismatch
            @NotNull HashMap<@NotNull DayOfWeek, @NotNull HashMap<@NotNull LocalTime, @NotBlank String>> schedule
    ) {}
}
