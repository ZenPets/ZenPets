package biz.zenpets.users.utils.models.clinics.images;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ClinicImages {

    @SerializedName("error") private Boolean error;
    @SerializedName("images") private ArrayList<ClinicImage> images = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public ArrayList<ClinicImage> getImages() {
        return images;
    }

    public void setImages(ArrayList<ClinicImage> images) {
        this.images = images;
    }
}