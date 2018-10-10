package co.zenpets.kennels.utils.models.statistics;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class KennelViews {
    @SerializedName("error") private Boolean error;
    @SerializedName("totalViews") private String totalViews;
    @SerializedName("totalClicks") private String totalClicks;
    @SerializedName("totalEnquiries") private String totalEnquiries;
    @SerializedName("totalReviews") private String totalReviews;
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

    public String getTotalClicks() {
        return totalClicks;
    }

    public void setTotalClicks(String totalClicks) {
        this.totalClicks = totalClicks;
    }

    public String getTotalEnquiries() {
        return totalEnquiries;
    }

    public void setTotalEnquiries(String totalEnquiries) {
        this.totalEnquiries = totalEnquiries;
    }

    public String getTotalReviews() {
        return totalReviews;
    }

    public void setTotalReviews(String totalReviews) {
        this.totalReviews = totalReviews;
    }

    public ArrayList<KennelView> getViews() {
        return views;
    }

    public void setViews(ArrayList<KennelView> views) {
        this.views = views;
    }
}