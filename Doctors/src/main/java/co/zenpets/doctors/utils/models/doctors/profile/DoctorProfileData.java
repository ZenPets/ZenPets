package co.zenpets.doctors.utils.models.doctors.profile;

import com.google.gson.annotations.SerializedName;

public class DoctorProfileData {

    @SerializedName("error") private Boolean error;
    @SerializedName("doctorID") private String doctorID;
    @SerializedName("doctorAuthID") private String doctorAuthID;
    @SerializedName("doctorPrefix") private String doctorPrefix;
    @SerializedName("doctorName") private String doctorName;
    @SerializedName("doctorEmail") private String doctorEmail;
    @SerializedName("doctorPhonePrefix") private String doctorPhonePrefix;
    @SerializedName("doctorPhoneNumber") private String doctorPhoneNumber;
    @SerializedName("doctorAddress") private String doctorAddress;
    @SerializedName("countryID") private String countryID;
    @SerializedName("countryName") private String countryName;
    @SerializedName("stateID") private String stateID;
    @SerializedName("stateName") private String stateName;
    @SerializedName("cityID") private String cityID;
    @SerializedName("cityName") private String cityName;
    @SerializedName("doctorGender") private String doctorGender;
    @SerializedName("doctorSummary") private String doctorSummary;
    @SerializedName("doctorExperience") private String doctorExperience;
    @SerializedName("doctorCharges") private String doctorCharges;
    @SerializedName("currencySymbol") private String currencySymbol;
    @SerializedName("doctorDisplayProfile") private String doctorDisplayProfile;
    @SerializedName("doctorToken") private String doctorToken;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public String getDoctorID() {
        return doctorID;
    }

    public void setDoctorID(String doctorID) {
        this.doctorID = doctorID;
    }

    public String getDoctorAuthID() {
        return doctorAuthID;
    }

    public void setDoctorAuthID(String doctorAuthID) {
        this.doctorAuthID = doctorAuthID;
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

    public String getDoctorEmail() {
        return doctorEmail;
    }

    public void setDoctorEmail(String doctorEmail) {
        this.doctorEmail = doctorEmail;
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

    public String getDoctorAddress() {
        return doctorAddress;
    }

    public void setDoctorAddress(String doctorAddress) {
        this.doctorAddress = doctorAddress;
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

    public String getDoctorGender() {
        return doctorGender;
    }

    public void setDoctorGender(String doctorGender) {
        this.doctorGender = doctorGender;
    }

    public String getDoctorSummary() {
        return doctorSummary;
    }

    public void setDoctorSummary(String doctorSummary) {
        this.doctorSummary = doctorSummary;
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

    public String getDoctorDisplayProfile() {
        return doctorDisplayProfile;
    }

    public void setDoctorDisplayProfile(String doctorDisplayProfile) {
        this.doctorDisplayProfile = doctorDisplayProfile;
    }

    public String getDoctorToken() {
        return doctorToken;
    }

    public void setDoctorToken(String doctorToken) {
        this.doctorToken = doctorToken;
    }
}