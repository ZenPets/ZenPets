package co.zenpets.users.utils.models.adoptions.adoption;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import co.zenpets.users.utils.models.adoptions.promotion.Promotion;

public class Adoptions {

    @SerializedName("error") private Boolean error;
    @SerializedName("totalPages") private Integer totalPages;
    @SerializedName("pageNumber") private Integer pageNumber;
    @SerializedName("totalPromotionPages") private Integer totalPromotionPages;
    @SerializedName("promotionPageNumber") private Integer promotionPageNumber;
    @SerializedName("adoptions") private ArrayList<Adoption> adoptions = null;
    @SerializedName("promotions") private ArrayList<Promotion> promotions = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Integer getTotalPromotionPages() {
        return totalPromotionPages;
    }

    public void setTotalPromotionPages(Integer totalPromotionPages) {
        this.totalPromotionPages = totalPromotionPages;
    }

    public Integer getPromotionPageNumber() {
        return promotionPageNumber;
    }

    public void setPromotionPageNumber(Integer promotionPageNumber) {
        this.promotionPageNumber = promotionPageNumber;
    }

    public ArrayList<Adoption> getAdoptions() {
        return adoptions;
    }

    public void setAdoptions(ArrayList<Adoption> adoptions) {
        this.adoptions = adoptions;
    }

    public ArrayList<Promotion> getPromotions() {
        return promotions;
    }

    public void setPromotions(ArrayList<Promotion> promotions) {
        this.promotions = promotions;
    }
}