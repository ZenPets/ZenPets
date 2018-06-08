package biz.zenpets.kennels.utils.models.location;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class CitiesData {

    @SerializedName("error") private Boolean error;
    @SerializedName("cities") private ArrayList<CityData> cities = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public ArrayList<CityData> getCities() {
        return cities;
    }

    public void setCities(ArrayList<CityData> cities) {
        this.cities = cities;
    }
}