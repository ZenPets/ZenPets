package biz.zenpets.users.utils.models.boarding;

import com.google.gson.annotations.SerializedName;

public class Boarding {
    @SerializedName("error") private Boolean error;
    @SerializedName("message") private String message;
    @SerializedName("boardingID") private String boardingID;
    @SerializedName("userID") private String userID;
    @SerializedName("boardingAddress") private String boardingAddress;
    @SerializedName("boardingPincode") private String boardingPincode;
    @SerializedName("boardingLatitude") private String boardingLatitude;
    @SerializedName("boardingLongitude") private String boardingLongitude;
    @SerializedName("boardingDistance") private String boardingDistance;
    @SerializedName("boardingDate") private String boardingDate;
    @SerializedName("boardingActive") private String boardingActive;
    @SerializedName("userName") private String userName;
    @SerializedName("userDisplayProfile") private String userDisplayProfile;
    @SerializedName("userToken") private String userToken;
    @SerializedName("countryID") private String countryID;
    @SerializedName("countryName") private String countryName;
    @SerializedName("stateID") private String stateID;
    @SerializedName("stateName") private String stateName;
    @SerializedName("cityID") private String cityID;
    @SerializedName("cityName") private String cityName;

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

    public String getBoardingID() {
        return boardingID;
    }

    public void setBoardingID(String boardingID) {
        this.boardingID = boardingID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getBoardingAddress() {
        return boardingAddress;
    }

    public void setBoardingAddress(String boardingAddress) {
        this.boardingAddress = boardingAddress;
    }

    public String getBoardingPincode() {
        return boardingPincode;
    }

    public void setBoardingPincode(String boardingPincode) {
        this.boardingPincode = boardingPincode;
    }

    public String getBoardingLatitude() {
        return boardingLatitude;
    }

    public void setBoardingLatitude(String boardingLatitude) {
        this.boardingLatitude = boardingLatitude;
    }

    public String getBoardingLongitude() {
        return boardingLongitude;
    }

    public void setBoardingLongitude(String boardingLongitude) {
        this.boardingLongitude = boardingLongitude;
    }

    public String getBoardingDistance() {
        return boardingDistance;
    }

    public void setBoardingDistance(String boardingDistance) {
        this.boardingDistance = boardingDistance;
    }

    public String getBoardingDate() {
        return boardingDate;
    }

    public void setBoardingDate(String boardingDate) {
        this.boardingDate = boardingDate;
    }

    public String getBoardingActive() {
        return boardingActive;
    }

    public void setBoardingActive(String boardingActive) {
        this.boardingActive = boardingActive;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserDisplayProfile() {
        return userDisplayProfile;
    }

    public void setUserDisplayProfile(String userDisplayProfile) {
        this.userDisplayProfile = userDisplayProfile;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
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
}