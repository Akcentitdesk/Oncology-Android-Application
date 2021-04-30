package com.indiaoncology.model.doctor;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.indiaoncology.model.doctor.location.LocationDatum;

import java.util.ArrayList;
import java.util.List;

public class DoctorData {
    @SerializedName("doctor_reviews")
    @Expose
    private String doctorReviews;
    @SerializedName("rating")
    @Expose
    private String rating;
    @SerializedName("doctor_category_id")
    @Expose
    private String doctorCategoryId;
    @SerializedName("doctor_category_name")
    @Expose
    private String doctorCategoryName;
    @SerializedName("id")
    @Expose
    private String doctorId;
    @SerializedName("doctor_name")
    @Expose
    private String doctorName;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("experience")
    @Expose
    private String experience;
    @SerializedName("location_data")
    @Expose
    private List<LocationDatum> location_data = null;
    @SerializedName("specializations")
    @Expose
    private ArrayList<String> specializations = null;

    public ArrayList<String> getSpecializations() {
        return specializations;
    }

    @SerializedName("registration")
    @Expose
    private String registration;
    @SerializedName("awards")
    @Expose
    private String awards;
    @SerializedName("membership")
    @Expose
    private String membership;
    @SerializedName("about")
    @Expose
    private String about;
    @SerializedName("full_experience")
    @Expose
    private String fullExperience;
    @SerializedName("qualification")
    @Expose
    private String qualification;
    @SerializedName("organ_specialization")
    @Expose
    private ArrayList<String> organ_specialization = null;
    @SerializedName("education")
    @Expose
    private String education;

    public void setSpecializations(ArrayList<String> specializations) {
        this.specializations = specializations;
    }

    public ArrayList<String> getOrgan_specialization() {
        return organ_specialization;
    }

    public void setOrgan_specialization(ArrayList<String> organ_specialization) {
        this.organ_specialization = organ_specialization;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getFullExperience() {
        return fullExperience;
    }

    public void setFullExperience(String fullExperience) {
        this.fullExperience = fullExperience;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public List<LocationDatum> getLocation_data() {
        return location_data;
    }


    public String getRegistration() {
        return registration;
    }

    public void setRegistration(String registration) {
        this.registration = registration;
    }

    public String getAwards() {
        return awards;
    }

    public void setAwards(String awards) {
        this.awards = awards;
    }

    public String getMembership() {
        return membership;
    }

    public void setMembership(String membership) {
        this.membership = membership;
    }

    public void setLocation_data(List<LocationDatum> location_data) {
        this.location_data = location_data;
    }

    public String getDoctorReviews() {
        return doctorReviews;
    }

    public void setDoctorReviews(String doctorReviews) {
        this.doctorReviews = doctorReviews;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getDoctorCategoryId() {
        return doctorCategoryId;
    }

    public void setDoctorCategoryId(String doctorCategoryId) {
        this.doctorCategoryId = doctorCategoryId;
    }

    public String getDoctorCategoryName() {
        return doctorCategoryName;
    }

    public void setDoctorCategoryName(String doctorCategoryName) {
        this.doctorCategoryName = doctorCategoryName;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }
}
