package co.zenpets.groomers.utils.models.enquiries;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Enquiries {
    @SerializedName("error") private Boolean error;
    @SerializedName("enquiries") private ArrayList<Enquiry> enquiries = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public ArrayList<Enquiry> getEnquiries() {
        return enquiries;
    }

    public void setEnquiries(ArrayList<Enquiry> enquiries) {
        this.enquiries = enquiries;
    }
}