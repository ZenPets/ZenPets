package co.zenpets.users.utils.models.visit;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Reasons {

    @SerializedName("error") private Boolean error;
    @SerializedName("reasons") private ArrayList<Reason> reasons = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public ArrayList<Reason> getReasons() {
        return reasons;
    }

    public void setReasons(ArrayList<Reason> reasons) {
        this.reasons = reasons;
    }
}