package com.jatana.medicalcaretaker.repo;

import com.jatana.medicalcaretaker.model.Medicine;
import com.jatana.medicalcaretaker.model.medicineEnums.MedicineState;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicineServiceRepo extends MongoRepository<Medicine, String> {

    List<Medicine> findByUserId(String userId);

    List<Medicine> findByUserIdAndState(String userId, MedicineState state);

    Optional<Medicine> findByIdAndUserId(String id, String userId);
}
