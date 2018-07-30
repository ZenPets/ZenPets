package biz.zenpets.kennels.utils.models.statistics;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class KennelViews {
    @SerializedName("error") private Boolean error;
    @SerializedName("totalViews") private String totalViews;
    @SerializedName("views") private ArrayList<KennelView> views = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public String getTotalViews() {
        return totalViews;
    }

    public void setTotalViews(String totalViews) {
        this.totalViews = totalViews;
    }

    public ArrayList<KennelView> getViews() {
        return views;
    }

    public void setViews(ArrayList<KennelView> views) {
        this.views = views;
    }
}