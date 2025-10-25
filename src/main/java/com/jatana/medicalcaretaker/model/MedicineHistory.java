package com.jatana.medicalcaretaker.model;

import com.jatana.medicalcaretaker.model.medicineEnums.ScheduleStatus;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "medicine_history")
public class MedicineHistory {
    @Id
    private String id;
    private String userId;
    private String medicineId;

    private ScheduleStatus action;

    // When the action was scheduled/recorded (UTC)
    private LocalDateTime actionAt;
    // When the action actually happened (UTC), optional
    private LocalDateTime actualAt;

    private String actorUserId;

    public MedicineHistory(String userId, String medicineId, ScheduleStatus action, LocalDateTime actionAt, LocalDateTime actualAt, String actorUserId) {
        this.userId = userId;
        this.medicineId = medicineId;
        this.action = action;
        this.actionAt = actionAt;
        this.actualAt = actualAt;
        this.actorUserId = actorUserId;
    }
}
