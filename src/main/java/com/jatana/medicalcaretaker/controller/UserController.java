package com.jatana.medicalcaretaker.controller;

import com.jatana.medicalcaretaker.model.User;
import com.jatana.medicalcaretaker.model.dto.user.CreateUserResponse;
import com.jatana.medicalcaretaker.model.dto.user.CreateUserRequest;
import com.jatana.medicalcaretaker.model.dto.user.AddCaretakerRequest;
import com.jatana.medicalcaretaker.service.MyUserDetailsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
public class UserController {

    private static final String ERROR_KEY = "error";

    private final MyUserDetailsService service;

    @Autowired
    public UserController(MyUserDetailsService service) {
        this.service = service;
    }

    @PostMapping("/create")
    public ResponseEntity<Object> createUser(@RequestBody @Valid CreateUserRequest request) {
        try {
            User user = new User();
            user.setName(request.name());
            user.setEmail(request.email());
            user.setPassword(request.password());
            user.setRole(request.role());
            user.setMobileNumber(request.mobileNumber());
            user.setCareReceiverUserIds(List.of());
            user.setCareTakerUserIds(List.of());

            User createdUser = service.saveUser(user);
            CreateUserResponse response = new CreateUserResponse(
                    createdUser.getId(),
                    createdUser.getName(),
                    createdUser.getEmail(),
                    createdUser.getRole(),
                    createdUser.getCareTakerUserIds(),
                    createdUser.getCareReceiverUserIds(),
                    createdUser.getMobileNumber()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put(ERROR_KEY, e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put(ERROR_KEY, "An error occurred while creating the user");
            errorResponse.put("details", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/addCaretaker")
    @PreAuthorize("hasAnyRole('CARERECIEVER', 'ADMIN')")
    public ResponseEntity<Object> addCaretaker(@RequestBody @Valid AddCaretakerRequest request) {
        try {
            String userId = service.getAuthenticatedUserId();
            User user = service.addCaretaker(userId, request.caretakerEmail());
            CreateUserResponse response = new CreateUserResponse(
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getRole(),
                    user.getCareTakerUserIds(),
                    user.getCareReceiverUserIds(),
                    user.getMobileNumber()
            );
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }catch (IllegalArgumentException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put(ERROR_KEY, e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("details", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
