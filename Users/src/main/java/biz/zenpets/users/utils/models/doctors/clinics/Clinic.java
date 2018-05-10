package biz.zenpets.users.utils.models.doctors.clinics;

import com.google.gson.annotations.SerializedName;

public class Clinic {

    @SerializedName("clinicID") private String clinicID;
    @SerializedName("countryID") private String countryID;
    @SerializedName("countryName") private String countryName;
    @SerializedName("stateID") private String stateID;
    @SerializedName("stateName") private String stateName;
    @SerializedName("cityID") private String cityID;
    @SerializedName("cityName") private String cityName;
    @SerializedName("localityID") private String localityID;
    @SerializedName("localityName") private String localityName;
    @SerializedName("clinicName") private String clinicName;
    @SerializedName("clinicAddress") private String clinicAddress;
    @SerializedName("clinicLandmark") private String clinicLandmark;
    @SerializedName("clinicPinCode") private String clinicPinCode;
    @SerializedName("clinicLatitude") private String clinicLatitude;
    @SerializedName("clinicLongitude") private String clinicLongitude;
    @SerializedName("clinicLogo") private String clinicLogo;
    @SerializedName("clinicPhone1") private String clinicPhone1;
    @SerializedName("clinicPhone2") private String clinicPhone2;

    public String getClinicID() {
        return clinicID;
    }

    public void setClinicID(String clinicID) {
        this.clinicID = clinicID;
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

    public String getClinicLandmark() {
        return clinicLandmark;
    }

    public void setClinicLandmark(String clinicLandmark) {
        this.clinicLandmark = clinicLandmark;
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

    public String getClinicLogo() {
        return clinicLogo;
    }

    public void setClinicLogo(String clinicLogo) {
        this.clinicLogo = clinicLogo;
    }

    public String getClinicPhone1() {
        return clinicPhone1;
    }

    public void setClinicPhone1(String clinicPhone1) {
        this.clinicPhone1 = clinicPhone1;
    }

    public String getClinicPhone2() {
        return clinicPhone2;
    }

    public void setClinicPhone2(String clinicPhone2) {
        this.clinicPhone2 = clinicPhone2;
    }
}