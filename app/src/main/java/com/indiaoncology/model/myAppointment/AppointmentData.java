package com.indiaoncology.model.myAppointment;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.indiaoncology.model.patient.PatientData;

public class AppointmentData {
    @SerializedName("appointment_order_id")
    @Expose
    private String Appointment_id;
    @SerializedName("doctor_name")
    @Expose
    private String doctorName;
    @SerializedName("doctor_id")
    @Expose
    private String doctorId;
    @SerializedName("doctor_profile")
    @Expose
    private String doctorProfile;
    @SerializedName("doctor_category_name")
    @Expose
    private String doctor_category_name;
    @SerializedName("doctor_experience")
    @Expose
    private String doctor_experience;
    @SerializedName("doctor_location")
    @Expose
    private String doctorLocation;
    @SerializedName("doctor_fees")
    @Expose
    private String doctorFees;
    @SerializedName("appointment_status")
    @Expose
    private String appointment_status;
    @SerializedName("appointment_date")
    @Expose
    private String appointment_date;
    @SerializedName("appointment_time")
    @Expose
    private String appointment_time;
    @SerializedName("clinic_name")
    @Expose
    private String clinic_name;
    @SerializedName("is_cancelled")
    @Expose
    private String is_cancelled;
    @SerializedName("is_rated")
    @Expose
    private String is_rated;
    @SerializedName("status_date")
    @Expose
    private String cancelled_date;
    @SerializedName("comment")
    @Expose
    private String comment;
    @SerializedName("selected_patient")
    @Expose
    private PatientData selectedPatientData;

    public String getDoctor_category_name() {
        return doctor_category_name;
    }

    public void setDoctor_category_name(String doctor_category_name) {
        this.doctor_category_name = doctor_category_name;
    }

    public String getDoctor_experience() {
        return doctor_experience;
    }

    public void setDoctor_experience(String doctor_experience) {
        this.doctor_experience = doctor_experience;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getIs_rated() {
        return is_rated;
    }

    public void setIs_rated(String is_rated) {
        this.is_rated = is_rated;
    }

    public String getCancelled_date() {
        return cancelled_date;
    }

    public void setCancelled_date(String cancelled_date) {
        this.cancelled_date = cancelled_date;
    }

    public String getIs_cancelled() {
        return is_cancelled;
    }

    public void setIs_cancelled(String is_cancelled) {
        this.is_cancelled = is_cancelled;
    }

    public String getAppointment_id() {
        return Appointment_id;
    }

    public void setAppointment_id(String appointment_id) {
        Appointment_id = appointment_id;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getDoctorProfile() {
        return doctorProfile;
    }

    public void setDoctorProfile(String doctorProfile) {
        this.doctorProfile = doctorProfile;
    }

    public String getDoctorLocation() {
        return doctorLocation;
    }

    public void setDoctorLocation(String doctorLocation) {
        this.doctorLocation = doctorLocation;
    }

    public String getDoctorFees() {
        return doctorFees;
    }

    public void setDoctorFees(String doctorFees) {
        this.doctorFees = doctorFees;
    }

    public String getAppointment_status() {
        return appointment_status;
    }

    public void setAppointment_status(String appointment_status) {
        this.appointment_status = appointment_status;
    }

    public String getAppointment_date() {
        return appointment_date;
    }

    public void setAppointment_date(String appointment_date) {
        this.appointment_date = appointment_date;
    }

    public String getAppointment_time() {
        return appointment_time;
    }

    public void setAppointment_time(String appointment_time) {
        this.appointment_time = appointment_time;
    }

    public String getClinic_name() {
        return clinic_name;
    }

    public void setClinic_name(String clinic_name) {
        this.clinic_name = clinic_name;
    }

    public PatientData getSelectedPatientData() {
        return selectedPatientData;
    }

    public void setSelectedPatientData(PatientData selectedPatientData) {
        this.selectedPatientData = selectedPatientData;
    }
}
