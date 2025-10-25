package com.jatana.medicalcaretaker.model.dto.dayWiseSchedule;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;
import java.util.List;

public record Dose(@NotNull LocalTime time, List<@Valid DoseItem> medicines) {}
