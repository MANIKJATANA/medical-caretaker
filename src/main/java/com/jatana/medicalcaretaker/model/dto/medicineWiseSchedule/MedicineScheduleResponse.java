package com.jatana.medicalcaretaker.model.dto.medicineWiseSchedule;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;

public record MedicineScheduleResponse(
        @NotBlank String medicineId,
        @NotBlank String userId,
        @NotBlank @Size(max = 100) String medicineName,
        @Size(max = 1000) String medicineDescription,
        @Size(max = 2048) String medicineImage,
        @NotNull HashMap<DayOfWeek, List<LocalTime>> schedule
) {}
