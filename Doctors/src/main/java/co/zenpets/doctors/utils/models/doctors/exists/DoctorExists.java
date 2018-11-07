package co.zenpets.doctors.utils.models.doctors.exists;

import com.google.gson.annotations.SerializedName;

public class DoctorExists {

    @SerializedName("error") private Boolean error;
    @SerializedName("message") private String message;

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
}