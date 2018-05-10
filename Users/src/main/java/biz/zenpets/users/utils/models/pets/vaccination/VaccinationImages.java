package biz.zenpets.users.utils.models.pets.vaccination;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class VaccinationImages {

    @SerializedName("error") private Boolean error;
    @SerializedName("images") private ArrayList<VaccinationImage> images = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public ArrayList<VaccinationImage> getImages() {
        return images;
    }

    public void setImages(ArrayList<VaccinationImage> images) {
        this.images = images;
    }
}