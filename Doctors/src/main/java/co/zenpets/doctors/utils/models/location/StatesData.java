package co.zenpets.doctors.utils.models.location;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class StatesData {

    @SerializedName("error") private Boolean error;
    @SerializedName("states") private ArrayList<StateData> states = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public ArrayList<StateData> getStates() {
        return states;
    }

    public void setStates(ArrayList<StateData> states) {
        this.states = states;
    }
}