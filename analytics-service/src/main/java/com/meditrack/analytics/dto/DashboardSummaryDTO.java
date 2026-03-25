package com.meditrack.analytics.dto;

public class DashboardSummaryDTO {
    private UserStatistics userStatistics;
    private AppointmentStatistics appointmentStatistics;
    private PaymentStatistics paymentStatistics;
    private PharmacyStatistics pharmacyStatistics;
    
    // Getters and Setters
    public UserStatistics getUserStatistics() {
        return userStatistics;
    }
    
    public void setUserStatistics(UserStatistics userStatistics) {
        this.userStatistics = userStatistics;
    }
    
    public AppointmentStatistics getAppointmentStatistics() {
        return appointmentStatistics;
    }
    
    public void setAppointmentStatistics(AppointmentStatistics appointmentStatistics) {
        this.appointmentStatistics = appointmentStatistics;
    }
    
    public PaymentStatistics getPaymentStatistics() {
        return paymentStatistics;
    }
    
    public void setPaymentStatistics(PaymentStatistics paymentStatistics) {
        this.paymentStatistics = paymentStatistics;
    }
    
    public PharmacyStatistics getPharmacyStatistics() {
        return pharmacyStatistics;
    }
    
    public void setPharmacyStatistics(PharmacyStatistics pharmacyStatistics) {
        this.pharmacyStatistics = pharmacyStatistics;
    }
    
    // Inner classes for statistics
    public static class UserStatistics {
        private Integer totalDoctors;
        private Integer totalPatients;
        private Integer totalPharmacists;
        private Integer totalAdmins;
        private Integer totalUsers;
        
        // Getters and Setters
        public Integer getTotalDoctors() { return totalDoctors; }
        public void setTotalDoctors(Integer totalDoctors) { this.totalDoctors = totalDoctors; }
        
        public Integer getTotalPatients() { return totalPatients; }
        public void setTotalPatients(Integer totalPatients) { this.totalPatients = totalPatients; }
        
        public Integer getTotalPharmacists() { return totalPharmacists; }
        public void setTotalPharmacists(Integer totalPharmacists) { this.totalPharmacists = totalPharmacists; }
        
        public Integer getTotalAdmins() { return totalAdmins; }
        public void setTotalAdmins(Integer totalAdmins) { this.totalAdmins = totalAdmins; }
        
        public Integer getTotalUsers() { return totalUsers; }
        public void setTotalUsers(Integer totalUsers) { this.totalUsers = totalUsers; }
    }
    
    public static class AppointmentStatistics {
        private Integer totalScheduled;
        private Integer totalCompleted;
        private Integer totalCancelled;
        private Integer totalAppointments;
        
        // Getters and Setters
        public Integer getTotalScheduled() { return totalScheduled; }
        public void setTotalScheduled(Integer totalScheduled) { this.totalScheduled = totalScheduled; }
        
        public Integer getTotalCompleted() { return totalCompleted; }
        public void setTotalCompleted(Integer totalCompleted) { this.totalCompleted = totalCompleted; }
        
        public Integer getTotalCancelled() { return totalCancelled; }
        public void setTotalCancelled(Integer totalCancelled) { this.totalCancelled = totalCancelled; }
        
        public Integer getTotalAppointments() { return totalAppointments; }
        public void setTotalAppointments(Integer totalAppointments) { this.totalAppointments = totalAppointments; }
    }
    
    public static class PaymentStatistics {
        private Integer totalPending;
        private Integer totalCompleted;
        private Integer totalFailed;
        private Double totalRevenue;
        
        // Getters and Setters
        public Integer getTotalPending() { return totalPending; }
        public void setTotalPending(Integer totalPending) { this.totalPending = totalPending; }
        
        public Integer getTotalCompleted() { return totalCompleted; }
        public void setTotalCompleted(Integer totalCompleted) { this.totalCompleted = totalCompleted; }
        
        public Integer getTotalFailed() { return totalFailed; }
        public void setTotalFailed(Integer totalFailed) { this.totalFailed = totalFailed; }
        
        public Double getTotalRevenue() { return totalRevenue; }
        public void setTotalRevenue(Double totalRevenue) { this.totalRevenue = totalRevenue; }
    }
    
    public static class PharmacyStatistics {
        private Integer totalMedicines;
        private Integer lowStockItems;
        
        // Getters and Setters
        public Integer getTotalMedicines() { return totalMedicines; }
        public void setTotalMedicines(Integer totalMedicines) { this.totalMedicines = totalMedicines; }
        
        public Integer getLowStockItems() { return lowStockItems; }
        public void setLowStockItems(Integer lowStockItems) { this.lowStockItems = lowStockItems; }
    }
}
