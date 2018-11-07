package co.zenpets.doctors.utils.models.clinics.images;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ImageData {

    @SerializedName("imageID") @Expose private String imageID;
    @SerializedName("clinicID") @Expose private String clinicID;
    @SerializedName("imageURL") @Expose private String imageURL;

    public String getImageID() {
        return imageID;
    }

    public void setImageID(String imageID) {
        this.imageID = imageID;
    }

    public String getClinicID() {
        return clinicID;
    }

    public void setClinicID(String clinicID) {
        this.clinicID = clinicID;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}