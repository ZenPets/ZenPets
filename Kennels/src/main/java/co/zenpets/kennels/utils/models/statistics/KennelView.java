package co.zenpets.kennels.utils.models.statistics;

import com.google.gson.annotations.SerializedName;

public class KennelView {
    @SerializedName("error") private Boolean error;
    @SerializedName("message") private String message;
    @SerializedName("totalViews") private String totalViews;
    @SerializedName("recordDate") private String recordDate;
    @SerializedName("kennelViews") private String kennelViews;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTotalViews() {
        return totalViews;
    }

    public void setTotalViews(String totalViews) {
        this.totalViews = totalViews;
    }

    public String getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(String recordDate) {
        this.recordDate = recordDate;
    }

    public String getKennelViews() {
        return kennelViews;
    }

    public void setKennelViews(String kennelViews) {
        this.kennelViews = kennelViews;
    }
}