package com.jatana.medicalcaretaker.model.dto.medicineHistory;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record MedicineHistoryResponse(
        @NotBlank String medicineId,
        @NotBlank String userId,
        @NotBlank @Size(max = 100) String medicineName,
        @Size(max = 1000) String medicineDescription,
        @Size(max = 2048) String medicineImage,
        @NotNull List<@Valid MedicineDateSchedule> medicineDateSchedules
) {}
