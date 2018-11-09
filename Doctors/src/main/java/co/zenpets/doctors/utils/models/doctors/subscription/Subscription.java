package co.zenpets.doctors.utils.models.doctors.subscription;

import com.google.gson.annotations.SerializedName;

public class Subscription {
    @SerializedName("error") private Boolean error;
    @SerializedName("message") private String message;
    @SerializedName("subscriptionID") private String subscriptionID;
    @SerializedName("doctorID") private String doctorID;
    @SerializedName("validFrom") private String validFrom;
    @SerializedName("validTo") private String validTo;
    @SerializedName("subscriptionNote") private String subscriptionNote;

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

    public String getSubscriptionID() {
        return subscriptionID;
    }

    public void setSubscriptionID(String subscriptionID) {
        this.subscriptionID = subscriptionID;
    }

    public String getDoctorID() {
        return doctorID;
    }

    public void setDoctorID(String doctorID) {
        this.doctorID = doctorID;
    }

    public String getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(String validFrom) {
        this.validFrom = validFrom;
    }

    public String getValidTo() {
        return validTo;
    }

    public void setValidTo(String validTo) {
        this.validTo = validTo;
    }

    public String getSubscriptionNote() {
        return subscriptionNote;
    }

    public void setSubscriptionNote(String subscriptionNote) {
        this.subscriptionNote = subscriptionNote;
    }
}