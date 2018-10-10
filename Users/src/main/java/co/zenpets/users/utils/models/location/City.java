package co.zenpets.users.utils.models.location;

import com.google.gson.annotations.SerializedName;

public class City {

    @SerializedName("cityID") private String cityID;
    @SerializedName("cityName") private String cityName;
    @SerializedName("stateID") private String stateID;

    public String getCityID() {
        return cityID;
    }

    public void setCityID(String cityID) {
        this.cityID = cityID;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getStateID() {
        return stateID;
    }

    public void setStateID(String stateID) {
        this.stateID = stateID;
    }
}