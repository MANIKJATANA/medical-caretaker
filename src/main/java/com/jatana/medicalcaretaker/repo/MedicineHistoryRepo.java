package com.jatana.medicalcaretaker.repo;

import com.jatana.medicalcaretaker.model.MedicineHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Repository
public interface MedicineHistoryRepo extends MongoRepository<MedicineHistory, String> {

    @Query("{ 'userId': ?0, 'actualAt': { $gte: ?1, $lt: ?2 } }")
    List<MedicineHistory> findByUserIdAndActualAtBetween(String userId, LocalDateTime startOfDay, LocalDateTime endOfDay);

    @Query("{ 'userId': ?3, 'medicineId': ?0, 'actionAt': { $gte: ?1, $lte: ?2 } }")
    List<MedicineHistory> findByMedicineIdAndActionAtBetweenAndUserId(String medicineId, Date startDate, Date endDate, String userId);
}

