package com.jatana.medicalcaretaker.service;

import com.jatana.medicalcaretaker.model.Medicine;
import com.jatana.medicalcaretaker.model.MedicineHistory;
import com.jatana.medicalcaretaker.model.MedicineSchedule;
import com.jatana.medicalcaretaker.model.ScheduleItem;
import com.jatana.medicalcaretaker.model.dto.medicineHistory.MedicineDateSchedule;
import com.jatana.medicalcaretaker.model.dto.medicineHistory.MedicineHistoryResponse;
import com.jatana.medicalcaretaker.model.dto.medicineWiseSchedule.MedicineScheduleResponse;
import com.jatana.medicalcaretaker.model.dto.request.*;
import com.jatana.medicalcaretaker.model.medicineEnums.MedicineState;
import com.jatana.medicalcaretaker.model.dto.dayWiseSchedule.DateSchedule;
import com.jatana.medicalcaretaker.model.dto.dayWiseSchedule.Dose;
import com.jatana.medicalcaretaker.model.dto.dayWiseSchedule.DoseItem;
import com.jatana.medicalcaretaker.model.dto.medicineList.MedicineResponseListItem;
import com.jatana.medicalcaretaker.model.medicineEnums.ScheduleStatus;
import com.jatana.medicalcaretaker.repo.MedicineServiceRepo;
import com.jatana.medicalcaretaker.repo.MedicineScheduleRepo;
import com.jatana.medicalcaretaker.repo.MedicineHistoryRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class MedicineService {

    private final MedicineServiceRepo medicineRepo;
    private final MedicineScheduleRepo medicineScheduleRepo;
    private final MedicineHistoryRepo medicineHistoryRepo;
    private final MyUserDetailsService userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(MedicineService.class);

    public MedicineService(MedicineServiceRepo medicineRepo, 
                          MedicineScheduleRepo medicineScheduleRepo,
                          MedicineHistoryRepo medicineHistoryRepo,
                          MyUserDetailsService userDetailsService) {
        this.medicineRepo = medicineRepo;
        this.medicineScheduleRepo = medicineScheduleRepo;
        this.medicineHistoryRepo = medicineHistoryRepo;
        this.userDetailsService = userDetailsService;
    }

    public List<MedicineResponseListItem> getMedicines(String stateFilter, String targetUserId, String actorUserId) {
        // Authorize access
        userDetailsService.authorizeAccess(actorUserId, targetUserId);
        logger.debug("Getting medicines for state filter: {}, target user id: {}", stateFilter, targetUserId);

        List<Medicine> medicines;
        if (stateFilter == null || stateFilter.equalsIgnoreCase("all")) {
            logger.debug("Getting all medicines");
            medicines = medicineRepo.findByUserId(targetUserId);
        } else if (stateFilter.equalsIgnoreCase(MedicineState.ACTIVE.name())) {
            logger.debug("Getting active medicines");
            medicines = medicineRepo.findByUserIdAndState(targetUserId, MedicineState.ACTIVE);
        } else if (stateFilter.equalsIgnoreCase(MedicineState.INACTIVE.name())) {
            logger.debug("Getting inactive medicines");
            medicines = medicineRepo.findByUserIdAndState(targetUserId, MedicineState.INACTIVE);
        } else if (stateFilter.equalsIgnoreCase(MedicineState.REMOVED.name())) {
            logger.debug("Getting removed medicines");
            medicines = medicineRepo.findByUserIdAndState(targetUserId, MedicineState.REMOVED);
        } else {
            logger.warn("Unknown medicine state: " + stateFilter);
            throw new IllegalArgumentException("Invalid state filter: " + stateFilter);
        }

        logger.debug("Returning medicines from list");

        return medicines.stream()
                .map(m -> new MedicineResponseListItem(
                        m.getId(),
                        m.getUserId(),
                        m.getName(),
                        m.getDescription()
                ))
                .toList();
    }

    public MedicineScheduleResponse getMedicineSchedule(String medicineId, String userId, String actorUserId) {
        // Authorize access
        userDetailsService.authorizeAccess(actorUserId, userId);

        logger.info("Getting medicine schedule for medicineId: {} and userId: {}", medicineId, userId);
        

        Optional<MedicineSchedule> medicineSchedule = medicineScheduleRepo.findByMedicineIdAndUserId(medicineId, userId);
        if(medicineSchedule.isEmpty()){
            logger.error("Medicine schedule not found for medicineId: {} and userId: {}", medicineId, userId);
            throw new IllegalArgumentException("Medicine schedule not found for medicineId: " + medicineId + " and userId: " + userId);
        }
        logger.info("Medicine schedule found for medicineId: {} and userId: {}", medicineId, userId);

       Optional<Medicine> medicineDetail = medicineRepo.findByIdAndUserId(medicineId, userId);
       if(medicineDetail.isEmpty()){
           throw new IllegalArgumentException("Medicine detail not found for medicineId: " + medicineId + " and userId: " + userId);
       }
        logger.info("Medicine detail found for medicineId: {} and userId: {}", medicineId, userId);

        MedicineSchedule ms = medicineSchedule.get();

        logger.info("Getting day of week list hash map");
        Medicine medicine = medicineDetail.get();
        String mId = medicine.getId();
        String uId = medicine.getUserId();
        String mName = medicine.getName();
        String mDescription = medicine.getDescription();
        String mImage = medicine.getImageUrl();
        HashMap<DayOfWeek, List<LocalTime>> schedule = getDayOfWeekListHashMap(ms);

        logger.info("Returning medicine schedule for medicineId: {} and userId: {}", medicineId, userId);
        return new MedicineScheduleResponse(mId, uId, mName, mDescription, mImage, schedule);
        
    }

    private static HashMap<DayOfWeek, List<LocalTime>> getDayOfWeekListHashMap(MedicineSchedule ms) {
        List<ScheduleItem> scheduleItemList = ms.getSchedule();
        HashMap<DayOfWeek, List<LocalTime>> schedule = new HashMap<>();

        for(ScheduleItem si : scheduleItemList){
         DayOfWeek dayOfWeek = si.getDayOfWeek();
         LocalTime time = si.getTime();
         List<LocalTime> timeList;

         if(schedule.containsKey(dayOfWeek)){
             timeList = schedule.get(dayOfWeek);
         }else {
             timeList = new ArrayList<>();
         }
         timeList.add(time);
         timeList.sort(Comparator.comparing(LocalTime::toSecondOfDay));
         schedule.put(dayOfWeek, timeList);

        }
        return schedule;
    }

    public DateSchedule getDateSchedule(LocalDate date, String userId, String actorUserId) {
        // Authorize access
        userDetailsService.authorizeAccess(actorUserId, userId);
        
        // check if it is the past date or not
        DayOfWeek dayOfWeek = date.getDayOfWeek(); 
        LocalDate currentDate = LocalDate.now();


        if(date.isAfter(currentDate)){
            List<MedicineSchedule> medicineSchedule = medicineScheduleRepo.findByUserIdAndScheduleDayOfWeek(userId, dayOfWeek);
            if(medicineSchedule.isEmpty()){
                throw new IllegalArgumentException("Medicine schedule not found for userId: " + userId + " and date: " + date);
            }
            
            List<Dose> doseList = new ArrayList<>();
            HashMap<LocalTime, List<DoseItem>> doseItemMap = new HashMap<>();

            for(MedicineSchedule ms : medicineSchedule){

                Optional<Medicine> medicine = medicineRepo.findByIdAndUserId(ms.getMedicineId(), userId);
                if(medicine.isEmpty()){
                    continue;
                }

                Medicine m = medicine.get();
                String mId = m.getId();
                String mName = m.getName();
                String mDescription = m.getDescription();
                String mImage = m.getImageUrl();



                List<ScheduleItem> scheduleItemList = ms.getSchedule();
                for(ScheduleItem si : scheduleItemList){
                    // Filter by day of week
                    if(si.getDayOfWeek() != dayOfWeek){
                        continue;
                    }
                    LocalTime time = si.getTime();
                    DoseItem doseItem = new DoseItem(mId, mName, mDescription, ScheduleStatus.SCHEDULED, mImage);
                    List<DoseItem> doseItemList = new ArrayList<>();
                    if(doseItemMap.containsKey(time)){
                        doseItemList = doseItemMap.get(time);
                    }
                    doseItemList.add(doseItem);
                    doseItemMap.put(time, doseItemList);
        
                }
            }

            for(LocalTime time : doseItemMap.keySet()){
                doseList.add(new Dose(time, doseItemMap.get(time)));
            }
            doseList.sort(Comparator.comparing(Dose::time));

            return new DateSchedule(date,userId,doseList);
        }else if(date.isBefore(currentDate)){
            // use history table
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
            List<MedicineHistory> medicineHistory = medicineHistoryRepo.findByUserIdAndActualAtBetween(userId, startOfDay, endOfDay);


            // group by medicine history base on actual time and medicine id
            HashMap<LocalDateTime, HashMap<String, MedicineHistory>> medicineHistoryMap = getMedicineHistoryMap(medicineHistory);

            List<Dose> doseList = new ArrayList<>();
            updateDoseListFromMedicineHashMap(userId, actorUserId, doseList, medicineHistoryMap);
            doseList.sort(Comparator.comparing(Dose::time));

            return new DateSchedule(date,userId,doseList);



        }else {
            // use both table get schedule from current and status from history

            LocalTime currentTime = LocalTime.now();

            // for time greater than current time get the schedule
            List<MedicineSchedule> medicineSchedule = medicineScheduleRepo.findByUserIdAndScheduleDayOfWeek(userId, dayOfWeek);
            if(medicineSchedule.isEmpty()){
                throw new IllegalArgumentException("Medicine schedule not found for userId: " + userId + " and date: " + date);
            }
            
            List<Dose> doseList = new ArrayList<>();
            HashMap<LocalTime, List<DoseItem>> doseItemMap = new HashMap<>();

            for(MedicineSchedule ms : medicineSchedule){

                Optional<Medicine> medicine = medicineRepo.findByIdAndUserId(ms.getMedicineId(), userId);
                if(medicine.isEmpty()){
                    continue;
                }

                Medicine m = medicine.get();
                String mId = m.getId();
                String mName = m.getName();
                String mDescription = m.getDescription();
                String mImage = m.getImageUrl();


                List<ScheduleItem> scheduleItemList = ms.getSchedule();
                for(ScheduleItem si : scheduleItemList){
                    // Filter by day of week
                    if(si.getDayOfWeek() != dayOfWeek){
                        continue;
                    }
                    LocalTime time = si.getTime();
                    if(!time.isBefore(currentTime)){
                        DoseItem doseItem = new DoseItem(mId, mName, mDescription, ScheduleStatus.SCHEDULED, mImage);
                        List<DoseItem> doseItemList = new ArrayList<>();
                        if(doseItemMap.containsKey(time)){
                            doseItemList = doseItemMap.get(time);
                        }
                        doseItemList.add(doseItem);
                        doseItemMap.put(time, doseItemList);
                    }
                }
            }

            for(LocalTime time : doseItemMap.keySet()){
                doseList.add(new Dose(time, doseItemMap.get(time)));
            }


            // for time less than current time get the history
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
            List<MedicineHistory> medicineHistory = medicineHistoryRepo.findByUserIdAndActualAtBetween(userId, startOfDay, endOfDay);

            // group by medicine history base on actual time and medicine id
            HashMap<LocalDateTime, HashMap<String, MedicineHistory>> medicineHistoryMap = getMedicineHistoryMap(medicineHistory);


            updateDoseListFromMedicineHashMap(userId, actorUserId, doseList, medicineHistoryMap);


            doseList.sort(Comparator.comparing(Dose::time));

            return new DateSchedule(date,userId,doseList);            
        }

    }

    private void updateDoseListFromMedicineHashMap(String userId, String actorUserId, List<Dose> doseList, HashMap<LocalDateTime, HashMap<String, MedicineHistory>> medicineHistoryMap) {
        for(LocalDateTime time : medicineHistoryMap.keySet()){
            HashMap<String, MedicineHistory> medicineHistoryMapTime = medicineHistoryMap.get(time);
            List<DoseItem> doseItemList = new ArrayList<>();
            for(String medicineId : medicineHistoryMapTime.keySet()){
                MedicineHistory mh = medicineHistoryMapTime.get(medicineId);
                Optional<Medicine> medicine = medicineRepo.findByIdAndUserId(medicineId, userId);
                if(medicine.isEmpty()){
                    continue;
                }
                Medicine m = medicine.get();
                String mId = m.getId();
                String mName = m.getName();
                String mDescription = m.getDescription();
                String mImage = m.getImageUrl();
                doseItemList.add(new DoseItem(mId, mName, mDescription, mh.getAction(), mImage));
            }
            doseList.add(new Dose(time.toLocalTime(), doseItemList));
        }
    }

    private static HashMap<LocalDateTime, HashMap<String, MedicineHistory>> getMedicineHistoryMap(List<MedicineHistory> medicineHistory) {
        HashMap<LocalDateTime, HashMap<String, MedicineHistory>> medicineHistoryMap = new HashMap<>();
        for(MedicineHistory mh : medicineHistory){
            LocalDateTime time = mh.getActualAt();
            if(medicineHistoryMap.containsKey(time)){
                HashMap<String, MedicineHistory> medicineHistoryMapTime = medicineHistoryMap.get(time);
                if(medicineHistoryMapTime.containsKey(mh.getMedicineId())){
                    MedicineHistory mhLatest = medicineHistoryMapTime.get(mh.getMedicineId());
                    if(mhLatest.getActionAt().isBefore(mh.getActionAt())){
                        medicineHistoryMapTime.put(mh.getMedicineId(), mh);
                    }
                }
                else {
                    medicineHistoryMapTime.put(mh.getMedicineId(), mh);
                }
                medicineHistoryMap.put(time, medicineHistoryMapTime);
            }
            else {
                HashMap<String, MedicineHistory> medicineHistoryMapTime = new HashMap<>();
                medicineHistoryMapTime.put(mh.getMedicineId(), mh);
                medicineHistoryMap.put(time, medicineHistoryMapTime);
            }
        }
        return medicineHistoryMap;
    }

    public MedicineHistoryResponse getMedicineHistory(String medicineId, Date startDate, Date endDate, String userId, String actorUserId) {
        // Authorize access
        userDetailsService.authorizeAccess(actorUserId, userId);

        List<MedicineHistory> medicineHistoryList = medicineHistoryRepo.findByMedicineIdAndActionAtBetweenAndUserId(medicineId, startDate, endDate, userId);

        Optional<Medicine> medicine = medicineRepo.findByIdAndUserId(medicineId, userId);
        if(medicine.isEmpty()){
            throw new IllegalArgumentException("Medicine not found");
        }
        Medicine m = medicine.get();
        String mName = m.getName();
        String mDescription = m.getDescription();
        String mImage = m.getImageUrl();

        List<MedicineDateSchedule> medicineDateSchedules = new ArrayList<>();

        HashMap<LocalDateTime, HashMap<String, MedicineHistory>> medicineHistoryMap = getMedicineHistoryMap(medicineHistoryList);
        for(LocalDateTime time : medicineHistoryMap.keySet()){
            HashMap<String, MedicineHistory> medicineHistoryMapTime = medicineHistoryMap.get(time);
            for(MedicineHistory mh : medicineHistoryMapTime.values()){
               MedicineDateSchedule medicineDateSchedule = new MedicineDateSchedule(mh.getActualAt(),mh.getAction().name());
               medicineDateSchedules.add(medicineDateSchedule);
            }
        }
        medicineDateSchedules.sort(Comparator.comparing(MedicineDateSchedule::actionTime));




        return new MedicineHistoryResponse(medicineId,userId,mName,mDescription,mImage,medicineDateSchedules); // Return empty Optional instead of null
    }

    public void removeSchedule(String userId, String actorUserId) {
        // Authorize access
        userDetailsService.authorizeAccess(actorUserId, userId);
        
        // TODO: Implement remove schedule logic
        // This method should remove all schedules for the given user

        List<Medicine> medicines = medicineRepo.findByUserIdAndState(userId, MedicineState.ACTIVE);
        for(Medicine m : medicines){
            m.setState(MedicineState.INACTIVE);
            m.setUpdatedAt(LocalDateTime.now());
            m.setUpdatedBy(actorUserId);
            medicineRepo.save(m);
        }

        medicineScheduleRepo.deleteByUserId(userId);
    }

    public void createSchedule(CreateScheduleRequest request, String actorUserId) {
        // Authorize access
        String userId = request.userId();
        userDetailsService.authorizeAccess(actorUserId, userId);
        
        // 1. Create medicines and 2. create schedules using embedded per-medicine schedules
        List<CreateScheduleRequest.MedicineInput> inputs = request.medicines();
        List<MedicineSchedule> medicineScheduleList = new ArrayList<>();

        for (CreateScheduleRequest.MedicineInput mi : inputs) {
            Medicine medicine = Medicine.builder()
                    .userId(userId)
                    .name(mi.medicineName())
                    .description(mi.medicineDescription())
                    .imageUrl(mi.medicineImage())
                    .state(MedicineState.ACTIVE)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .createdBy(actorUserId)
                    .updatedBy(actorUserId)
                    .build();

            Medicine saved = medicineRepo.save(medicine);

            MedicineSchedule medicineSchedule = new MedicineSchedule();
            medicineSchedule.setMedicineId(saved.getId());
            medicineSchedule.setCreatedBy(actorUserId);
            medicineSchedule.setUpdatedBy(actorUserId);
            medicineSchedule.setUserId(userId);
            medicineSchedule.setCreatedAt(LocalDateTime.now());
            medicineSchedule.setUpdatedAt(LocalDateTime.now());

            HashMap<DayOfWeek, HashMap<LocalTime, String>> scheduleMap = mi.schedule();
            List<ScheduleItem> schedule = getScheduleFromDayTimeMap(scheduleMap);
            medicineSchedule.setSchedule(schedule);
            medicineScheduleList.add(medicineSchedule);
        }

        medicineScheduleRepo.saveAll(medicineScheduleList);
    }

    private static List<ScheduleItem> getScheduleFromDayTimeMap(HashMap<DayOfWeek, HashMap<LocalTime, String>> scheduleMap) {
        List<ScheduleItem> schedule = new ArrayList<>();
        for(DayOfWeek dayOfWeek : scheduleMap.keySet()){
            for(LocalTime localTime : scheduleMap.get(dayOfWeek).keySet()){
                String status = scheduleMap.get(dayOfWeek).get(localTime);
                ScheduleItem scheduleItem = new ScheduleItem();
                scheduleItem.setDayOfWeek(dayOfWeek);
                scheduleItem.setTime(localTime);
                try {
                    scheduleItem.setStatus(ScheduleStatus.SCHEDULED);
                } catch (IllegalArgumentException ex) {
                    throw new IllegalArgumentException("Invalid schedule status: " + status);
                }
                schedule.add(scheduleItem);
            }
        }
        return schedule;
    }


    public void updateSchedule(UpdateScheduleRequest request, String actorUserId) {
        // Authorize access
        String userId = request.userId();
        userDetailsService.authorizeAccess(actorUserId, userId);
        
        // TODO: Implement update schedule logic
        // This method should:
        // 1. Update existing MedicineScheduleResponse entries
        // 2. Update Medicine entries if needed
        // 3. Log changes in MedicineHistoryResponse
        String medicineId = request.medicineId();
        Optional<MedicineSchedule> currentSchedule = medicineScheduleRepo.findByMedicineIdAndUserId(medicineId, userId);


        if(currentSchedule.isEmpty()){
            throw new IllegalArgumentException("No medicine schedule found for medicine " + medicineId);
        }
        MedicineSchedule medicineSchedule = currentSchedule.get();

        HashMap<DayOfWeek,HashMap<LocalTime,String>> updatedSchedule = request.schedule();
        List<ScheduleItem> schedule = getScheduleFromDayTimeMap(updatedSchedule);

        medicineSchedule.setUpdatedAt(LocalDateTime.now());
        medicineSchedule.setUpdatedBy(actorUserId);
        medicineSchedule.setSchedule(schedule);

        medicineScheduleRepo.save(medicineSchedule);

    }

    public void updateMedicineData(UpdateMedicineData request, String actorUserId) {
        // Authorize access
        userDetailsService.authorizeAccess(actorUserId, request.userId());
        
        // TODO: Implement update medicine data logic
        // This method should:

        Optional<Medicine> md = medicineRepo.findByIdAndUserId(request.medicineId(), request.userId());
        if(md.isEmpty()){
            throw new IllegalArgumentException("Medicine not found");
        }
        Medicine m =md.get();

        Medicine medicine = new Medicine(m.getId(),m.getUserId(),m.getName(),request.medicineDescription(),request.medicineImage(),request.medicineState(), m.getCreatedAt(), LocalDateTime.now(), m.getCreatedBy(),actorUserId);
        medicineRepo.save(medicine);
    }

    public void recordUserAction(UserActionRequest request, String actorUserId) {
        // Authorize access
        userDetailsService.authorizeAccess(actorUserId, request.userId());

        MedicineHistory medicineHistory = new MedicineHistory(request.userId(), request.medicineId(), request.action(), request.actionDateTime(), request.actualDateTime(), actorUserId);
        medicineHistoryRepo.save(medicineHistory);
    }
}
