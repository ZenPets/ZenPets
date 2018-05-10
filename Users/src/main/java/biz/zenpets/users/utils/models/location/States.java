package biz.zenpets.users.utils.models.location;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class States {

    @SerializedName("error") private Boolean error;
    @SerializedName("states") private ArrayList<State> states = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public ArrayList<State> getStates() {
        return states;
    }

    public void setStates(ArrayList<State> states) {
        this.states = states;
    }
}