package biz.zenpets.users.utils.models.location;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Cities {

    @SerializedName("error") private Boolean error;
    @SerializedName("cities") private ArrayList<City> cities = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public ArrayList<City> getCities() {
        return cities;
    }

    public void setCities(ArrayList<City> cities) {
        this.cities = cities;
    }
}