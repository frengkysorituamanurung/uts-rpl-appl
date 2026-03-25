package com.meditrack.pharmacy.repository;

import com.meditrack.pharmacy.model.Medicine;
import com.meditrack.pharmacy.model.MedicineCategory;
import com.meditrack.pharmacy.model.MedicineStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicineRepository extends JpaRepository<Medicine, Long> {
    
    Optional<Medicine> findByMedicineCode(String medicineCode);
    
    List<Medicine> findByCategory(MedicineCategory category);
    
    List<Medicine> findByStatus(MedicineStatus status);
    
    List<Medicine> findByNameContainingIgnoreCase(String name);
    
    List<Medicine> findByStockQuantityLessThanEqual(Integer quantity);
}
