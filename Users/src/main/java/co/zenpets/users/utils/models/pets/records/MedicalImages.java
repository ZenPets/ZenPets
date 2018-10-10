package co.zenpets.users.utils.models.pets.records;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class MedicalImages {

    @SerializedName("error") private Boolean error;
    @SerializedName("images") private ArrayList<MedicalImage> images = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public ArrayList<MedicalImage> getImages() {
        return images;
    }

    public void setImages(ArrayList<MedicalImage> images) {
        this.images = images;
    }
}