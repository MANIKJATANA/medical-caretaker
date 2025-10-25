package com.jatana.medicalcaretaker.model.dto.user;

import com.jatana.medicalcaretaker.model.userEnums.Role;

import java.util.List;

public record CreateUserResponse(
        String id,
        String name,
        String email,
        Role role,
        List<String> careTakerUserIds,
        List<String> careReceiverUserIds,
        String mobileNumber
) {}


