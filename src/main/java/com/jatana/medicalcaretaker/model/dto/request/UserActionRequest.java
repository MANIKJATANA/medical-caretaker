package com.jatana.medicalcaretaker.model.dto.request;

import com.jatana.medicalcaretaker.model.medicineEnums.ScheduleStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * Captures a user's action in response to an alarm/alert for a dose.
 * actionDateTime = scheduled/expected time; actualDateTime = when user actually acted.
 * Both should be ISO-8601 strings (e.g., 2025-09-28T08:00:00+05:30).
 */
public record UserActionRequest(
        @NotBlank String userId,
        @NotBlank String medicineId,


        @NotNull ScheduleStatus action,    // TAKEN, SKIPPED, MISSED, SCHEDULED

        @NotNull LocalDateTime actionDateTime,   // scheduled/expected time (ISO-8601)
        @NotNull LocalDateTime actualDateTime,   // actual performed time (ISO-8601)

        @Size(max = 500) String note       // optional free-text
) {}


