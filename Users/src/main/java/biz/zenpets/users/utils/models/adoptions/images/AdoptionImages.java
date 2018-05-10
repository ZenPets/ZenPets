package biz.zenpets.users.utils.models.adoptions.images;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class AdoptionImages {

    @SerializedName("error") private Boolean error;
    @SerializedName("images") private ArrayList<AdoptionImage> images = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public ArrayList<AdoptionImage> getImages() {
        return images;
    }

    public void setImages(ArrayList<AdoptionImage> images) {
        this.images = images;
    }
}