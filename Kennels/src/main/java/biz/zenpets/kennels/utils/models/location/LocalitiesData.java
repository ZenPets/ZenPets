package biz.zenpets.kennels.utils.models.location;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class LocalitiesData {

    @SerializedName("error") private Boolean error;
    @SerializedName("localities") private ArrayList<LocalityData> localities = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public ArrayList<LocalityData> getLocalities() {
        return localities;
    }

    public void setLocalities(ArrayList<LocalityData> localities) {
        this.localities = localities;
    }
}