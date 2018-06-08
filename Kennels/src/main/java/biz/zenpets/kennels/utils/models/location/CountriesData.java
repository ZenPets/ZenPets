package biz.zenpets.kennels.utils.models.location;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class CountriesData {

    @SerializedName("error") private Boolean error;
    @SerializedName("countries") private ArrayList<CountryData> countries = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public ArrayList<CountryData> getCountries() {
        return countries;
    }

    public void setCountries(ArrayList<CountryData> countries) {
        this.countries = countries;
    }
}