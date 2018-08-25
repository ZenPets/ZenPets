package biz.zenpets.kennels.utils.models.inventory;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Types {
    @SerializedName("error") private Boolean error;
    @SerializedName("message") private String message;
    @SerializedName("types") private ArrayList<Type> types = null;

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

    public ArrayList<Type> getTypes() {
        return types;
    }

    public void setTypes(ArrayList<Type> types) {
        this.types = types;
    }
}