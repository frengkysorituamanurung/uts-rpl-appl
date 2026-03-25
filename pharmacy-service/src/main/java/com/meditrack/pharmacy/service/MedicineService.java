package com.meditrack.pharmacy.service;

import com.meditrack.pharmacy.dto.CreateMedicineRequest;
import com.meditrack.pharmacy.dto.MedicineDTO;
import com.meditrack.pharmacy.model.Medicine;
import com.meditrack.pharmacy.model.MedicineCategory;
import com.meditrack.pharmacy.model.MedicineStatus;
import com.meditrack.pharmacy.repository.MedicineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MedicineService {
    
    @Autowired
    private MedicineRepository medicineRepository;
    
    @Transactional
    public MedicineDTO createMedicine(CreateMedicineRequest request) {
        Medicine medicine = new Medicine();
        medicine.setMedicineCode(generateMedicineCode());
        medicine.setName(request.getName());
        medicine.setDescription(request.getDescription());
        medicine.setManufacturer(request.getManufacturer());
        medicine.setCategory(request.getCategory());
        medicine.setDosageForm(request.getDosageForm());
        medicine.setStrength(request.getStrength());
        medicine.setPrice(request.getPrice());
        medicine.setStockQuantity(request.getStockQuantity());
        medicine.setReorderLevel(request.getReorderLevel());
        medicine.setExpiryDate(request.getExpiryDate());
        medicine.setRequiresPrescription(request.getRequiresPrescription());
        medicine.setStorageConditions(request.getStorageConditions());
        medicine.setSideEffects(request.getSideEffects());
        
        // Set initial status based on stock
        medicine.setStatus(determineStatus(request.getStockQuantity(), request.getReorderLevel(), request.getExpiryDate()));
        
        Medicine saved = medicineRepository.save(medicine);
        return convertToDTO(saved);
    }

    
    public MedicineDTO getMedicineById(Long id) {
        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medicine not found with id: " + id));
        return convertToDTO(medicine);
    }
    
    public MedicineDTO getMedicineByCode(String code) {
        Medicine medicine = medicineRepository.findByMedicineCode(code)
                .orElseThrow(() -> new RuntimeException("Medicine not found with code: " + code));
        return convertToDTO(medicine);
    }
    
    public List<MedicineDTO> getAllMedicines() {
        return medicineRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<MedicineDTO> getMedicinesByCategory(MedicineCategory category) {
        return medicineRepository.findByCategory(category).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<MedicineDTO> searchMedicines(String name) {
        return medicineRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<MedicineDTO> getLowStockMedicines() {
        return medicineRepository.findAll().stream()
                .filter(m -> m.getStockQuantity() <= m.getReorderLevel())
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public MedicineDTO updateStock(Long id, Integer quantity, String operation) {
        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medicine not found"));
        
        int newStock;
        if ("ADD".equalsIgnoreCase(operation)) {
            newStock = medicine.getStockQuantity() + quantity;
        } else if ("SUBTRACT".equalsIgnoreCase(operation)) {
            newStock = medicine.getStockQuantity() - quantity;
            if (newStock < 0) {
                throw new RuntimeException("Insufficient stock");
            }
        } else {
            throw new RuntimeException("Invalid operation. Use ADD or SUBTRACT");
        }
        
        medicine.setStockQuantity(newStock);
        medicine.setStatus(determineStatus(newStock, medicine.getReorderLevel(), medicine.getExpiryDate()));
        
        Medicine updated = medicineRepository.save(medicine);
        return convertToDTO(updated);
    }
    
    @Transactional
    public MedicineDTO updateMedicine(Long id, CreateMedicineRequest request) {
        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medicine not found"));
        
        medicine.setName(request.getName());
        medicine.setDescription(request.getDescription());
        medicine.setManufacturer(request.getManufacturer());
        medicine.setCategory(request.getCategory());
        medicine.setDosageForm(request.getDosageForm());
        medicine.setStrength(request.getStrength());
        medicine.setPrice(request.getPrice());
        medicine.setStockQuantity(request.getStockQuantity());
        medicine.setReorderLevel(request.getReorderLevel());
        medicine.setExpiryDate(request.getExpiryDate());
        medicine.setRequiresPrescription(request.getRequiresPrescription());
        medicine.setStorageConditions(request.getStorageConditions());
        medicine.setSideEffects(request.getSideEffects());
        medicine.setStatus(determineStatus(request.getStockQuantity(), request.getReorderLevel(), request.getExpiryDate()));
        
        Medicine updated = medicineRepository.save(medicine);
        return convertToDTO(updated);
    }
    
    private String generateMedicineCode() {
        return "MED-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    private MedicineStatus determineStatus(Integer stock, Integer reorderLevel, LocalDate expiryDate) {
        if (expiryDate != null && expiryDate.isBefore(LocalDate.now())) {
            return MedicineStatus.EXPIRED;
        }
        if (stock == 0) {
            return MedicineStatus.OUT_OF_STOCK;
        }
        if (stock <= reorderLevel) {
            return MedicineStatus.LOW_STOCK;
        }
        return MedicineStatus.AVAILABLE;
    }
    
    private MedicineDTO convertToDTO(Medicine medicine) {
        MedicineDTO dto = new MedicineDTO();
        dto.setId(medicine.getId());
        dto.setMedicineCode(medicine.getMedicineCode());
        dto.setName(medicine.getName());
        dto.setDescription(medicine.getDescription());
        dto.setManufacturer(medicine.getManufacturer());
        dto.setCategory(medicine.getCategory());
        dto.setDosageForm(medicine.getDosageForm());
        dto.setStrength(medicine.getStrength());
        dto.setPrice(medicine.getPrice());
        dto.setStockQuantity(medicine.getStockQuantity());
        dto.setReorderLevel(medicine.getReorderLevel());
        dto.setExpiryDate(medicine.getExpiryDate());
        dto.setRequiresPrescription(medicine.getRequiresPrescription());
        dto.setStatus(medicine.getStatus());
        dto.setStorageConditions(medicine.getStorageConditions());
        dto.setSideEffects(medicine.getSideEffects());
        return dto;
    }
}
