package biz.zenpets.groomers.utils.models.groomers;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Groomers {
    @SerializedName("error") private Boolean error;
    @SerializedName("message") private String message;
    @SerializedName("groomers") private ArrayList<Groomer> groomers = null;

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

    public ArrayList<Groomer> getGroomers() {
        return groomers;
    }

    public void setGroomers(ArrayList<Groomer> groomers) {
        this.groomers = groomers;
    }
}