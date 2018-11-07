package co.zenpets.doctors.utils.models.clinics.images;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ImagesData {

    @SerializedName("error") private Boolean error;
    @SerializedName("images") private ArrayList<ImageData> images = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public ArrayList<ImageData> getImages() {
        return images;
    }

    public void setImages(ArrayList<ImageData> images) {
        this.images = images;
    }
}