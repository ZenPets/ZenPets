package co.zenpets.users.utils.models.trainers.modules;

import com.google.gson.annotations.SerializedName;

public class ModulesCount {

    @SerializedName("error") private Boolean error;
    @SerializedName("total_modules") private String total_modules;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public String getTotal_modules() {
        return total_modules;
    }

    public void setTotal_modules(String total_modules) {
        this.total_modules = total_modules;
    }
}