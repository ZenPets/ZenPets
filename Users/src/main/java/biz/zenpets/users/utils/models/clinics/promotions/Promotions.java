package biz.zenpets.users.utils.models.clinics.promotions;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Promotions {
    @SerializedName("error") private Boolean error;
    @SerializedName("message") private String message;
    @SerializedName("promotions") private ArrayList<Promotion> promotions = null;

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

    public ArrayList<Promotion> getPromotions() {
        return promotions;
    }

    public void setPromotions(ArrayList<Promotion> promotions) {
        this.promotions = promotions;
    }
}