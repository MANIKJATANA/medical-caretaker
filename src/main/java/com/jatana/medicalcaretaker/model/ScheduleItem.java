package com.jatana.medicalcaretaker.model;

import com.jatana.medicalcaretaker.model.medicineEnums.ScheduleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleItem {
    @Field("dayOfWeek")
    private DayOfWeek dayOfWeek;

    @Field("time")
    private LocalTime time;

    @Field("status")
    private ScheduleStatus status;

}


