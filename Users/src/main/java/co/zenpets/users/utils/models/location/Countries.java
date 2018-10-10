package co.zenpets.users.utils.models.location;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Countries {

    @SerializedName("error") private Boolean error;
    @SerializedName("countries") private ArrayList<Country> countries = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public ArrayList<Country> getCountries() {
        return countries;
    }

    public void setCountries(ArrayList<Country> countries) {
        this.countries = countries;
    }
}