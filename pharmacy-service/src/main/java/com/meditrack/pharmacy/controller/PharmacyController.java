package com.meditrack.pharmacy.controller;

import com.meditrack.common.dto.ApiResponse;
import com.meditrack.pharmacy.dto.*;
import com.meditrack.pharmacy.model.MedicineCategory;
import com.meditrack.pharmacy.model.OrderStatus;
import com.meditrack.pharmacy.service.MedicineService;
import com.meditrack.pharmacy.service.PrescriptionOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pharmacy")
public class PharmacyController {
    
    @Autowired
    private MedicineService medicineService;
    
    @Autowired
    private PrescriptionOrderService orderService;
    
    // Medicine Endpoints
    
    @PostMapping("/medicines")
    public ResponseEntity<ApiResponse<MedicineDTO>> createMedicine(@RequestBody CreateMedicineRequest request) {
        MedicineDTO medicine = medicineService.createMedicine(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Medicine created successfully", medicine));
    }
    
    @GetMapping("/medicines/{id}")
    public ResponseEntity<ApiResponse<MedicineDTO>> getMedicineById(@PathVariable Long id) {
        MedicineDTO medicine = medicineService.getMedicineById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Medicine retrieved successfully", medicine));
    }
    
    @GetMapping("/medicines/code/{code}")
    public ResponseEntity<ApiResponse<MedicineDTO>> getMedicineByCode(@PathVariable String code) {
        MedicineDTO medicine = medicineService.getMedicineByCode(code);
        return ResponseEntity.ok(new ApiResponse<>(true, "Medicine retrieved successfully", medicine));
    }
    
    @GetMapping("/medicines")
    public ResponseEntity<ApiResponse<List<MedicineDTO>>> getAllMedicines() {
        List<MedicineDTO> medicines = medicineService.getAllMedicines();
        return ResponseEntity.ok(new ApiResponse<>(true, "Medicines retrieved successfully", medicines));
    }
    
    @GetMapping("/medicines/category/{category}")
    public ResponseEntity<ApiResponse<List<MedicineDTO>>> getMedicinesByCategory(@PathVariable MedicineCategory category) {
        List<MedicineDTO> medicines = medicineService.getMedicinesByCategory(category);
        return ResponseEntity.ok(new ApiResponse<>(true, "Medicines retrieved successfully", medicines));
    }
    
    @GetMapping("/medicines/search")
    public ResponseEntity<ApiResponse<List<MedicineDTO>>> searchMedicines(@RequestParam String name) {
        List<MedicineDTO> medicines = medicineService.searchMedicines(name);
        return ResponseEntity.ok(new ApiResponse<>(true, "Search completed successfully", medicines));
    }
    
    @GetMapping("/medicines/low-stock")
    public ResponseEntity<ApiResponse<List<MedicineDTO>>> getLowStockMedicines() {
        List<MedicineDTO> medicines = medicineService.getLowStockMedicines();
        return ResponseEntity.ok(new ApiResponse<>(true, "Low stock medicines retrieved", medicines));
    }
    
    @PutMapping("/medicines/{id}")
    public ResponseEntity<ApiResponse<MedicineDTO>> updateMedicine(
            @PathVariable Long id,
            @RequestBody CreateMedicineRequest request) {
        MedicineDTO medicine = medicineService.updateMedicine(id, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Medicine updated successfully", medicine));
    }
    
    @PutMapping("/medicines/{id}/stock")
    public ResponseEntity<ApiResponse<MedicineDTO>> updateStock(
            @PathVariable Long id,
            @RequestBody UpdateStockRequest request) {
        MedicineDTO medicine = medicineService.updateStock(id, request.getQuantity(), request.getOperation());
        return ResponseEntity.ok(new ApiResponse<>(true, "Stock updated successfully", medicine));
    }

    
    // Prescription Order Endpoints
    
    @PostMapping("/orders")
    public ResponseEntity<ApiResponse<PrescriptionOrderDTO>> createOrder(@RequestBody CreateOrderRequest request) {
        PrescriptionOrderDTO order = orderService.createOrder(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Order created successfully", order));
    }
    
    @GetMapping("/orders/{id}")
    public ResponseEntity<ApiResponse<PrescriptionOrderDTO>> getOrderById(@PathVariable Long id) {
        PrescriptionOrderDTO order = orderService.getOrderById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Order retrieved successfully", order));
    }
    
    @GetMapping("/orders/number/{orderNumber}")
    public ResponseEntity<ApiResponse<PrescriptionOrderDTO>> getOrderByNumber(@PathVariable String orderNumber) {
        PrescriptionOrderDTO order = orderService.getOrderByNumber(orderNumber);
        return ResponseEntity.ok(new ApiResponse<>(true, "Order retrieved successfully", order));
    }
    
    @GetMapping("/orders/patient/{patientId}")
    public ResponseEntity<ApiResponse<List<PrescriptionOrderDTO>>> getOrdersByPatient(@PathVariable Long patientId) {
        List<PrescriptionOrderDTO> orders = orderService.getOrdersByPatient(patientId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Orders retrieved successfully", orders));
    }
    
    @GetMapping("/orders/pharmacist/{pharmacistId}")
    public ResponseEntity<ApiResponse<List<PrescriptionOrderDTO>>> getOrdersByPharmacist(@PathVariable Long pharmacistId) {
        List<PrescriptionOrderDTO> orders = orderService.getOrdersByPharmacist(pharmacistId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Orders retrieved successfully", orders));
    }
    
    @GetMapping("/orders/status/{status}")
    public ResponseEntity<ApiResponse<List<PrescriptionOrderDTO>>> getOrdersByStatus(@PathVariable OrderStatus status) {
        List<PrescriptionOrderDTO> orders = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(new ApiResponse<>(true, "Orders retrieved successfully", orders));
    }
    
    @PutMapping("/orders/{id}/status")
    public ResponseEntity<ApiResponse<PrescriptionOrderDTO>> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        OrderStatus status = OrderStatus.valueOf(request.get("status"));
        String notes = request.get("pharmacistNotes");
        PrescriptionOrderDTO order = orderService.updateOrderStatus(id, status, notes);
        return ResponseEntity.ok(new ApiResponse<>(true, "Order status updated successfully", order));
    }
}
