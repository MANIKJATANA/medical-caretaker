package com.jatana.medicalcaretaker.model.dto.dayWiseSchedule;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public record DateSchedule(
        @NotNull LocalDate date,
        @NotNull String userId,
        @NotNull List<@Valid Dose> dose
) {}
