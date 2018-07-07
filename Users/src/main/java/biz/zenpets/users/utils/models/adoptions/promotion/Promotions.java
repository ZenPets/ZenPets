package biz.zenpets.users.utils.models.adoptions.promotion;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

class Promotions {

    @SerializedName("error") private Boolean error;
    @SerializedName("promotions") private ArrayList<Promotion> promotions = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public ArrayList<Promotion> getPromotions() {
        return promotions;
    }

    public void setPromotions(ArrayList<Promotion> promotions) {
        this.promotions = promotions;
    }
}