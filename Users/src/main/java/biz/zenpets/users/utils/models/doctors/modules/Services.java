package biz.zenpets.users.utils.models.doctors.modules;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Services {

    @SerializedName("error") private Boolean error;
    @SerializedName("services") private ArrayList<Service> services = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public ArrayList<Service> getServices() {
        return services;
    }

    public void setServices(ArrayList<Service> services) {
        this.services = services;
    }
}