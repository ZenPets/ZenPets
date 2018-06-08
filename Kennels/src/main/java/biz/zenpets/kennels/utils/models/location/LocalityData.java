package biz.zenpets.kennels.utils.models.location;

import com.google.gson.annotations.SerializedName;

public class LocalityData {

    @SerializedName("localityID") private String localityID;
    @SerializedName("localityName") private String localityName;
    @SerializedName("cityID") private String cityID;

    public String getLocalityID() {
        return localityID;
    }

    public void setLocalityID(String localityID) {
        this.localityID = localityID;
    }

    public String getLocalityName() {
        return localityName;
    }

    public void setLocalityName(String localityName) {
        this.localityName = localityName;
    }

    public String getCityID() {
        return cityID;
    }

    public void setCityID(String cityID) {
        this.cityID = cityID;
    }
}