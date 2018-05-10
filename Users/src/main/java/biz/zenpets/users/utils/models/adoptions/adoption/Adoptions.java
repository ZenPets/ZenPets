package biz.zenpets.users.utils.models.adoptions.adoption;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Adoptions {

    @SerializedName("error") private Boolean error;
    @SerializedName("adoptions") private ArrayList<Adoption> adoptions = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public ArrayList<Adoption> getAdoptions() {
        return adoptions;
    }

    public void setAdoptions(ArrayList<Adoption> adoptions) {
        this.adoptions = adoptions;
    }
}