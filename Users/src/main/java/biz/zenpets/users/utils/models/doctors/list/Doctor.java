package biz.zenpets.users.utils.models.doctors.list;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import biz.zenpets.users.utils.models.clinics.images.ClinicImage;

public class Doctor {

    @SerializedName("clinicID") private String clinicID;
    @SerializedName("clinicName") private String clinicName;
    @SerializedName("clinicAddress") private String clinicAddress;
    @SerializedName("clinicPinCode") private String clinicPinCode;
    @SerializedName("cityName") private String cityName;
    @SerializedName("localityName") private String localityName;
    @SerializedName("clinicLatitude") private String clinicLatitude;
    @SerializedName("clinicLongitude") private String clinicLongitude;
    @SerializedName("doctorID") private String doctorID;
    @SerializedName("doctorDisplayProfile") private String doctorDisplayProfile;
    @SerializedName("doctorPrefix") private String doctorPrefix;
    @SerializedName("doctorName") private String doctorName;
    @SerializedName("doctorExperience") private String doctorExperience;
    @SerializedName("doctorCharges") private String doctorCharges;
    @SerializedName("clinicDistance") private String clinicDistance;
    @SerializedName("doctorVotes") private String doctorVotes;
    @SerializedName("doctorLikes") private String doctorLikes;
    @SerializedName("doctorLikesPercent") private String doctorLikesPercent;
    @SerializedName("currencySymbol") private String currencySymbol;
    @SerializedName("doctorClaimed") private String doctorClaimed;
    @SerializedName("images") private ArrayList<ClinicImage> images;

    public String getClinicID() {
        return clinicID;
    }

    public void setClinicID(String clinicID) {
        this.clinicID = clinicID;
    }

    public String getClinicName() {
        return clinicName;
    }

    public void setClinicName(String clinicName) {
        this.clinicName = clinicName;
    }

    public String getClinicAddress() {
        return clinicAddress;
    }

    public void setClinicAddress(String clinicAddress) {
        this.clinicAddress = clinicAddress;
    }

    public String getClinicPinCode() {
        return clinicPinCode;
    }

    public void setClinicPinCode(String clinicPinCode) {
        this.clinicPinCode = clinicPinCode;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getLocalityName() {
        return localityName;
    }

    public void setLocalityName(String localityName) {
        this.localityName = localityName;
    }

    public String getClinicLatitude() {
        return clinicLatitude;
    }

    public void setClinicLatitude(String clinicLatitude) {
        this.clinicLatitude = clinicLatitude;
    }

    public String getClinicLongitude() {
        return clinicLongitude;
    }

    public void setClinicLongitude(String clinicLongitude) {
        this.clinicLongitude = clinicLongitude;
    }

    public String getDoctorID() {
        return doctorID;
    }

    public void setDoctorID(String doctorID) {
        this.doctorID = doctorID;
    }

    public String getDoctorDisplayProfile() {
        return doctorDisplayProfile;
    }

    public void setDoctorDisplayProfile(String doctorDisplayProfile) {
        this.doctorDisplayProfile = doctorDisplayProfile;
    }

    public String getDoctorPrefix() {
        return doctorPrefix;
    }

    public void setDoctorPrefix(String doctorPrefix) {
        this.doctorPrefix = doctorPrefix;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getDoctorExperience() {
        return doctorExperience;
    }

    public void setDoctorExperience(String doctorExperience) {
        this.doctorExperience = doctorExperience;
    }

    public String getDoctorCharges() {
        return doctorCharges;
    }

    public void setDoctorCharges(String doctorCharges) {
        this.doctorCharges = doctorCharges;
    }

    public String getClinicDistance() {
        return clinicDistance;
    }

    public void setClinicDistance(String clinicDistance) {
        this.clinicDistance = clinicDistance;
    }

    public String getDoctorVotes() {
        return doctorVotes;
    }

    public void setDoctorVotes(String doctorVotes) {
        this.doctorVotes = doctorVotes;
    }

    public String getDoctorLikes() {
        return doctorLikes;
    }

    public void setDoctorLikes(String doctorLikes) {
        this.doctorLikes = doctorLikes;
    }

    public String getDoctorLikesPercent() {
        return doctorLikesPercent;
    }

    public void setDoctorLikesPercent(String doctorLikesPercent) {
        this.doctorLikesPercent = doctorLikesPercent;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    public String getDoctorClaimed() {
        return doctorClaimed;
    }

    public void setDoctorClaimed(String doctorClaimed) {
        this.doctorClaimed = doctorClaimed;
    }

    public ArrayList<ClinicImage> getImages() {
        return images;
    }

    public void setImages(ArrayList<ClinicImage> images) {
        this.images = images;
    }
}