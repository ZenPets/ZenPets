package co.zenpets.users.utils.models.kennels.bookings;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Units {
    @SerializedName("error") private Boolean error;
    @SerializedName("units") private ArrayList<Unit> units = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public ArrayList<Unit> getUnits() {
        return units;
    }

    public void setUnits(ArrayList<Unit> units) {
        this.units = units;
    }
}