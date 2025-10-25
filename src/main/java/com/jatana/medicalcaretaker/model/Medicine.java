package com.jatana.medicalcaretaker.model;

import com.jatana.medicalcaretaker.model.medicineEnums.MedicineState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "medicines")
public class Medicine {
    @Id
    private String id;
    private String userId;
    private String name;
    private String description;
    @Field("image")
    private String imageUrl;
    private MedicineState state;

    // Auditing and concurrency control
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

}

