package co.zenpets.doctors.utils.models.visit;

import com.google.gson.annotations.SerializedName;

public class Reason {

    @SerializedName("visitReasonID") private String visitReasonID;
    @SerializedName("visitReason") private String visitReason;

    public String getVisitReasonID() {
        return visitReasonID;
    }

    public void setVisitReasonID(String visitReasonID) {
        this.visitReasonID = visitReasonID;
    }

    public String getVisitReason() {
        return visitReason;
    }

    public void setVisitReason(String visitReason) {
        this.visitReason = visitReason;
    }
}