package co.zenpets.users.utils.models.boarding.house;

import com.google.gson.annotations.SerializedName;

public class House {
    @SerializedName("error") private Boolean error;
    @SerializedName("message") private String message;
    @SerializedName("detailsID") private String detailsID;
    @SerializedName("boardingID") private String boardingID;
    @SerializedName("userID") private String userID;
    @SerializedName("boardingUnitID") private String boardingUnitID;
    @SerializedName("boardingUnitType") private String boardingUnitType;
    @SerializedName("detailsDog") private String detailsDog;
    @SerializedName("detailsCat") private String detailsCat;
    @SerializedName("detailsSmoking") private String detailsSmoking;
    @SerializedName("detailsVaping") private String detailsVaping;

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

    public String getDetailsID() {
        return detailsID;
    }

    public void setDetailsID(String detailsID) {
        this.detailsID = detailsID;
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

    public String getBoardingUnitID() {
        return boardingUnitID;
    }

    public void setBoardingUnitID(String boardingUnitID) {
        this.boardingUnitID = boardingUnitID;
    }

    public String getBoardingUnitType() {
        return boardingUnitType;
    }

    public void setBoardingUnitType(String boardingUnitType) {
        this.boardingUnitType = boardingUnitType;
    }

    public String getDetailsDog() {
        return detailsDog;
    }

    public void setDetailsDog(String detailsDog) {
        this.detailsDog = detailsDog;
    }

    public String getDetailsCat() {
        return detailsCat;
    }

    public void setDetailsCat(String detailsCat) {
        this.detailsCat = detailsCat;
    }

    public String getDetailsSmoking() {
        return detailsSmoking;
    }

    public void setDetailsSmoking(String detailsSmoking) {
        this.detailsSmoking = detailsSmoking;
    }

    public String getDetailsVaping() {
        return detailsVaping;
    }

    public void setDetailsVaping(String detailsVaping) {
        this.detailsVaping = detailsVaping;
    }
}