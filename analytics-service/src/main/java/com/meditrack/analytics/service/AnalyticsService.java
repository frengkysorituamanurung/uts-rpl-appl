package com.meditrack.analytics.service;

import com.meditrack.analytics.client.*;
import com.meditrack.analytics.dto.DashboardSummaryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AnalyticsService {
    
    @Autowired
    private UserServiceClient userServiceClient;
    
    @Autowired
    private AppointmentServiceClient appointmentServiceClient;
    
    @Autowired
    private PaymentServiceClient paymentServiceClient;
    
    @Autowired
    private PharmacyServiceClient pharmacyServiceClient;
    
    public DashboardSummaryDTO getDashboardSummary() {
        DashboardSummaryDTO summary = new DashboardSummaryDTO();
        
        // Get user statistics
        summary.setUserStatistics(getUserStatistics());
        
        // Get appointment statistics
        summary.setAppointmentStatistics(getAppointmentStatistics());
        
        // Get payment statistics
        summary.setPaymentStatistics(getPaymentStatistics());
        
        // Get pharmacy statistics
        summary.setPharmacyStatistics(getPharmacyStatistics());
        
        return summary;
    }
    
    public DashboardSummaryDTO.UserStatistics getUserStatistics() {
        DashboardSummaryDTO.UserStatistics stats = new DashboardSummaryDTO.UserStatistics();
        
        try {
            Map<String, Object> doctors = userServiceClient.getUsersByRole("DOCTOR");
            Map<String, Object> patients = userServiceClient.getUsersByRole("PATIENT");
            Map<String, Object> pharmacists = userServiceClient.getUsersByRole("PHARMACIST");
            Map<String, Object> admins = userServiceClient.getUsersByRole("ADMIN");
            
            int doctorCount = getDataSize(doctors);
            int patientCount = getDataSize(patients);
            int pharmacistCount = getDataSize(pharmacists);
            int adminCount = getDataSize(admins);
            
            stats.setTotalDoctors(doctorCount);
            stats.setTotalPatients(patientCount);
            stats.setTotalPharmacists(pharmacistCount);
            stats.setTotalAdmins(adminCount);
            stats.setTotalUsers(doctorCount + patientCount + pharmacistCount + adminCount);
        } catch (Exception e) {
            System.err.println("Error fetching user statistics: " + e.getMessage());
            stats.setTotalDoctors(0);
            stats.setTotalPatients(0);
            stats.setTotalPharmacists(0);
            stats.setTotalAdmins(0);
            stats.setTotalUsers(0);
        }
        
        return stats;
    }
    
    public DashboardSummaryDTO.AppointmentStatistics getAppointmentStatistics() {
        DashboardSummaryDTO.AppointmentStatistics stats = new DashboardSummaryDTO.AppointmentStatistics();
        
        try {
            Map<String, Object> scheduled = appointmentServiceClient.getAppointmentsByStatus("SCHEDULED");
            Map<String, Object> completed = appointmentServiceClient.getAppointmentsByStatus("COMPLETED");
            Map<String, Object> cancelled = appointmentServiceClient.getAppointmentsByStatus("CANCELLED");
            
            int scheduledCount = getDataSize(scheduled);
            int completedCount = getDataSize(completed);
            int cancelledCount = getDataSize(cancelled);
            
            stats.setTotalScheduled(scheduledCount);
            stats.setTotalCompleted(completedCount);
            stats.setTotalCancelled(cancelledCount);
            stats.setTotalAppointments(scheduledCount + completedCount + cancelledCount);
        } catch (Exception e) {
            System.err.println("Error fetching appointment statistics: " + e.getMessage());
            stats.setTotalScheduled(0);
            stats.setTotalCompleted(0);
            stats.setTotalCancelled(0);
            stats.setTotalAppointments(0);
        }
        
        return stats;
    }
    
    public DashboardSummaryDTO.PaymentStatistics getPaymentStatistics() {
        DashboardSummaryDTO.PaymentStatistics stats = new DashboardSummaryDTO.PaymentStatistics();
        
        try {
            Map<String, Object> pending = paymentServiceClient.getPaymentsByStatus("PENDING");
            Map<String, Object> completed = paymentServiceClient.getPaymentsByStatus("COMPLETED");
            Map<String, Object> failed = paymentServiceClient.getPaymentsByStatus("FAILED");
            
            stats.setTotalPending(getDataSize(pending));
            stats.setTotalCompleted(getDataSize(completed));
            stats.setTotalFailed(getDataSize(failed));
            
            // Calculate total revenue from completed payments
            double revenue = calculateRevenue(completed);
            stats.setTotalRevenue(revenue);
        } catch (Exception e) {
            System.err.println("Error fetching payment statistics: " + e.getMessage());
            stats.setTotalPending(0);
            stats.setTotalCompleted(0);
            stats.setTotalFailed(0);
            stats.setTotalRevenue(0.0);
        }
        
        return stats;
    }
    
    public DashboardSummaryDTO.PharmacyStatistics getPharmacyStatistics() {
        DashboardSummaryDTO.PharmacyStatistics stats = new DashboardSummaryDTO.PharmacyStatistics();
        
        try {
            Map<String, Object> allMedicines = pharmacyServiceClient.getAllMedicines();
            Map<String, Object> lowStock = pharmacyServiceClient.getLowStockMedicines();
            
            stats.setTotalMedicines(getDataSize(allMedicines));
            stats.setLowStockItems(getDataSize(lowStock));
        } catch (Exception e) {
            System.err.println("Error fetching pharmacy statistics: " + e.getMessage());
            stats.setTotalMedicines(0);
            stats.setLowStockItems(0);
        }
        
        return stats;
    }
    
    private int getDataSize(Map<String, Object> response) {
        if (response != null && response.containsKey("data")) {
            Object data = response.get("data");
            if (data instanceof List) {
                return ((List<?>) data).size();
            }
        }
        return 0;
    }
    
    private double calculateRevenue(Map<String, Object> completedPayments) {
        if (completedPayments != null && completedPayments.containsKey("data")) {
            Object data = completedPayments.get("data");
            if (data instanceof List) {
                List<?> payments = (List<?>) data;
                double total = 0.0;
                for (Object payment : payments) {
                    if (payment instanceof Map) {
                        Map<?, ?> paymentMap = (Map<?, ?>) payment;
                        Object amount = paymentMap.get("amount");
                        if (amount instanceof Number) {
                            total += ((Number) amount).doubleValue();
                        }
                    }
                }
                return total;
            }
        }
        return 0.0;
    }
}
