package co.zenpets.users.utils.models.doctors.subscription;

import com.google.gson.annotations.SerializedName;

public class SubscriptionData {

    @SerializedName("error") private Boolean error;
    @SerializedName("docSubscriptionID") private String docSubscriptionID;
    @SerializedName("doctorID") private String doctorID;
    @SerializedName("subscriptionID") private String subscriptionID;
    @SerializedName("subscriptionStart") private String subscriptionStart;
    @SerializedName("subscriptionEnd") private String subscriptionEnd;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public String getDocSubscriptionID() {
        return docSubscriptionID;
    }

    public void setDocSubscriptionID(String docSubscriptionID) {
        this.docSubscriptionID = docSubscriptionID;
    }

    public String getDoctorID() {
        return doctorID;
    }

    public void setDoctorID(String doctorID) {
        this.doctorID = doctorID;
    }

    public String getSubscriptionID() {
        return subscriptionID;
    }

    public void setSubscriptionID(String subscriptionID) {
        this.subscriptionID = subscriptionID;
    }

    public String getSubscriptionStart() {
        return subscriptionStart;
    }

    public void setSubscriptionStart(String subscriptionStart) {
        this.subscriptionStart = subscriptionStart;
    }

    public String getSubscriptionEnd() {
        return subscriptionEnd;
    }

    public void setSubscriptionEnd(String subscriptionEnd) {
        this.subscriptionEnd = subscriptionEnd;
    }
}