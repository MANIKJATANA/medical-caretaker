package com.jatana.medicalcaretaker.model.dto.request;

import com.jatana.medicalcaretaker.model.medicineEnums.MedicineState;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MedicineData(
        @NotBlank @Size(max = 100) String medicineName,
        @Size(max = 1000) String medicineDescription,
        @Size(max = 2048) String medicineImage,
        @NotNull MedicineState medicineState
) {}
