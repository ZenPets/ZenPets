package biz.zenpets.users.utils.models.appointment.client;

import com.google.gson.annotations.SerializedName;

public class Client {

    @SerializedName("error") private Boolean error;
    @SerializedName("message") private String message;
    @SerializedName("clientID") private String clientID;

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

    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }
}