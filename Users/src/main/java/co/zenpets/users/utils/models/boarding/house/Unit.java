package co.zenpets.users.utils.models.boarding.house;

import com.google.gson.annotations.SerializedName;

public class Unit {
    @SerializedName("error") private Boolean error;
    @SerializedName("message") private String message;
    @SerializedName("boardingUnitID") private String boardingUnitID;
    @SerializedName("boardingUnitType") private String boardingUnitType;

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
}