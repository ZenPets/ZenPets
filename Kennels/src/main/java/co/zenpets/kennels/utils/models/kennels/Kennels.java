package co.zenpets.kennels.utils.models.kennels;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Kennels {
    @SerializedName("error") private Boolean error;
    @SerializedName("kennels") private ArrayList<Kennel> kennels = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public ArrayList<Kennel> getKennels() {
        return kennels;
    }

    public void setKennels(ArrayList<Kennel> kennels) {
        this.kennels = kennels;
    }
}