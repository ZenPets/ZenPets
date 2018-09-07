package biz.zenpets.users.utils.models.doctors.list;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import biz.zenpets.users.utils.models.clinics.images.ClinicImage;
import biz.zenpets.users.utils.models.clinics.promotions.Promotion;

public class Doctor {
    @SerializedName("error") private Boolean error;
    @SerializedName("message") private String message;
    @SerializedName("clinicID") private String clinicID;
    @SerializedName("clinicName") private String clinicName;
    @SerializedName("clinicAddress") private String clinicAddress;
    @SerializedName("countryID") private String countryID;
    @SerializedName("countryName") private String countryName;
    @SerializedName("stateID") private String stateID;
    @SerializedName("stateName") private String stateName;
    @SerializedName("cityID") private String cityID;
    @SerializedName("cityName") private String cityName;
    @SerializedName("localityID") private String localityID;
    @SerializedName("localityName") private String localityName;
    @SerializedName("clinicPinCode") private String clinicPinCode;
    @SerializedName("clinicLatitude") private String clinicLatitude;
    @SerializedName("clinicLongitude") private String clinicLongitude;
    @SerializedName("clinicDistance") private String clinicDistance;
    @SerializedName("clinicLogo") private String clinicLogo;
    @SerializedName("doctorID") private String doctorID;
    @SerializedName("doctorPrefix") private String doctorPrefix;
    @SerializedName("doctorName") private String doctorName;
    @SerializedName("doctorDisplayProfile") private String doctorDisplayProfile;
    @SerializedName("doctorPhonePrefix") private String doctorPhonePrefix;
    @SerializedName("doctorPhoneNumber") private String doctorPhoneNumber;
    @SerializedName("doctorExperience") private String doctorExperience;
    @SerializedName("doctorCharges") private String doctorCharges;
    @SerializedName("currencySymbol") private String currencySymbol;
    @SerializedName("doctorReviews") private String doctorReviews;
    @SerializedName("doctorPositives") private String doctorPositives;
    @SerializedName("doctorVoteStats") private String doctorVoteStats;
    @SerializedName("clinicRating") private String clinicRating;
    @SerializedName("doctorClaimed") private String doctorClaimed;
    @SerializedName("images") private ArrayList<ClinicImage> images;
    @SerializedName("promotions") private ArrayList<Promotion> promotions = new ArrayList<>();

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

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

    public String getCountryID() {
        return countryID;
    }

    public void setCountryID(String countryID) {
        this.countryID = countryID;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getStateID() {
        return stateID;
    }

    public void setStateID(String stateID) {
        this.stateID = stateID;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getCityID() {
        return cityID;
    }

    public void setCityID(String cityID) {
        this.cityID = cityID;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getLocalityID() {
        return localityID;
    }

    public void setLocalityID(String localityID) {
        this.localityID = localityID;
    }

    public String getLocalityName() {
        return localityName;
    }

    public void setLocalityName(String localityName) {
        this.localityName = localityName;
    }

    public String getClinicPinCode() {
        return clinicPinCode;
    }

    public void setClinicPinCode(String clinicPinCode) {
        this.clinicPinCode = clinicPinCode;
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

    public String getClinicDistance() {
        return clinicDistance;
    }

    public void setClinicDistance(String clinicDistance) {
        this.clinicDistance = clinicDistance;
    }

    public String getClinicLogo() {
        return clinicLogo;
    }

    public void setClinicLogo(String clinicLogo) {
        this.clinicLogo = clinicLogo;
    }

    public String getDoctorID() {
        return doctorID;
    }

    public void setDoctorID(String doctorID) {
        this.doctorID = doctorID;
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

    public String getDoctorDisplayProfile() {
        return doctorDisplayProfile;
    }

    public void setDoctorDisplayProfile(String doctorDisplayProfile) {
        this.doctorDisplayProfile = doctorDisplayProfile;
    }

    public String getDoctorPhonePrefix() {
        return doctorPhonePrefix;
    }

    public void setDoctorPhonePrefix(String doctorPhonePrefix) {
        this.doctorPhonePrefix = doctorPhonePrefix;
    }

    public String getDoctorPhoneNumber() {
        return doctorPhoneNumber;
    }

    public void setDoctorPhoneNumber(String doctorPhoneNumber) {
        this.doctorPhoneNumber = doctorPhoneNumber;
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

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    public String getDoctorReviews() {
        return doctorReviews;
    }

    public void setDoctorReviews(String doctorReviews) {
        this.doctorReviews = doctorReviews;
    }

    public String getDoctorPositives() {
        return doctorPositives;
    }

    public void setDoctorPositives(String doctorPositives) {
        this.doctorPositives = doctorPositives;
    }

    public String getDoctorVoteStats() {
        return doctorVoteStats;
    }

    public void setDoctorVoteStats(String doctorVoteStats) {
        this.doctorVoteStats = doctorVoteStats;
    }

    public String getClinicRating() {
        return clinicRating;
    }

    public void setClinicRating(String clinicRating) {
        this.clinicRating = clinicRating;
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

    public ArrayList<Promotion> getPromotions() {
        return promotions;
    }

    public void setPromotions(ArrayList<Promotion> promotions) {
        this.promotions = promotions;
    }
}