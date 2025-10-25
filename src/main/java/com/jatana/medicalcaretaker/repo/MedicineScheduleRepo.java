package com.jatana.medicalcaretaker.repo;

import com.jatana.medicalcaretaker.model.MedicineSchedule;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

@Repository
public interface MedicineScheduleRepo extends MongoRepository<MedicineSchedule, String> {

    Optional<MedicineSchedule> findByMedicineIdAndUserId(String medicineId, String userId);

    @Query("{ 'userId': ?0, 'schedule.dayOfWeek': ?1 }")
    List<MedicineSchedule> findByUserIdAndScheduleDayOfWeek(String userId, DayOfWeek dayOfWeek);

    void deleteByUserId(String userId);

    List<MedicineSchedule> findByUserId(String userId);
}

