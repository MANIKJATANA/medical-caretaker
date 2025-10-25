package com.jatana.medicalcaretaker.model.dto.medicineList;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MedicineResponseListItem(
        @NotBlank String medicineId,
        @NotBlank String userId,
        @NotBlank @Size(max = 100) String medicineName,
        @Size(max = 1000) String medicineDescription
) {}

