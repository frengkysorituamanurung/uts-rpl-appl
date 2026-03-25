package com.meditrack.pharmacy.service;

import com.meditrack.pharmacy.dto.*;
import com.meditrack.pharmacy.model.*;
import com.meditrack.pharmacy.repository.MedicineRepository;
import com.meditrack.pharmacy.repository.PrescriptionOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PrescriptionOrderService {
    
    @Autowired
    private PrescriptionOrderRepository orderRepository;
    
    @Autowired
    private MedicineRepository medicineRepository;
    
    @Autowired
    private MedicineService medicineService;
    
    @Transactional
    public PrescriptionOrderDTO createOrder(CreateOrderRequest request) {
        // Check if order already exists for this prescription
        orderRepository.findByPrescriptionNumber(request.getPrescriptionNumber())
                .ifPresent(order -> {
                    throw new RuntimeException("Order already exists for prescription: " + request.getPrescriptionNumber());
                });
        
        PrescriptionOrder order = new PrescriptionOrder();
        order.setOrderNumber(generateOrderNumber());
        order.setPrescriptionNumber(request.getPrescriptionNumber());
        order.setPatientId(request.getPatientId());
        order.setPharmacistId(request.getPharmacistId());
        order.setNotes(request.getNotes());
        order.setStatus(OrderStatus.PENDING);
        
        BigDecimal totalAmount = BigDecimal.ZERO;
        
        // Process order items
        for (OrderItemRequest itemRequest : request.getItems()) {
            Medicine medicine = medicineRepository.findById(itemRequest.getMedicineId())
                    .orElseThrow(() -> new RuntimeException("Medicine not found: " + itemRequest.getMedicineId()));
            
            // Check stock availability
            if (medicine.getStockQuantity() < itemRequest.getQuantity()) {
                throw new RuntimeException("Insufficient stock for medicine: " + medicine.getName());
            }
            
            OrderItem item = new OrderItem();
            item.setMedicine(medicine);
            item.setQuantity(itemRequest.getQuantity());
            item.setUnitPrice(medicine.getPrice());
            item.setSubtotal(medicine.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity())));
            item.setInstructions(itemRequest.getInstructions());
            
            order.addItem(item);
            totalAmount = totalAmount.add(item.getSubtotal());
        }
        
        order.setTotalAmount(totalAmount);
        
        PrescriptionOrder saved = orderRepository.save(order);
        return convertToDTO(saved);
    }

    
    public PrescriptionOrderDTO getOrderById(Long id) {
        PrescriptionOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return convertToDTO(order);
    }
    
    public PrescriptionOrderDTO getOrderByNumber(String orderNumber) {
        PrescriptionOrder order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return convertToDTO(order);
    }
    
    public List<PrescriptionOrderDTO> getOrdersByPatient(Long patientId) {
        return orderRepository.findByPatientId(patientId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<PrescriptionOrderDTO> getOrdersByPharmacist(Long pharmacistId) {
        return orderRepository.findByPharmacistId(pharmacistId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<PrescriptionOrderDTO> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public PrescriptionOrderDTO updateOrderStatus(Long id, OrderStatus newStatus, String pharmacistNotes) {
        PrescriptionOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        // Validate status transition
        if (order.getStatus() == OrderStatus.DISPENSED) {
            throw new RuntimeException("Cannot update status of dispensed order");
        }
        
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new RuntimeException("Cannot update status of cancelled order");
        }
        
        order.setStatus(newStatus);
        if (pharmacistNotes != null) {
            order.setPharmacistNotes(pharmacistNotes);
        }
        
        // If dispensing, reduce stock
        if (newStatus == OrderStatus.DISPENSED) {
            order.setDispensedAt(LocalDateTime.now());
            
            for (OrderItem item : order.getItems()) {
                medicineService.updateStock(
                    item.getMedicine().getId(),
                    item.getQuantity(),
                    "SUBTRACT"
                );
            }
        }
        
        PrescriptionOrder updated = orderRepository.save(order);
        return convertToDTO(updated);
    }
    
    private String generateOrderNumber() {
        return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    private PrescriptionOrderDTO convertToDTO(PrescriptionOrder order) {
        PrescriptionOrderDTO dto = new PrescriptionOrderDTO();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setPrescriptionNumber(order.getPrescriptionNumber());
        dto.setPatientId(order.getPatientId());
        dto.setPharmacistId(order.getPharmacistId());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus());
        dto.setOrderedAt(order.getOrderedAt());
        dto.setDispensedAt(order.getDispensedAt());
        dto.setNotes(order.getNotes());
        dto.setPharmacistNotes(order.getPharmacistNotes());
        
        List<OrderItemDTO> itemDTOs = order.getItems().stream()
                .map(this::convertItemToDTO)
                .collect(Collectors.toList());
        dto.setItems(itemDTOs);
        
        return dto;
    }
    
    private OrderItemDTO convertItemToDTO(OrderItem item) {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setId(item.getId());
        dto.setMedicineId(item.getMedicine().getId());
        dto.setMedicineName(item.getMedicine().getName());
        dto.setQuantity(item.getQuantity());
        dto.setUnitPrice(item.getUnitPrice());
        dto.setSubtotal(item.getSubtotal());
        dto.setInstructions(item.getInstructions());
        return dto;
    }
}
