package com.jatana.medicalcaretaker.controller;

import com.jatana.medicalcaretaker.model.dto.medicineHistory.MedicineHistoryResponse;
import com.jatana.medicalcaretaker.model.dto.medicineWiseSchedule.MedicineScheduleResponse;
import com.jatana.medicalcaretaker.model.dto.dayWiseSchedule.DateSchedule;
import com.jatana.medicalcaretaker.model.dto.medicineList.MedicineResponseListItem;
import com.jatana.medicalcaretaker.model.dto.request.CreateScheduleRequest;
import com.jatana.medicalcaretaker.model.dto.request.UpdateScheduleRequest;
import com.jatana.medicalcaretaker.model.dto.request.UpdateMedicineData;
import com.jatana.medicalcaretaker.model.dto.request.UserActionRequest;
import com.jatana.medicalcaretaker.service.MedicineService;
import com.jatana.medicalcaretaker.service.MyUserDetailsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
 

@RestController
@RequestMapping("/api")
public class MedicineController {

    private final MedicineService medicineService;
    private final MyUserDetailsService userDetailsService;

    public MedicineController(MedicineService medicineService, MyUserDetailsService userDetailsService) {
        this.medicineService = medicineService;
        this.userDetailsService = userDetailsService;
    }

    @GetMapping("/medicines")
    @PreAuthorize("hasAnyRole('CARETAKER', 'CARERECIEVER', 'ADMIN')")
    public ResponseEntity<List<MedicineResponseListItem>> getActiveMedicines(@RequestParam String filter, @RequestParam String userId) {
        try {
            String actorUserId = userDetailsService.getAuthenticatedUserId();
            List<MedicineResponseListItem> medicineResponseListItems = medicineService.getMedicines(filter, userId, actorUserId);
            return  new ResponseEntity<>(medicineResponseListItems, HttpStatus.OK);
        } catch (AccessDeniedException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/medicineSchedule")
    @PreAuthorize("hasAnyRole('CARETAKER', 'CARERECIEVER', 'ADMIN')")
    public ResponseEntity<MedicineScheduleResponse> getMedicineSchedule(@RequestParam String medicineId, @RequestParam String userId) {
        try{
            String actorUserId = userDetailsService.getAuthenticatedUserId();
            MedicineScheduleResponse medicineSchedule = medicineService.getMedicineSchedule(medicineId, userId, actorUserId);
            return new ResponseEntity<>(medicineSchedule, HttpStatus.OK);
        } catch (AccessDeniedException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (IllegalArgumentException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/daySchedule")
    @PreAuthorize("hasAnyRole('CARETAKER', 'CARERECIEVER', 'ADMIN')")
    public ResponseEntity<DateSchedule> getDateSchedule(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, @RequestParam String userId) {
        try {
            String actorUserId = userDetailsService.getAuthenticatedUserId();
            DateSchedule dateSchedule = medicineService.getDateSchedule(date, userId, actorUserId);
            return new ResponseEntity<>(dateSchedule, HttpStatus.OK);
        } catch (AccessDeniedException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/medicineHistory")
    @PreAuthorize("hasAnyRole('CARETAKER', 'CARERECIEVER', 'ADMIN')")
    public ResponseEntity<MedicineHistoryResponse> getMedicineHistory(@RequestParam String medicineId, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate, @RequestParam String userId) {
        try {
            String actorUserId = userDetailsService.getAuthenticatedUserId();
            MedicineHistoryResponse medicineHistory = medicineService.getMedicineHistory(medicineId, startDate, endDate, userId, actorUserId);
            return new ResponseEntity<>(medicineHistory, HttpStatus.OK);
        } catch (AccessDeniedException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (IllegalArgumentException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/schedule")
    @PreAuthorize("hasAnyRole('CARETAKER', 'CARERECIEVER', 'ADMIN')")
    public ResponseEntity<String> createMedicineSchedule(@RequestBody @jakarta.validation.Valid CreateScheduleRequest request) {
        try {
            String actorUserId = userDetailsService.getAuthenticatedUserId();
            medicineService.createSchedule(request, actorUserId);
            return new ResponseEntity<>("Schedule created successfully", HttpStatus.CREATED);
        } catch (AccessDeniedException e) {
            return new ResponseEntity<>("Access denied: " + e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/schedule")
    @PreAuthorize("hasAnyRole('CARETAKER', 'CARERECIEVER', 'ADMIN')")
    public ResponseEntity<String> updateMedicineSchedule(@RequestBody @jakarta.validation.Valid UpdateScheduleRequest request) {
        try {
            String actorUserId = userDetailsService.getAuthenticatedUserId();
            medicineService.updateSchedule(request, actorUserId);
            return new ResponseEntity<>("Schedule updated successfully", HttpStatus.OK);
        } catch (AccessDeniedException e) {
            return new ResponseEntity<>("Access denied: " + e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/medicine")
    @PreAuthorize("hasAnyRole('CARETAKER', 'CARERECIEVER', 'ADMIN')")
    public ResponseEntity<String> updateMedicineData(@RequestBody @jakarta.validation.Valid UpdateMedicineData request) {
        try {
            String actorUserId = userDetailsService.getAuthenticatedUserId();
            medicineService.updateMedicineData(request, actorUserId);
            return new ResponseEntity<>("Medicine data updated successfully", HttpStatus.OK);
        } catch (AccessDeniedException e) {
            return new ResponseEntity<>("Access denied: " + e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/userAction")
    @PreAuthorize("hasAnyRole('CARERECIEVER', 'ADMIN')")
    public ResponseEntity<String> recordUserAction(@RequestBody @jakarta.validation.Valid UserActionRequest request) {
        try {
            String actorUserId = userDetailsService.getAuthenticatedUserId();
            medicineService.recordUserAction(request, actorUserId);
            return new ResponseEntity<>("Action recorded", HttpStatus.ACCEPTED);
        } catch (AccessDeniedException e) {
            return new ResponseEntity<>("Access denied: " + e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/schedule")
    @PreAuthorize("hasAnyRole('CARETAKER', 'ADMIN')")
    public ResponseEntity<String> deleteMedicineSchedule(@RequestParam String userId) {
        try {
            String actorUserId = userDetailsService.getAuthenticatedUserId();
            medicineService.removeSchedule(userId, actorUserId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (AccessDeniedException e) {
            return new ResponseEntity<>("Access denied: " + e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (IllegalArgumentException e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        } catch (Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
