package co.zenpets.users.utils.models.doctors.modules;

import com.google.gson.annotations.SerializedName;

public class ServicesCount {
    @SerializedName("error") private Boolean error;
    @SerializedName("message") private String message;
    @SerializedName("totalServices") private String totalServices;

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

    public String getTotalServices() {
        return totalServices;
    }

    public void setTotalServices(String totalServices) {
        this.totalServices = totalServices;
    }
}
