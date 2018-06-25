package biz.zenpets.users.utils.models.adoptions.promotion;

import com.google.gson.annotations.SerializedName;

public class Promotion {
    @SerializedName("promotedID") private String promotedID;
    @SerializedName("adoptionID") private String adoptionID;
    @SerializedName("optionID") private String optionID;
    @SerializedName("paymentID") private String paymentID;
    @SerializedName("promotedFrom") private String promotedFrom;
    @SerializedName("promotedTo") private String promotedTo;
    @SerializedName("promotedTimestamp") private String promotedTimestamp;

    public String getPromotedID() {
        return promotedID;
    }

    public void setPromotedID(String promotedID) {
        this.promotedID = promotedID;
    }

    public String getAdoptionID() {
        return adoptionID;
    }

    public void setAdoptionID(String adoptionID) {
        this.adoptionID = adoptionID;
    }

    public String getOptionID() {
        return optionID;
    }

    public void setOptionID(String optionID) {
        this.optionID = optionID;
    }

    public String getPaymentID() {
        return paymentID;
    }

    public void setPaymentID(String paymentID) {
        this.paymentID = paymentID;
    }

    public String getPromotedFrom() {
        return promotedFrom;
    }

    public void setPromotedFrom(String promotedFrom) {
        this.promotedFrom = promotedFrom;
    }

    public String getPromotedTo() {
        return promotedTo;
    }

    public void setPromotedTo(String promotedTo) {
        this.promotedTo = promotedTo;
    }

    public String getPromotedTimestamp() {
        return promotedTimestamp;
    }

    public void setPromotedTimestamp(String promotedTimestamp) {
        this.promotedTimestamp = promotedTimestamp;
    }
}