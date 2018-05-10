package biz.zenpets.users.utils.models.consultations.views;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ConsultationViews {

    @SerializedName("error") private Boolean error;
    @SerializedName("views") private ArrayList<ConsultationView> views = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public ArrayList<ConsultationView> getViews() {
        return views;
    }

    public void setViews(ArrayList<ConsultationView> views) {
        this.views = views;
    }
}