package com.jatana.medicalcaretaker.model.dto;

import com.jatana.medicalcaretaker.model.medicineEnums.MedicineState;

public record MedicineResponse(
        String medicineId,
        String userId,
        String medicineName,
        String medicineDescription,
        String imageUrl,
        MedicineState state
) {
}
