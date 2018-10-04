package biz.zenpets.users.utils.models.groomers.statistics;

import com.google.gson.annotations.SerializedName;

public class GroomerStats {
    @SerializedName("error") private Boolean error;
    @SerializedName("message") private String message;
    @SerializedName("groomerViewID") private String groomerViewID;
    @SerializedName("groomerClickID") private String groomerClickID;
    @SerializedName("groomerID") private String groomerID;
    @SerializedName("userID") private String userID;
    @SerializedName("groomerViewDate") private String groomerViewDate;
    @SerializedName("groomerClickDate") private String groomerClickDate;

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

    public String getGroomerViewID() {
        return groomerViewID;
    }

    public void setGroomerViewID(String groomerViewID) {
        this.groomerViewID = groomerViewID;
    }

    public String getGroomerClickID() {
        return groomerClickID;
    }

    public void setGroomerClickID(String groomerClickID) {
        this.groomerClickID = groomerClickID;
    }

    public String getGroomerID() {
        return groomerID;
    }

    public void setGroomerID(String groomerID) {
        this.groomerID = groomerID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getGroomerViewDate() {
        return groomerViewDate;
    }

    public void setGroomerViewDate(String groomerViewDate) {
        this.groomerViewDate = groomerViewDate;
    }

    public String getGroomerClickDate() {
        return groomerClickDate;
    }

    public void setGroomerClickDate(String groomerClickDate) {
        this.groomerClickDate = groomerClickDate;
    }
}