package com.jatana.medicalcaretaker.model.dto.request;

import com.jatana.medicalcaretaker.model.medicineEnums.MedicineState;


public record AddMedicineRequest(

        String userId,
        String medicineName,
        String medicineDescription,
        String medicineImage,
        MedicineState state

) {
}
