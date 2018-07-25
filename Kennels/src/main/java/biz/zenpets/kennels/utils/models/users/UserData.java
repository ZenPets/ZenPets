package biz.zenpets.kennels.utils.models.users;

import com.google.gson.annotations.SerializedName;

public class UserData {
    @SerializedName("error") private Boolean error;
    @SerializedName("userID") private String userID;
    @SerializedName("userAuthID") private String userAuthID;
    @SerializedName("userName") private String userName;
    @SerializedName("userEmail") private String userEmail;
    @SerializedName("userDisplayProfile") private String userDisplayProfile;
    @SerializedName("userPhonePrefix") private String userPhonePrefix;
    @SerializedName("userPhoneNumber") private String userPhoneNumber;
    @SerializedName("userGender") private String userGender;
    @SerializedName("countryID") private String countryID;
    @SerializedName("countryName") private String countryName;
    @SerializedName("stateID") private String stateID;
    @SerializedName("stateName") private String stateName;
    @SerializedName("cityID") private String cityID;
    @SerializedName("cityName") private String cityName;
    @SerializedName("userToken") private String userToken;
    @SerializedName("profileComplete") private String profileComplete;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserAuthID() {
        return userAuthID;
    }

    public void setUserAuthID(String userAuthID) {
        this.userAuthID = userAuthID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserDisplayProfile() {
        return userDisplayProfile;
    }

    public void setUserDisplayProfile(String userDisplayProfile) {
        this.userDisplayProfile = userDisplayProfile;
    }

    public String getUserPhonePrefix() {
        return userPhonePrefix;
    }

    public void setUserPhonePrefix(String userPhonePrefix) {
        this.userPhonePrefix = userPhonePrefix;
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public void setUserPhoneNumber(String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }

    public String getUserGender() {
        return userGender;
    }

    public void setUserGender(String userGender) {
        this.userGender = userGender;
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

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public String getProfileComplete() {
        return profileComplete;
    }

    public void setProfileComplete(String profileComplete) {
        this.profileComplete = profileComplete;
    }
}