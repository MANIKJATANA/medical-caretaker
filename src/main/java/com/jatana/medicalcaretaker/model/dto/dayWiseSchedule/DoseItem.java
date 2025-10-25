package com.jatana.medicalcaretaker.model.dto.dayWiseSchedule;

import com.jatana.medicalcaretaker.model.medicineEnums.ScheduleStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DoseItem(
        @NotBlank String medicineId,
        @NotBlank @Size(max = 100) String medicineName,
        @Size(max = 1000) String medicineDescription,
        ScheduleStatus status,
        @Size(max = 2048) String medicineImage
) {}
