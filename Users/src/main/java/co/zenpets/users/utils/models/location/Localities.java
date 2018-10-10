package co.zenpets.users.utils.models.location;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Localities {

    @SerializedName("error") private Boolean error;
    @SerializedName("localities") private ArrayList<Locality> localities = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public ArrayList<Locality> getLocalities() {
        return localities;
    }

    public void setLocalities(ArrayList<Locality> localities) {
        this.localities = localities;
    }
}