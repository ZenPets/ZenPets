package biz.zenpets.users.utils.models.kennels.statistics;

import com.google.gson.annotations.SerializedName;

public class Stat {
    @SerializedName("error") private Boolean error;
    @SerializedName("message") private String message;
    @SerializedName("kennelID") private String kennelID;
    @SerializedName("userID") private String userID;
    @SerializedName("kennelViewedDate") private String kennelViewedDate;

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

    public String getKennelID() {
        return kennelID;
    }

    public void setKennelID(String kennelID) {
        this.kennelID = kennelID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getKennelViewedDate() {
        return kennelViewedDate;
    }

    public void setKennelViewedDate(String kennelViewedDate) {
        this.kennelViewedDate = kennelViewedDate;
    }
}